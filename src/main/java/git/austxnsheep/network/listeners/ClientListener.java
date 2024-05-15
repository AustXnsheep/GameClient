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
                ModelLoader loader = new G3dModelLoader(new JsonReader());
                Model model = loader.loadModel(Gdx.files.internal("Assets/sword.G3DJ"));
                ModelInstance instance = new ModelInstance(model);
                World.staticInstances.add(instance);
            });
        } else if (object instanceof ActorLocationPacket packet) {
            Gdx.app.postRunnable(() -> {
                // Log outside the immediate loop to reduce overhead

                // Initialize instancesMap outside of executor tasks to minimize map creations
                Map<UUID, ModelStack> instancesMap = World.dynamicInstances.stream()
                        .collect(Collectors.toMap(modelStack -> modelStack.ID, Function.identity()));
                for (SimpleEntity instance : packet.objectsData) {
                    Main.getLogger().info("Actor Packet Size:" + packet.objectsData.size());
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

                                // Interpolation the existing entities
                                interpolateWorld(packet);

                            } else if (instance instanceof SimpleBlockMan blockMan) {
                                if (modelStack == null) {
                                    // Model loading optimization: Consider caching models outside of this loop
                                    ModelLoader loader = new G3dModelLoader(new JsonReader());
                                    Model model = loader.loadModel(Gdx.files.internal("Assets/sword.G3DJ"));
                                    ModelInstance modelInstance = new ModelInstance(model);
                                    modelStack = new ModelStack(modelInstance, null);
                                    modelStack.ID = uuid;
                                    synchronized (World.dynamicInstances) {
                                        World.dynamicInstances.add(modelStack);
                                    }
                                }

                                // Direct position and orientation setting
                                modelStack.transform.setTranslation(blockMan.location);
                                modelStack.transform.set(blockMan.orientation);
                            }

                            // Schedule the task immediately or with a slight delay if necessary
                        }
                    }
            });
        } else if (object instanceof SoundPacket packet) {
            //Sound sound, Vector2 initialSoundPosition, Vector2 listenerPosition, float maxHearingDistance
            /*
            if (!Main.developerMode) {
                new SoundStack(Main.assetManager.get(Main.assetRoot + packet.soundID), packet.initialSoundPosition, new Vector2(Main.leftEye.position.x, Main.leftEye.position.y), packet.maxHearingDistance);
            } else {
                new SoundStack(Main.assetManager.get(Main.assetRoot + packet.soundID), packet.initialSoundPosition, new Vector2(Main.camera.position.x, Main.camera.position.y), packet.maxHearingDistance);}

             */
        }
    }
    public static void interpolateWorld(ActorLocationPacket packet) {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(Math.min(packet.objectsData.size(), Runtime.getRuntime().availableProcessors()));
        Runnable task = () -> {
            for (ModelStack modelStack : World.dynamicInstances) {
                Vector3 currentPosition = new Vector3();
                modelStack.transform.getTranslation(currentPosition);
                Vector3 targetPosition = modelStack.velocity;
                float alpha = 0.006f;
                Vector3 interpolatedPosition = currentPosition.lerp(targetPosition, alpha);
                modelStack.transform.setTranslation(interpolatedPosition);
            }
        };
        executor.schedule(task, 6, TimeUnit.MILLISECONDS);
        executor.shutdown();
    }

}
