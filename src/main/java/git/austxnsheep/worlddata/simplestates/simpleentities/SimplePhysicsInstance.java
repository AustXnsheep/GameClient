package git.austxnsheep.worlddata.simplestates.simpleentities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import git.austxnsheep.worlddata.simplestates.SimpleEntity;

public class SimplePhysicsInstance extends SimpleEntity {
    private Vector3 deltaDirection;
    private boolean isStatic;
    private Color color;
    private float width, height, depth; // Dimensions for box shapes
    private float mass;
    private int state;

    // Constructor including Quaternion for rotation
    public SimplePhysicsInstance() {}
    public SimplePhysicsInstance(Vector3 position, Vector3 deltaDirection, Quaternion rotation, boolean isStatic, Color color, float width, float height, float depth, float mass, int state) {
        super(position, rotation);
        this.isStatic = isStatic;
        this.color = color;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.mass = mass;
        this.state = state;
        this.deltaDirection = deltaDirection;
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
