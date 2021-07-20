package com.snOwmOtion.brickbreaker_try_0;

import android.opengl.Matrix;

public class GradientLine {
    private float[] pointDef1;
    private float[] initialPointDef1;
    private float[] pointDef2;
    private float[] initialPointDef2;
    public float[] equation = new float[3];
    public float[] color;
    private float[] modelMatrix = new float[16];
    private float[] referencePos = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
    private float initialDistanceToLine;
    protected static final int X = 0, Y = 1, Z = 2;
    protected static final int a = 0, b = 1, c = 2;

    private void CalculateEquation() {
        float slope = (pointDef2[Y] - pointDef1[Y]) / (pointDef2[X] - pointDef1[X]);
        equation[a] = -slope;
        equation[b] = 1;
        equation[c] = slope * pointDef1[X] - pointDef1[Y];
    }

    private float DistanceFromReferencePosToLine() {
        return (float)Math.abs(equation[a] * referencePos[X] + equation[b] * referencePos[Y] + equation[c])
                / (float)Math.sqrt(Math.pow(equation[a], 2) + Math.pow(equation[b], 2));
    }

    public GradientLine(float[] pointDef1, float[] pointDef2, float[] color) {
        this.pointDef1 = new float[] {pointDef1[X], pointDef1[Y], 0.0f, 1.0f};
        this.initialPointDef1 = this.pointDef1.clone();
        this.pointDef2 = new float[] {pointDef2[X], pointDef2[Y], 0.0f, 1.0f};
        this.initialPointDef2 = this.pointDef2.clone();

        this.color = new float[] {color[0], color[1], color[2], 1.0f};
        CalculateEquation();
        initialDistanceToLine = DistanceFromReferencePosToLine();
    }

    public void TranslateLine(float[] amount2D) {
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, amount2D[X], amount2D[Y], 0.0f);
        Matrix.multiplyMV(pointDef1, 0, modelMatrix, 0, pointDef1, 0);
        Matrix.multiplyMV(pointDef2, 0, modelMatrix, 0, pointDef2, 0);
        CalculateEquation();

        float distanceProgress = DistanceFromReferencePosToLine() - initialDistanceToLine;
        // 1.5f hardcoded! so that the gradient (on the bricks) repeats (approximately) smoothly
        if (distanceProgress <= -2.0f / 1.5f || distanceProgress >= 2.0f / 1.5f) {
            pointDef1 = initialPointDef1.clone();
            pointDef2 = initialPointDef2.clone();
            CalculateEquation();
        }
    }

    public void RotateLine(float angleDeg) {
        float[] pointDef2RelativeTo1 = new float[] {
                pointDef2[X] - pointDef1[X],
                pointDef2[Y] - pointDef1[Y],
                0.0f,
                1.0f
        };
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.rotateM(modelMatrix, 0, angleDeg * MyMath.deg2Rad, 0.0f, 0.0f, -1.0f);
        Matrix.multiplyMV(pointDef2RelativeTo1, 0, modelMatrix, 0, pointDef2RelativeTo1, 0);
        pointDef2 = new float[] {
                pointDef1[X] + pointDef2RelativeTo1[X],
                pointDef1[Y] + pointDef2RelativeTo1[Y],
                0.0f,
                1.0f
        };
        CalculateEquation();
    }
}
