package git.austxnsheep.network.listeners;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.JsonReader;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import git.austxnsheep.Main;
import git.austxnsheep.network.packets.post.ActorLocationPacket;
import git.austxnsheep.network.packets.post.WorldDataPacket;
import git.austxnsheep.worlddata.World;
import git.austxnsheep.worlddata.objectdata.ObjectConversionFactory;
import git.austxnsheep.worlddata.simplestates.SimpleEntity;
import git.austxnsheep.worlddata.simplestates.simpleentities.SimpleBlockMan;
import git.austxnsheep.worlddata.simplestates.simpleentities.SimplePhysicsInstance;

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
                World.dynamicInstances.clear();
                // Convert simpleInstance to ModelInstance here
                for (SimpleEntity instance : packet.objectsData) {
                    if (instance instanceof SimplePhysicsInstance) {
                        ModelInstance modelInstance = createModelInstanceFromSimple((SimplePhysicsInstance) instance);
                        World.dynamicInstances.add(modelInstance);
                        // Assuming you have a mechanism to add this modelInstance to your rendering list
                    } else if (instance instanceof SimpleBlockMan blockMan) {
                        ModelLoader loader = new G3dModelLoader(new JsonReader());
                        Model model = loader.loadModel(Gdx.files.internal("Assets/sword.G3DJ"));
                        ModelInstance modelInstance = new ModelInstance(model);
                        modelInstance.transform.setTranslation(blockMan.location);
                        modelInstance.transform.set(blockMan.orientation);
                        World.staticInstances.add(modelInstance);
                    }
                }
            });
        }
    }

}
