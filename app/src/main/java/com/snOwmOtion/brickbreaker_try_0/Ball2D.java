package com.snOwmOtion.brickbreaker_try_0;

import android.opengl.Matrix;

public class Ball2D extends Shape {
    float[] initialPosition;
    float[] lightPosition;

    public Ball2D(VertexFormat[] vertices, short[] indices, float[] initialPosition, float[] lightPosition) {
        super(vertices, indices);
        this.initialPosition = initialPosition;
        this.lightPosition = lightPosition;
        Matrix.translateM(modelMatrix, 0, initialPosition[X], initialPosition[Y], initialPosition[Z]);
    }

    @Override
    public float[] GetShapePosition() {
        float[] originPosition = new float[] { 0.0f, 0.0f, 0.0f, 1.0f };
        Matrix.multiplyMV(originPosition, 0, modelMatrix, 0, vertices[0].position, 0);
        return originPosition;
    }

    public void Travel() {
        float screenRatio = GameStatus.glWindowHeight / GameStatus.glWindowWidth;
        float[] velocity = new float[] {
                GameStatus.ballDirection[X] * ValueSheet.ballDefaultPace * GameStatus.ballSpeedMultiplier * GameStatus.deltaTime * screenRatio,
                GameStatus.ballDirection[Y] * ValueSheet.ballDefaultPace * GameStatus.ballSpeedMultiplier * GameStatus.deltaTime,
                0.0f
        };
        Matrix.translateM(modelMatrix, 0, velocity[X], velocity[Y], 0.0f);
    }

    public void ResetBallStatus() {
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, initialPosition[X], initialPosition[Y], initialPosition[Z]);
    }
}
