package git.austxnsheep.network.packets.post;

import git.austxnsheep.worlddata.simplestates.SimpleEntity;
import git.austxnsheep.worlddata.simplestates.simpleentities.SimplePhysicsInstance;

import java.util.List;

public class ActorLocationPacket {
    public List<SimpleEntity> objectsData;
    public ActorLocationPacket() {}
    public ActorLocationPacket(List<SimpleEntity> objectsData) {
        this.objectsData = objectsData;
    }
}
