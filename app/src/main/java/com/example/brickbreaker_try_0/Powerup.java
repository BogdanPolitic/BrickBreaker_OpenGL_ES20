package com.example.brickbreaker_try_0;

import android.opengl.Matrix;

public class Powerup extends Shape {
    public enum FunctionalType {
        SIMPLE,
        TRICKY,
        SUPER_TRICKY
    }

    public enum ShapeType {
        POLYGON,
        SQUARE,
        TRIANGLE
    }

    public enum Status {
        LATENT,
        DROPPING,
        ON_PLATFORM,
        OFF
    }

    private FunctionalType powerupFunctionalType;
    private ShapeType powerupShapeType;
    private float[] homogenousColor;
    public boolean enableBlending;
    public Status status;
    private long droppingStart;
    private float droppingDescentSpeed;
    private float droppingRotationSpeedDeg;
    private float offsetXOnPlatform;
    private long gettingOnPlatformTimestamp;
    private Brick brickTrigger;

    // in modelMatrix1 there are all the transformations, except the one which translates the powerup with the X coordinate of its corresponding brick (or its own initial position)
    // modelMatrix1 is needed for offsetting the powerup to the platform when it comes sticking on it (when the player GETS the powerup). Any missed powerup will instatly disappear after exceeding the low Y limit
    float[] modelMatrix1 = new float[16];
    float[] modelMatrix2 = new float[16];
    float[] modelMatrixOnPlatform = new float[16];

    public Powerup(VertexFormat[] vertices, short[] indices, FunctionalType powerupFunctionalType, ShapeType powerupShapeType, int brickTriggerIndex, float[] color) {
        super(vertices, indices);
        this.powerupFunctionalType = powerupFunctionalType;
        this.powerupShapeType = powerupShapeType;
        this.homogenousColor = color;
        this.enableBlending = false;
        this.status = Status.LATENT;

        switch (powerupFunctionalType) {
            case SIMPLE:
                droppingDescentSpeed = 2.0f;
                droppingRotationSpeedDeg = 3.0f;
                break;
            case TRICKY:
                droppingDescentSpeed = 3.0f;
                droppingRotationSpeedDeg = 5.0f;
                break;
            default:
                droppingDescentSpeed = 5.0f;
                droppingRotationSpeedDeg = 10.0f;
        }

        this.brickTrigger = (Brick)MyGLRenderer.shapes.get("Brick" + brickTriggerIndex);
        AttachToOwnPosition();
    }

    public int GetIndex() {
        return brickTrigger.GetIndex();
    }

    public void StartDropping() {
        if (status == Status.DROPPING)
            return;

        status = Status.DROPPING;
        droppingStart = GameStatus.time;
    }

