package git.austxnsheep.network.packets.requests;

import com.badlogic.gdx.math.Vector2;

public class SoundPacket {
    //Sound sound, Vector2 initialSoundPosition, Vector2 listenerPosition, float maxHearingDistance
    public Vector2 initialSoundPosition;
    public String soundID;
    public int maxHearingDistance;
    public SoundPacket() {}
}
