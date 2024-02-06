package git.austxnsheep.worlddata.simplestates;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public class SimplePhysicsInstance {
    private Vector3 position;
    private Quaternion rotation;
    private boolean isStatic;
    private Color color;
    private float width, height, depth; // Dimensions for box shapes
    private float mass;
    private int state;

    // Constructor including Quaternion for rotation
    public SimplePhysicsInstance() {}
    public SimplePhysicsInstance(Vector3 position, Quaternion rotation, boolean isStatic, Color color, float width, float height, float depth, float mass, int state) {
        this.position = position;
        this.rotation = rotation;
        this.isStatic = isStatic;
        this.color = color;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.mass = mass;
        this.state = state;
    }

    // Getters and Setters
    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getDepth() {
        return depth;
    }

    public Vector3 getPosition() {
        return position;
    }

    public void setPosition(Vector3 position) {
        this.position = position;
    }

    public Quaternion getRotation() {
        return rotation;
    }

    public void setRotation(Quaternion rotation) {
        this.rotation = rotation;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void setStatic(boolean isStatic) {
        this.isStatic = isStatic;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }

    public void getState(int state) {
        this.state = state;
    }
    public int getState() {
        return this.state;
    }
}
