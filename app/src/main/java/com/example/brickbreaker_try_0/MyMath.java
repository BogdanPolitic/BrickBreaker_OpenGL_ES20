package com.example.brickbreaker_try_0;

public class MyMath {
    public static float Clamp(float value, float min, float max) {
        return (value < min)
                ? min
                : (Math.min(value, max));
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
        return VectorDifference(incidence, ScalarMVector(2 * ScalarProduct(incidence, normal), normal));
    }
}
