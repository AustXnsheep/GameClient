package git.austxnsheep.types;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public abstract class Entity {
    protected Vector3 position;
    protected com.badlogic.gdx.math.Quaternion orientation;
    protected int health;
    protected int defense;
    protected EntityType type;

    public Entity() {
        position = new Vector3();
        orientation = new com.badlogic.gdx.math.Quaternion();
        health = 100;
        defense = 0;
        type = EntityType.PLAYER;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public EntityType getType() {
        return type;
    }

    public void setType(EntityType type) {
        this.type = type;
    }
    public void setPosition(Vector3 loc) {
        this.position = loc;
    }
    public Vector3 getPosition() {
        return this.position;
    }
    public com.badlogic.gdx.math.Quaternion getOrientation() {
        return orientation;
    }
    public void setOrientation(Quaternion newor) {
        this.orientation = newor;
    }

    public abstract void update();
}
