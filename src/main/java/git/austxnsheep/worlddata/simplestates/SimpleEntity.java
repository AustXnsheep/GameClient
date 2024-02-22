package git.austxnsheep.worlddata.simplestates;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public class SimpleEntity {
    public Vector3 location;
    public Quaternion orientation;
    public SimpleEntity() {
        this.location = new Vector3(0, 0, 0);
        this.orientation = new Quaternion();
    }
    public SimpleEntity(Vector3 location, Quaternion orientation) {
        this.location = location;
        this.orientation = orientation;
    }


    public Vector3 getPosition() {
        return location;
    }

    public void setPosition(Vector3 position) {
        this.location = position;
    }

    public Quaternion getRotation() {
        return orientation;
    }

    public void setRotation(Quaternion rotation) {
        this.orientation = rotation;
    }
}
