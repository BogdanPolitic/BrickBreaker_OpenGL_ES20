package com.snOwmOtion.brickbreaker_try_0;

public class BrickNetwork {
    public static int[] size = new int[]{10, 5};
    public static float[] brickSize = new float[]{0.08f, 0.13f};
    public static float[] bricksGapSize = new float[]{0.06f, 0.1f};
    public static float[] networkCenterPosition = new float[]{0.0f, 0.25f};
    public static float[] scale = new float[]{1.0f, 1.0f};
    protected static final int X = 0, Y = 1, Z = 2;

    public static int GetCoord(int coord, int index) {
        return (coord == X)
                ? index % BrickNetwork.size[X]
                : index / BrickNetwork.size[X];
    }

    // (-1, |0|, 1) for odd size, or (-1, ||, 1) for even size, where "||" is the center of the grid (on the "coord" axis)
    private static int PositionOnGrid(int coord, int index) {
        int position = GetCoord(coord, index) - BrickNetwork.size[coord] / 2;
        if (BrickNetwork.size[coord] % 2 == 0 && GetCoord(coord, index) >= BrickNetwork.size[coord] / 2)
            position++;

        return position;
    }

    public static float Position(int coord, int index) {
        float[] positionOnGrid = new float[] {PositionOnGrid(X, index), PositionOnGrid(Y, index)};
        float timesGapsNumber, timesBricksNumber;

        if (BrickNetwork.size[coord] % 2 == 0) {
            timesGapsNumber = timesBricksNumber = positionOnGrid[coord] - Math.signum(positionOnGrid[coord]) * 0.5f;
        } else {
            timesGapsNumber = timesBricksNumber = positionOnGrid[coord];
        }

        return BrickNetwork.networkCenterPosition[coord] +
                timesGapsNumber * BrickNetwork.bricksGapSize[coord] +
                timesBricksNumber * BrickNetwork.brickSize[coord];
    }
}
