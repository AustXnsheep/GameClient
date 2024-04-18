package git.austxnsheep.vr.utils;

import com.badlogic.gdx.math.Matrix4;
import org.lwjgl.openvr.HmdMatrix34;
import org.lwjgl.openvr.HmdMatrix44;

public class VRUtils {
    //Made completely by chatgpt. I cannot do none of this for the life of me
    public static Matrix4 convertToMatrix4(HmdMatrix34 hmdMatrix) {
        Matrix4 matrix4 = new Matrix4();
        // Set the 3x4 part of the matrix
        matrix4.val[Matrix4.M00] = hmdMatrix.m(0);
        matrix4.val[Matrix4.M01] = hmdMatrix.m(1);
        matrix4.val[Matrix4.M02] = hmdMatrix.m(2);
        matrix4.val[Matrix4.M03] = hmdMatrix.m(3);
        matrix4.val[Matrix4.M10] = hmdMatrix.m(4);
        matrix4.val[Matrix4.M11] = hmdMatrix.m(5);
        matrix4.val[Matrix4.M12] = hmdMatrix.m(6);
        matrix4.val[Matrix4.M13] = hmdMatrix.m(7);
        matrix4.val[Matrix4.M20] = hmdMatrix.m(8);
        matrix4.val[Matrix4.M21] = hmdMatrix.m(9);
        matrix4.val[Matrix4.M22] = hmdMatrix.m(10);
        matrix4.val[Matrix4.M23] = hmdMatrix.m(11);
        // Set the last row to make it a proper affine transform matrix
        matrix4.val[Matrix4.M30] = 0;
        matrix4.val[Matrix4.M31] = 0;
        matrix4.val[Matrix4.M32] = 0;
        matrix4.val[Matrix4.M33] = 1;

        return matrix4;
    }
    public static Matrix4 convertToMatrix4(HmdMatrix44 hmdMatrix) {
        Matrix4 matrix4 = new Matrix4();
        // Set the entire 4x4 matrix
        for (int i = 0; i < 16; i++) {
            matrix4.val[i] = hmdMatrix.m(i);
        }
        return matrix4;
    }
}
