package git.austxnsheep.network.listeners;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import git.austxnsheep.network.packets.post.ActorLocationPacket;
import git.austxnsheep.network.packets.post.WorldDataPacket;
import git.austxnsheep.worlddata.World;
import git.austxnsheep.worlddata.objectdata.ObjectConversionFactory;
import git.austxnsheep.worlddata.simplestates.SimplePhysicsInstance;

public class ClientListener extends Listener implements ObjectConversionFactory {
    @Override
    public void received(Connection connection, Object object) {
        if (object instanceof WorldDataPacket packet) {
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    World.staticInstances.clear();
                    // Convert simpleInstance to ModelInstance here
                    for (SimplePhysicsInstance instance : packet.objectsData) {
                        ModelInstance modelInstance = createModelInstanceFromSimple(instance);
                        World.staticInstances.add(modelInstance);
                        // Assuming you have a mechanism to add this modelInstance to your rendering list
                    }
                }
            });
        } else if (object instanceof ActorLocationPacket packet) {
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    World.dynamicInstances.clear();
                    // Convert simpleInstance to ModelInstance here
                    for (SimplePhysicsInstance instance : packet.objectsData) {
                        ModelInstance modelInstance = createModelInstanceFromSimple(instance);
                        World.dynamicInstances.add(modelInstance);
                        // Assuming you have a mechanism to add this modelInstance to your rendering list
                    }
                }
            });
        }
    }

}
