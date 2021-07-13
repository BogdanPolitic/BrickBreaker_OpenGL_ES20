package com.example.brickbreaker_try_0;

import android.opengl.Matrix;

public class Platform extends Shape {
    public Platform(VertexFormat[] vertices, short[] indices) {
        super(vertices, indices);
        Matrix.translateM(modelMatrix, 0, 0.0f, ValueSheet.platformPositionY, 0.0f);
    }
}
