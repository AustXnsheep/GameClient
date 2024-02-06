package git.austxnsheep.network.packets.post;

import git.austxnsheep.worlddata.simplestates.SimplePhysicsInstance;

import java.util.List;

public class WorldDataPacket {
    // This simplified representation includes only the data necessary for the client
    public List<SimplePhysicsInstance> objectsData;
    public WorldDataPacket() {}

    public WorldDataPacket(List<SimplePhysicsInstance> objectsData) {
        this.objectsData = objectsData;
    }
}