    public void Update() {
        if (status == Status.DROPPING) {
            float droppingTransformArg = droppingRotationSpeedDeg
                    * (float)(GameStatus.time - droppingStart)
                    / (float)GameStatus.powerupDroppingDefaultDuration;
            float droppingPercentage = (float)(GameStatus.time - droppingStart) * droppingDescentSpeed / (float)GameStatus.powerupDroppingDefaultDuration;

            if (droppingPercentage > 1.0f) {
                status = Status.OFF;
                return;
            }

            if (!IsInPlatformRange() && droppingPercentage > 1.0f) {
                status = Status.OFF;
                return;
            } else if (IsInPlatformRange() && GetShapePosition()[Y] <= -1.0f + ValueSheet.groundBorderHeight) {
                status = Status.ON_PLATFORM;
                offsetXOnPlatform = GetOffsetXRelativeToPlatformCenter();
                gettingOnPlatformTimestamp = GameStatus.time;
                enableBlending = true;
                return;
            }

            float initPosY = GetInitialPositionY();
            float finalPosY = -1.0f + ValueSheet.groundBorderHeight - ValueSheet.depthUnderPlatformConsideredLost;
            Matrix.setIdentityM(modelMatrix1, 0);
            Matrix.setIdentityM(modelMatrix2, 0);
            float screenRatio = GameStatus.glWindowHeight / GameStatus.glWindowWidth;

            switch (powerupFunctionalType) {
                case SIMPLE:
                    // translate powerup so that the X position remains unchanged during its lifetime but the Y position descends gradually per frame:
                    Matrix.translateM(modelMatrix2, 0, BrickNetwork.Position(X, GetIndex()), 0.0f, 0.0f);
                    Matrix.translateM(modelMatrix1, 0, 0.0f, initPosY + (finalPosY - initPosY) * droppingPercentage, 0.0f);
                    // scale powerup so that at every frame/rotation it's stretch is not affected by the phone's screen ratio:
                    Matrix.scaleM(modelMatrix1, 0, screenRatio, 1.0f, 1.0f);
                    // rotate powerup around self while it's descending towards the ground:
                    Matrix.rotateM(modelMatrix1, 0, droppingTransformArg * MyMath.degPerRad, 0.0f, 0.0f, -1.0f);
                    // scale powerup according to the (every) brick's scale:
                    Matrix.scaleM(modelMatrix1, 0, BrickNetwork.scale[X], BrickNetwork.scale[Y], 1.0f);
                    break;
                default:    // case TRICKY or SUPER_TRICKY:
                    Matrix.translateM(modelMatrix2, 0, BrickNetwork.Position(X, GetIndex()), 0.0f, 0.0f);
                    Matrix.translateM(modelMatrix1, 0, 0.0f, initPosY + (finalPosY - initPosY) * droppingPercentage, 0.0f);
                    Matrix.scaleM(modelMatrix1, 0, screenRatio, 1.0f, 1.0f);
                    // the powerup is not rotating around self, like the SIMPLE powerup type
                    // when the powerup is rotated, the rotation happens around an intuitive circle with the radius equal to the translation described 2-3 lines below:
                    Matrix.rotateM(modelMatrix1, 0, droppingTransformArg * MyMath.degPerRad, 0.0f, 0.0f, -1.0f);
                    // translate powerup back and forth (on local X axis) according to a sine wave
                    Matrix.translateM(modelMatrix1, 0, ValueSheet.trickyPowerupMotionXMagnitude * (float)Math.sin(Math.PI * droppingTransformArg), 0.0f, 0.0f);
                    Matrix.scaleM(modelMatrix1, 0, BrickNetwork.scale[X], BrickNetwork.scale[Y], 1.0f);
            }
            Matrix.multiplyMM(modelMatrix, 0, modelMatrix2, 0, modelMatrix1, 0);
        }

        if (status == Status.ON_PLATFORM) {
            Shape platform = MyGLRenderer.shapes.get("Platform");
            Matrix.setIdentityM(modelMatrixOnPlatform, 0);
            Matrix.translateM(modelMatrixOnPlatform, 0, platform.GetShapePosition()[X] + offsetXOnPlatform, 0.0f, 0.0f);
            Matrix.multiplyMM(modelMatrix, 0, modelMatrixOnPlatform, 0, modelMatrix1, 0);

            float opacity = 1.0f - (float)(GameStatus.time - gettingOnPlatformTimestamp) / (float)GameStatus.powerupStickingToPlatformDuration;
            if (opacity < 0.0f) {
                status = Status.OFF;
                return;
            }
            ChangeShapeColor(new float[] {homogenousColor[X], homogenousColor[Y], homogenousColor[Z], opacity});
            UpdateBuffers();
        }
    }

    private void AttachToOwnPosition() {
        int index = GetIndex();
        float screenRatio = GameStatus.glWindowHeight / GameStatus.glWindowWidth;
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, BrickNetwork.Position(X, index), BrickNetwork.Position(Y, index), 0.0f);
        Matrix.scaleM(modelMatrix, 0, screenRatio, 1.0f, 1.0f);
        Matrix.scaleM(modelMatrix, 0, BrickNetwork.scale[X], BrickNetwork.scale[Y], 1.0f);
    }

    private float GetInitialPositionY() {
        return BrickNetwork.Position(Y, GetIndex());
    }

    private boolean IsInPlatformRange() {
        Shape platform = MyGLRenderer.shapes.get("Platform");
        return Math.abs(GetShapePosition()[X] - platform.GetShapePosition()[X]) < ValueSheet.platformWidth / 2.0f;
    }

    private float GetOffsetXRelativeToPlatformCenter() {
        Shape platform = MyGLRenderer.shapes.get("Platform");
        return GetShapePosition()[X] - platform.GetShapePosition()[X];
    }
}
