package git.austxnsheep.network.packets.post;

import com.badlogic.gdx.math.Vector3;

public class PlayerUpdatePacket {
    public Vector3 loc;
    public PlayerUpdatePacket() {}
    public PlayerUpdatePacket(Vector3 loc) {
        this.loc = loc;
    }
}
