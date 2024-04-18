package git.austxnsheep.vr.types;

import com.badlogic.gdx.math.Vector3;

public class EyePositions {
    public Vector3 leftEyePosition;
    public Vector3 rightEyePosition;

    public EyePositions(Vector3 leftEye, Vector3 rightEye) {
        this.leftEyePosition = leftEye;
        this.rightEyePosition = rightEye;
    }
}
