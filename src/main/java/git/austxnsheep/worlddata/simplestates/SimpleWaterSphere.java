package git.austxnsheep.worlddata.simplestates;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public class SimpleWaterSphere {
    private Quaternion rotation;
    private Vector3 location;
    public SimpleWaterSphere(Quaternion rotation, Vector3 location) {
        this.rotation = rotation;
        this.location = location;
    }

    // Getters
    public Quaternion getRotation() {
        return rotation;
    }

    public Vector3 getLocation() {
        return location;
    }

    // Setters
    public void setRotation(Quaternion rotation) {
        this.rotation = rotation;
    }

    public void setLocation(Vector3 location) {
        this.location = location;
    }
}