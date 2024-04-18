package git.austxnsheep.vr;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import git.austxnsheep.Main;
import git.austxnsheep.vr.tracking.VRTracking;
import git.austxnsheep.vr.types.EyePositions;
import git.austxnsheep.vr.utils.VRUtils;
import git.austxnsheep.worlddata.genericclasses.Location;
import org.lwjgl.BufferUtils;
import org.lwjgl.openvr.*;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.openvr.VR.*;
import static org.lwjgl.openvr.VRSystem.VRSystem_GetRecommendedRenderTargetSize;
import static org.lwjgl.openvr.VRSystem.VRSystem_GetStringTrackedDeviceProperty;

public class VRManager {
    public static boolean vrInitialized;
    public static int recommendedWidth;
    public static int recommendedHeight;
    public static void initializeOpenVR() {
        try {
            MemoryStack stack = MemoryStack.create().push();
            // Initialize OpenVR
            IntBuffer peError = stack.mallocInt(1);
            int token = VR_InitInternal(peError, VR.EVRApplicationType_VRApplication_Scene);
            if (peError.get(0) == 0) {
                OpenVR.create(token);

                System.out.println("Model Number : " + VRSystem_GetStringTrackedDeviceProperty(
                        k_unTrackedDeviceIndex_Hmd, ETrackedDeviceProperty_Prop_ModelNumber_String, peError));
                System.out.println("Serial Number: " + VRSystem_GetStringTrackedDeviceProperty(
                        k_unTrackedDeviceIndex_Hmd, ETrackedDeviceProperty_Prop_SerialNumber_String, peError));

                IntBuffer w = stack.mallocInt(1);
                IntBuffer h = stack.mallocInt(1);
                VRSystem_GetRecommendedRenderTargetSize(w, h);
                System.out.println("Recommended width : " + w.get(0));
                System.out.println("Recommended height: " + h.get(0));

            } else {
                System.out.println("INIT ERROR SYMBOL: " + VR_GetVRInitErrorAsSymbol(peError.get(0)));
                System.out.println("INIT ERROR  DESCR: " + VR_GetVRInitErrorAsEnglishDescription(peError.get(0)));
                System.exit(-1);
            }

            setupRenderTargetSize();

        } catch (Exception e) {
            System.err.println("Failed to initialize OpenVR: " + e.getMessage());
            // Handle initialization failure
        }
    }
    public static void shutdown() {
        if (!vrInitialized) {
            System.out.println("OpenVR was not initialized or already shut down.");
            return;
        }

        VR.VR_ShutdownInternal();
        vrInitialized = false;
        System.out.println("OpenVR shut down successfully.");
    }
    public static Location updateCameraFromPose() {
        Matrix4 headsetPose = VRTracking.getHeadsetPoseMatrix();
        Vector3 headsetPosition = new Vector3();
        Quaternion headsetRotation = new Quaternion();

        headsetPose.getTranslation(headsetPosition);
        headsetPose.getRotation(headsetRotation, true);

        Vector3 newDirection = new Vector3(Vector3.Z).mul(headsetRotation);
        Vector3 newUp = new Vector3(Vector3.Y).mul(headsetRotation);

        return new Location(headsetPosition, newDirection, newUp, headsetRotation);
    }
    public static EyePositions getEyePositions(Matrix4 headsetPoseMatrix) {
        // Get eye to head transform matrices
        HmdMatrix34 leftEyeToHeadTransform = getEyeToHeadTransform(0); // 0 for left eye
        HmdMatrix34 rightEyeToHeadTransform = getEyeToHeadTransform(1); // 1 for right eye

        // Convert HmdMatrix34 to your engine's matrix format if necessary
        Matrix4 leftEyeTransform = VRUtils.convertToMatrix4(leftEyeToHeadTransform);
        Matrix4 rightEyeTransform = VRUtils.convertToMatrix4(rightEyeToHeadTransform);

        // Apply the transformations to the headset's position to get the eye positions
        // This step assumes you have a method to apply a Matrix4 transformation to a Vector3
        // and that your headsetPoseMatrix is the current pose of the headset in world space.
        Vector3 leftEyePosition = applyTransformation(headsetPoseMatrix, leftEyeTransform);
        Vector3 rightEyePosition = applyTransformation(headsetPoseMatrix, rightEyeTransform);

        return new EyePositions(leftEyePosition, rightEyePosition);
    }
    public static void setupRenderTargetSize() {
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        VRSystem_GetRecommendedRenderTargetSize(width, height);
        recommendedWidth = width.get();
        recommendedHeight = height.get();
        System.out.println("Recommended Render Target Size: " + recommendedWidth + " x " + recommendedHeight);
    }
    public static HmdMatrix34 getEyeToHeadTransform(int eye) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            HmdMatrix34 eyeToHeadTransform = HmdMatrix34.mallocStack(stack);
            VRSystem.VRSystem_GetEyeToHeadTransform(eye, eyeToHeadTransform);
            return eyeToHeadTransform;
        }
    }
    public static HmdMatrix44 getProjectionMatrix(int eye, float nearClip, float farClip) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            HmdMatrix44 projectionMatrix = HmdMatrix44.mallocStack(stack);
            VRSystem.VRSystem_GetProjectionMatrix(eye, nearClip, farClip, projectionMatrix);
            return projectionMatrix;
        }
    }
    public static Vector3 applyTransformation(Matrix4 basePose, Matrix4 eyeOffset) {
        // Combine the base pose with the eye offset transformation.
        // This results in a transformation matrix that represents the eye's position and orientation in world space.
        Matrix4 eyeWorldTransform = new Matrix4(basePose).mul(eyeOffset);

        return new Vector3(eyeWorldTransform.getTranslation(new Vector3()));
    }
    public static Matrix4 getHeadsetPos() {
        if (!vrInitialized) {
            System.err.println("OpenVR is not initialized.");
            return null;
        }

        // Assuming VRTracking.getHeadsetPoseMatrix() is a method that retrieves
        // the current pose matrix of the headset. You'll need to implement or
        // ensure this method exists based on your tracking update loop.
        Matrix4 headsetPose = VRTracking.getHeadsetPoseMatrix();

        if (headsetPose != null) {
            // Directly return the Matrix4 representing the headset's full pose
            return headsetPose;
        } else {
            System.err.println("Failed to obtain headset pose.");
            return null;
        }
    }
}
