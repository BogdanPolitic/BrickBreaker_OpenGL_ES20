package com.snOwmOtion.brickbreaker_try_0;

import android.opengl.Matrix;

public class MyMath {
    private static final int X = 0, Y = 1, Z = 2;
    public static final float degPerRad = 180.0f;
    public static float deg2Rad = (float)Math.PI / degPerRad;

    public static float Clamp(float value, float min, float max) {
        return (value < min)
                ? min
                : (Math.min(value, max));
    }

    public static float Norm(float[] v) {
        float norm = 0.0f;
        for (int i = 0; i < v.length; i++) {
            norm += Math.pow(v[i], 2);
        }
        return (float)Math.sqrt(norm);
    }

    public static float Norm3(float[] v) {
        if (v.length < 3)
            return -1.0f;
        return Norm(new float[] {v[0], v[1], v[2]});
    }

    public static float[] Normalize(float[] v) {
        float norm = Norm(v);

        float[] normalizedV = new float[v.length];
        for (int i = 0; i < v.length; i++) {
            normalizedV[i] = v[i] / norm;
        }

        return normalizedV;
    }

    private static float ScalarProduct(float[] v1, float[] v2) {
        if (v1.length != v2.length)
            return -1.0f;

        float sum = 0.0f;
        for (int i = 0; i < v1.length; i++) {
            sum += v1[i] * v2[i];
        }
        return sum;
    }

    public static float ScalarProduct3(float[] v1, float[] v2) {
        if (v1.length < 3 || v2.length < 3)
            return -1.0f;
        return ScalarProduct(new float[] {v1[0], v1[1], v1[2]}, new float[] {v2[0], v2[1], v2[2]});
    }

    private static float[] ScalarMVector(float s, float[] v) {
        float[] vRes = new float[v.length];
        for (int i = 0; i < v.length; i++) {
            vRes[i] = v[i] * s;
        }
        return vRes;
    }

    private static float[] VectorDifference(float[] v1, float[] v2) {
        if (v1.length != v2.length)
            return null;

        float[] vRes = new float[v1.length];
        for (int i = 0; i < v1.length; i++) {
            vRes[i] = v1[i] - v2[i];
        }
        return vRes;
    }

    public static float[] Reflect(float[] incidence, float[] normal) {
        float[] reversedIncidence = ScalarMVector(-1.0f, incidence);
        double angleRad = Math.acos(ScalarProduct3(reversedIncidence, normal) / (Norm3(reversedIncidence) * Norm3(normal)));
        double angleDeg = angleRad / Math.PI * 180.0d;

        float zAxis = CalculateZAxis(reversedIncidence, normal);

        float[] reflectedDirection = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
        float[] reflectionMatrix = new float[16];
        Matrix.setIdentityM(reflectionMatrix, 0);
        Matrix.rotateM(reflectionMatrix, 0, (float)angleDeg, 0.0f, 0.0f, zAxis);
        Matrix.multiplyMV(reflectedDirection, 0, reflectionMatrix, 0, normal, 0);

        return reflectedDirection;
    }

    private static float CalculateZAxis(float[] incidence, float[] normal) {
        float zAxis = Math.signum(incidence[X] * normal[Y] - incidence[Y] * normal[X]); // taken from the basic cross product formula, the coefficients of unit vector k (k is the equivalent of z axis)
        float eps = 0.01f;  // in case the incidence and normal are parallel or facing each other, we add a slight angle to the difference, so that zAxis is assured not to be zero

        if (zAxis == 0.0f) {
            zAxis = Math.signum(incidence[X] * (normal[Y] + eps) - incidence[Y] * normal[X]);
            if (zAxis == 0.0f) {
                zAxis = Math.signum(incidence[X] * normal[Y] - incidence[Y] * (normal[X] + eps));
            }
        }

        return zAxis;
    }
}
