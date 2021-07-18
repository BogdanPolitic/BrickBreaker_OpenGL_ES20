package com.example.brickbreaker_try_0;

import android.opengl.Matrix;
import android.os.SystemClock;

public class Brick extends Shape {
    enum Status {
        ON,
        SHRINKING,
        OFF
    }

    private final int X = 0;
    private final int Y = 1;

    private final int index;
    public Status status;
    private long timerId;

    public Brick(VertexFormat[] vertices, short[] indices, int index) {
        super(vertices, indices);
        this.index = index;
        status = Status.ON;
        AttachToOwnPosition();
    }

    public int GetIndex() {
        return index;
    }

    public void StartShrinking() {
        if (status == Status.SHRINKING)
            return;

        status = Status.SHRINKING;
        timerId = InterpolationTimer.AddTimer(GameStatus.brickShrinkingAwayDuration);
    }

    public void Update() {
        if (status == Status.SHRINKING) {
            float percent = InterpolationTimer.GetPercent(timerId);

            if (percent == 1.0f) {
                status = Status.OFF;
                return;
            }

            AttachToOwnPosition();
            Matrix.scaleM(modelMatrix, 0, 1.0f - percent, 1.0f - percent, 1.0f - percent);
        }
    }

    public void ResetBrickStatus() {
        status = Status.ON;
        AttachToOwnPosition();
    }

    private void AttachToOwnPosition() {
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, BrickNetwork.Position(X, index), BrickNetwork.Position(Y, index), 0.0f);
        Matrix.scaleM(modelMatrix, 0, BrickNetwork.scale[X], BrickNetwork.scale[Y], 1.0f);
    }
}
