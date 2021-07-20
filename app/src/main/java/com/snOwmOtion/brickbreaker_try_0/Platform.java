package com.snOwmOtion.brickbreaker_try_0;

import android.opengl.Matrix;

public class Platform extends Shape {
    public Platform(VertexFormat[] vertices, short[] indices) {
        super(vertices, indices);
        Matrix.translateM(modelMatrix, 0, 0.1f, GetPositionY(), 0.0f);
    }

    public float GetPositionY() {
        return -1.0f + ValueSheet.groundBorderHeight;
    }

    public void ChangeSizeCoord(int coord, float newSize) {
        if (coord == X)
            ValueSheet.platformWidth = newSize;
        if (coord == Y)
            ValueSheet.platformHeight = newSize;

        for (int i = 0; i < vertices.length; i++) {
            float[] position = vertices[i].position;
            vertices[i] = new VertexFormat(
                    new float[] {
                            (coord == X) ? Math.signum(position[X]) * newSize / 2.0f : position[X],
                            (coord == Y) ? Math.signum(position[Y]) * newSize / 2.0f : position[Y],
                            position[Z],
                            1.0f
                    },
                    vertices[i].color
            );
        }

        UpdateBuffers();
    }
}
