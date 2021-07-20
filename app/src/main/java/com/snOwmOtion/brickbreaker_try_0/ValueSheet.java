package com.snOwmOtion.brickbreaker_try_0;

import java.util.HashMap;

public class ValueSheet {
    public static float ballRadius = 0.06f;
    public static int ballEdgesNumber = 30;
    public static float platformHeight = 0.05f;
    public static float platformWidth = 0.4f;
    public static float initialPlatformHeight = 0.05f;
    public static float initialPlatformWidth = 0.4f;
    public static float borderWidthHoriz = 0.03f;
    public static float sideBordersOffsetFromGround = 0.3f;
    public static float groundBorderHeight = 0.2f;
    public static float depthUnderPlatformConsideredLost = 0.05f;
    public static float ballHeightRelativeToPlatform = 0.05f;

    public static int initialSupplyBallsCount = 3;
    public static float supplyBallsOffsetFromBorder = -0.97f;
    public static float supplyBallsDepthUnderPlatform = 0.07f;
    public static float distanceBetweenSupplyBalls = 0.06f;
    public static float ballDirectionFlatLimit = 0.2f;

    public static float[] gradientLineP1 = new float[] {-0.6f, 1.0f, 0.0f, 1.0f};
    public static float[] gradientLineP2 = new float[] {-0.1f, -1.0f, 0.0f, 1.0f};
    public static float[] gradientLineColor = new float[] {1.0f, 1.0f, 1.0f, 1.0f};
    public static float[] gradientLineSpeed = new float[] {0.003f, 0.0f, 0.0f};

    public static long windowPopDuration = 250l;

    public static float trickyPowerupMotionXMagnitude = 0.1f;

    public static float powerupPolygonEdge = 0.06f;
    public static float powerupSquareEdge = 0.1f;
    public static float powerupTriangleEdge = 0.1f;
    public static float[][] powerupColorsPool = new float[][] {
            new float[] {1.0f, 0.0f, 1.0f, 1.0f},
            new float[] {0.0f, 1.0f, 0.0f, 1.0f},
            new float[] {0.0f, 1.0f, 1.0f, 1.0f},
            new float[] {0.0f, 0.5f, 1.0f, 1.0f},
            new float[] {0.5f, 1.0f, 1.0f, 1.0f},
            new float[] {0.0f, 1.0f, 0.5f, 1.0f},
            new float[] {1.0f, 0.5f, 0.5f, 1.0f},
            new float[] {1.0f, 1.0f, 0.5f, 1.0f},
            new float[] {1.0f, 1.0f, 0.0f, 1.0f},
            new float[] {0.5f, 1.0f, 0.5f, 1.0f}
    };

    static class Interval<T> {
        T min;
        T max;
        public Interval(T min, T max) {
            this.min = min;
            this.max = max;
        }
    }

    public static HashMap<Powerup.FunctionalType, Interval<Float>> powerupPoints = new HashMap<Powerup.FunctionalType, Interval<Float>>() {
        {
            put(Powerup.FunctionalType.SIMPLE, new Interval<Float>(1.01f, 3.01f));
            put(Powerup.FunctionalType.TRICKY, new Interval<Float>(4.01f, 6.01f));
            put(Powerup.FunctionalType.SUPER_TRICKY, new Interval<Float>(10.01f, 12.01f));
        }
    };

    public static Interval<Float> scoreFontScaleX = new Interval<Float>(1.0f, 1.25f);
    public static Interval<Integer> scoreColor = new Interval<Integer>(0x00FF00, 0x00FFFF);
    public static long scoreAnimationTime = 200l;
}
