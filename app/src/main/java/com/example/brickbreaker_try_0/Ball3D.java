package com.example.brickbreaker_try_0;

import android.opengl.Matrix;

public class Ball3D extends Shape {
    float[] initialPosition;
    float[] lightPosition;

    public Ball3D(VertexFormat[] vertices, short[] indices, float[] initialPosition, float[] lightPosition) {
        super(vertices, indices);
        this.initialPosition = initialPosition;
        this.lightPosition = lightPosition;
        Matrix.translateM(modelMatrix, 0, initialPosition[0], initialPosition[1], initialPosition[2]);
    }
}
