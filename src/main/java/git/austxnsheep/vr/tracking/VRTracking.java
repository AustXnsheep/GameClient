package git.austxnsheep.vr.tracking;

import com.badlogic.gdx.math.Matrix4;
import git.austxnsheep.vr.utils.VRUtils;
import org.lwjgl.openvr.TrackedDevicePose;
import org.lwjgl.openvr.VR;
import org.lwjgl.openvr.VRSystem;

public class VRTracking {
    public static TrackedDevicePose.Buffer getDevicePoses() {
        TrackedDevicePose.Buffer trackedDevicePoses = TrackedDevicePose.create(VR.k_unMaxTrackedDeviceCount);
        VRSystem.VRSystem_GetDeviceToAbsoluteTrackingPose(VR.ETrackingUniverseOrigin_TrackingUniverseStanding, 0, trackedDevicePoses);
        return trackedDevicePoses;
    }

    public static Matrix4 getHeadsetPoseMatrix() {
        TrackedDevicePose.Buffer poses = getDevicePoses();
        TrackedDevicePose hmdPose = poses.get(VR.k_unTrackedDeviceIndex_Hmd);

        if (hmdPose.bPoseIsValid()) {
            return VRUtils.convertToMatrix4(hmdPose.mDeviceToAbsoluteTracking());
        }
        return new Matrix4(); // Returning an identity matrix as the default
    }
}
