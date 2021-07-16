package com.example.brickbreaker_try_0;

public class ValueSheet {
    public static float ballRadius = 0.06f;
    public static int ballEdgesNumber = 30;
    public static float platformHeight = 0.05f;
    public static float platformWidth = 0.4f;
    public static float borderWidthHoriz = 0.03f;
    public static float sideBordersOffsetFromGround = 0.3f;
    public static float groundBorderHeight = 0.15f;
    public static float platformPositionY = -0.85f;
    public static float depthUnderPlatformConsideredLost = 0.05f;
    public static float ballHeightRelativeToPlatform = 0.05f;

    public static float supplyBallsOffsetFromBorder = -0.97f;
    public static float supplyBallsDepthUnderPlatform = 0.07f;
    public static float distanceBetweenSupplyBalls = 0.06f;

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
}
