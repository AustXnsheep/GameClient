package git.austxnsheep.worlddata.genericclasses;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public class Location {
    public Vector3 position;
    public Vector3 direction;
    public Vector3 up;
    public Quaternion rotation;

    public Location(Vector3 position, Quaternion rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    // Copy constructor
    public Location(Location other) {
        this.position = new Vector3(other.position);
        this.rotation = new Quaternion(other.rotation);
    }

    public Location(Vector3 position) {
        this.position = position;
        this.rotation = new Quaternion();
    }
    public Location(Vector3 position, Vector3 direction, Vector3 up, Quaternion rotation) {
        this.position = position;
        this.direction = direction;
        this.up = up;
        this.rotation = rotation;
    }
}
