package git.austxnsheep.types.Entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import git.austxnsheep.types.Entity;
import git.austxnsheep.worlddata.objectdata.ObjectFactory;
import org.lwjgl.openvr.HmdMatrix34;
import org.lwjgl.openvr.TrackedDevicePose;
import org.lwjgl.openvr.VR;
import org.lwjgl.util.vector.Quaternion;

import java.nio.FloatBuffer;


public class Player extends Entity implements ObjectFactory {
    private ModelInstance model;
    private PerspectiveCamera camera;
    private ModelInstance head;
    private ModelBatch modelBatch;

    public Player(ModelInstance model) {
        this.model = model;
        modelBatch = new ModelBatch();
        Model cubeModel = modelBuilder.createBox(1, 1, 1,
                new Material(ColorAttribute.createDiffuse(Color.YELLOW)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        head = new ModelInstance(cubeModel);
    }

    @Override
    public void update() {
        /*
        TrackedDevicePose.Buffer poses = HeadsetInfo.getDevicePose();
        TrackedDevicePose hmdPose = poses.get(VR.k_unTrackedDeviceIndex_Hmd);

        if (hmdPose.bPoseIsValid()) {
            // Update position and orientation based on VR headset pose
            updateFromPose(hmdPose);

        }

         */
    }
        /*

    private void updateFromPose(TrackedDevicePose hmdPose) {
        position.set(convertPosition(hmdPose));
        orientation.set(convertOrientation(hmdPose));

        if (model != null) {
            head.transform.setToTranslation(position);
            head.transform.set(orientation.getX(), orientation.getY(), orientation.getZ(), orientation.getW());
        }
    }

    private Vector3 convertPosition(TrackedDevicePose hmdPose) {
        HmdMatrix34 matrix = hmdPose.mDeviceToAbsoluteTracking();

        FloatBuffer buffer = matrix.m();

        float x = buffer.get(12);
        float y = buffer.get(13);
        float z = buffer.get(14);

        return new Vector3(x, y, z);
    }

    private Quaternion convertOrientation(TrackedDevicePose hmdPose) {
        HmdMatrix34 hmdMatrix = hmdPose.mDeviceToAbsoluteTracking();
        FloatBuffer buffer = hmdMatrix.m();

        // Create an empty Matrix4
        Matrix4 rotationMatrix = new Matrix4();

        rotationMatrix.set(new float[]{
                buffer.get(0), buffer.get(4), buffer.get(8), 0.0f,
                buffer.get(1), buffer.get(5), buffer.get(9), 0.0f,
                buffer.get(2), buffer.get(6), buffer.get(10), 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f
        });

        return matrixToQuaternion(rotationMatrix);
    }

    private Quaternion matrixToQuaternion(Matrix4 matrix) {
        Quaternion q = new Quaternion();
        q.w = (float) Math.sqrt(Math.max(0, 1 + matrix.val[Matrix4.M00] + matrix.val[Matrix4.M11] + matrix.val[Matrix4.M22])) / 2;
        q.x = (float) Math.sqrt(Math.max(0, 1 + matrix.val[Matrix4.M00] - matrix.val[Matrix4.M11] - matrix.val[Matrix4.M22])) / 2;
        q.y = (float) Math.sqrt(Math.max(0, 1 - matrix.val[Matrix4.M00] + matrix.val[Matrix4.M11] - matrix.val[Matrix4.M22])) / 2;
        q.z = (float) Math.sqrt(Math.max(0, 1 - matrix.val[Matrix4.M00] - matrix.val[Matrix4.M11] + matrix.val[Matrix4.M22])) / 2;
        q.x *= Math.signum(q.x * (matrix.val[Matrix4.M21] - matrix.val[Matrix4.M12]));
        q.y *= Math.signum(q.y * (matrix.val[Matrix4.M02] - matrix.val[Matrix4.M20]));
        q.z *= Math.signum(q.z * (matrix.val[Matrix4.M10] - matrix.val[Matrix4.M01]));

        return q;
    }

    public void render() {
        if (model != null) {
            modelBatch.begin(camera);
            modelBatch.render(model);
            modelBatch.end();
        }
    }
         */
}
