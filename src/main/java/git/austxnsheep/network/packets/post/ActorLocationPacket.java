package git.austxnsheep.network.packets.post;

import git.austxnsheep.worlddata.simplestates.SimplePhysicsInstance;

import java.util.List;

public class ActorLocationPacket {
    public List<SimplePhysicsInstance> objectsData;
    public ActorLocationPacket() {}
    public ActorLocationPacket(List<SimplePhysicsInstance> objectsData) {
        this.objectsData = objectsData;
    }
}
