package com.example.brickbreaker_try_0;

public class VertexFormat {
    public float[] position;
    public float[] color;
    public static int POSITION_LENGTH = 3;
    public static int COLOR_LENGTH = 4;
    private final int X = 0, Y = 1, Z = 2;

    public VertexFormat(float[] position, float[] color) {
        this.position = new float[] {position[X], position[Y], position[Z], 1.0f};
        this.color = color;
    }
}
