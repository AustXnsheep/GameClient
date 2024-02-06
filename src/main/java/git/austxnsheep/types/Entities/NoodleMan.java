package git.austxnsheep.types.Entities;

import git.austxnsheep.types.Entity;
import git.austxnsheep.worlddata.objectdata.ObjectFactory;

public class NoodleMan extends Entity implements ObjectFactory {
    @Override
    public void update() {

    }
        /*
    public PhysicsModelInstance body;
    public PhysicsModelInstance head;
    public NoodleMan(Vector3 loc) {
        super.setPosition(loc);
        body = createPhysicsCube(1f, 5f, 1f, Color.GREEN, new Vector3(1f, 1f, 1f));
        Main.world.instances.add(body);
        World.dynamicsWorld.addRigidBody(body.body);
        body.setMass(1);

        head = createPhysicsCube(1f, 1f, 1f, Color.GREEN, new Vector3(1f, 1f, 1f));
        World.instances.add(head);
        World.dynamicsWorld.addRigidBody(head.body);
        head.setMass(1);

    }
    @Override
    public void update() {
        // Update the RigidBodies
        updateRigidBody(body.body, getPosition());
        updateRigidBody(head.body, new Vector3(getPosition().x, getPosition().y - 1, getPosition().z));

        body.updateModelInstanceTransform();
        head.updateModelInstanceTransform();
    }
    public Quaternion getOrientationFromModelInstance(PhysicsModelInstance instance) {
        Quaternion orientation = new Quaternion();
        instance.transform.getRotation(orientation, true);
        return orientation;
    }
    private void updateRigidBody(btRigidBody rigidBody, Vector3 position) {
        if (rigidBody != null) {
            Matrix4 transform = new Matrix4();
            transform.setTranslation(position);
            rigidBody.setWorldTransform(transform);
            rigidBody.activate();
        }
    }
    public void lookAt(Vector3 sourcePosition, Quaternion sourceOrientation, Vector3 targetPosition) {
        Vector3 direction = new Vector3(targetPosition).sub(sourcePosition).nor();

        Quaternion lookRotation = new Quaternion().setFromCross(new Vector3(0, 0, 1), direction);

        sourceOrientation.set(lookRotation);
    }
    public Vector3 getHeadPosition() {
        Vector3 position = new Vector3();
        head.transform.getTranslation(position);
        return position;
    }
    public Quaternion getHeadOrientation() {
        Quaternion orientation = new Quaternion();
        head.transform.getRotation(orientation, true);
        return orientation;
    }
    */
}

