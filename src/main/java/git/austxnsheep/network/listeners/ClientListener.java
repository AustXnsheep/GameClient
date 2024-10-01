package git.austxnsheep.network.listeners;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.JsonReader;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import git.austxnsheep.Main;
import git.austxnsheep.Sound.SoundStack;
import git.austxnsheep.network.packets.post.ActorLocationPacket;
import git.austxnsheep.network.packets.post.JoinResponsePacket;
import git.austxnsheep.network.packets.post.WorldDataPacket;
import git.austxnsheep.network.packets.requests.SoundPacket;
import git.austxnsheep.worlddata.World;
import git.austxnsheep.worlddata.objectdata.ObjectConversionFactory;
import git.austxnsheep.worlddata.objecttypes.ModelStack;
import git.austxnsheep.worlddata.simplestates.SimpleEntity;
import git.austxnsheep.worlddata.simplestates.simpleentities.SimpleBlockMan;
import git.austxnsheep.worlddata.simplestates.simpleentities.SimplePhysicsInstance;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ClientListener extends Listener implements ObjectConversionFactory {
    private final ModelLoader<ModelLoader.ModelParameters> modelLoader = new G3dModelLoader(new JsonReader());
    private final Model defaultModel = modelLoader.loadModel(Gdx.files.internal("Assets/sword.G3DJ"));

    public static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Override
    public void received(Connection connection, Object object) {
        if (object instanceof WorldDataPacket packet) {
            Gdx.app.postRunnable(() -> {
                World.staticInstances.clear();
                // Convert simpleInstance to ModelInstance here
                for (SimplePhysicsInstance instance : packet.objectsData) {
                    ModelInstance modelInstance = createModelInstanceFromSimple(instance);
                    World.staticInstances.add(modelInstance);
                    // Assuming you have a mechanism to add this modelInstance to your rendering list
                }
                ModelInstance instance = new ModelInstance(defaultModel);
                World.staticInstances.add(instance);
            });
        } else if (object instanceof ActorLocationPacket packet) {
            Gdx.app.postRunnable(() -> {
                // Log outside the immediate loop to reduce overhead

                // Initialize instancesMap outside of executor tasks to minimize map creations
                Map<UUID, ModelStack> instancesMap = World.dynamicInstances.stream()
                        .collect(Collectors.toMap(modelStack -> modelStack.ID, Function.identity()));
                for (SimpleEntity instance : packet.objectsData) {
                    if (connection != null) {
                        UUID uuid = instance.getUuid();
                        ModelStack modelStack = instancesMap.get(uuid);
                        if (instance instanceof SimplePhysicsInstance simplePhysicsInstance) {
                            // Simplified logging and condition checks
                            if (modelStack == null) {
                                modelStack = createModelInstanceFromSimple(simplePhysicsInstance);
                                World.dynamicInstances.add(modelStack);
                            } else {
                                modelStack.transform.set(instance.getPosition(), instance.getRotation());
                            }
                            // Interpolation of the existing entities
                            // Disabled until I fix whatever the problem is.
                            //interpolateWorld(packet);

                            } else if (instance instanceof SimpleBlockMan blockMan) {
                                if (modelStack == null) {
                                    ModelInstance modelInstance = new ModelInstance(defaultModel);
                                    modelStack = new ModelStack(modelInstance, uuid);
                                    synchronized (World.dynamicInstances) {
                                        World.dynamicInstances.add(modelStack);
                                    }
                                }

                                // Direct position and orientation setting
                                modelStack.transform.setTranslation(blockMan.location);
                                modelStack.transform.set(blockMan.orientation);
                            }
                        }
                    }
            });
        } else if (object instanceof SoundPacket packet) {
            //Sound sound, Vector2 initialSoundPosition, Vector2 listenerPosition, float maxHearingDistance
            Main.getLogger().info("Attempting to play sound.");
            if (Main.developerMode) {
                new SoundStack(Main.assetManager.get(Main.assetRoot + packet.soundID), packet.initialSoundPosition, new Vector2(Main.camera.position.x, Main.camera.position.y), packet.maxHearingDistance);
            } else {
                new SoundStack(Main.assetManager.get(Main.assetRoot + packet.soundID), packet.initialSoundPosition, new Vector2(Main.leftEye.position.x, Main.leftEye.position.y), packet.maxHearingDistance);
            }

        } else if (object instanceof JoinResponsePacket packet) {
            Main.serverIP = packet.serverIP;
            Main.softwareType = packet.softwareType;
            Main.isConnectedToServer = true;
        }
    }
    public static void interpolateWorld(ActorLocationPacket packet) {
        scheduler.schedule(() -> {
            synchronized (World.dynamicInstances) { // Thread-safe access
                for (ModelStack modelStack : World.dynamicInstances) {
                    if (containsUUID(modelStack.ID, packet)) {
                        if (modelStack.velocity != null) {
                            Vector3 currentPosition = new Vector3();
                            modelStack.transform.getTranslation(currentPosition);
                            Vector3 targetPosition = modelStack.velocity;
                            float alpha = 0.006f;
                            currentPosition.lerp(targetPosition, alpha);  // currentPosition is now the interpolated position
                            modelStack.transform.setTranslation(currentPosition);
                        } else {
                            Main.getLogger().severe("Failed to interpolate: Velocity is null.");
                        }
                    } else {
                        Main.getLogger().severe("Failed to interpolate an object: UUID, Object, or packet could be null.");
                    }
                }
            }
        }, 6, TimeUnit.MILLISECONDS);
    }
    public static void shutdownScheduler() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    public static boolean containsUUID(UUID uuid, ActorLocationPacket packet) {
        for (SimpleEntity entity : packet.objectsData) {
            if (entity.getUuid() == uuid) {
                return true;
            }
        }
        return false;
    }
}
