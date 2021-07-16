package com.example.brickbreaker_try_0;

import android.renderscript.Sampler;

import java.util.ArrayList;
import java.util.HashMap;

public class Collisions {
    static class CornerInRange {
        int brickId;
        CornerType cornerType;
        public CornerInRange(int brickId, CornerType cornerType) {
            this.brickId = brickId;
            this.cornerType = cornerType;
        }
    }

    static class EdgeInRange {
        int brickId;
        EdgeType edgeType;
        public EdgeInRange(int brickId, EdgeType edgeType) {
            this.brickId = brickId;
            this.edgeType = edgeType;
        }
    }

    enum Borders {
        LEFT,
        RIGHT,
        TOP,
        PLATFORM
    }

    enum CornerType {
        BOTTOM_LEFT,
        BOTTOM_RIGHT,
        TOP_LEFT,
        TOP_RIGHT
    }

    enum EdgeType {
        BOTTOM,
        TOP,
        LEFT,
        RIGHT
    }

    protected static int X = 0, Y = 1, Z = 2;
    private static ArrayList<Borders> borderCollisions = new ArrayList<Borders>();
    private static ArrayList<Integer> brickCollisions = new ArrayList<Integer>();
    private static ArrayList<CornerInRange> cornersInRange = new ArrayList<CornerInRange>();
    private static ArrayList<EdgeInRange> edgesInRange = new ArrayList<EdgeInRange>();
    private static HashMap<CornerType, float[]> cornerNormal = new HashMap<CornerType, float[]>();
    private static HashMap<EdgeType, float[]> edgeNormal = new HashMap<EdgeType, float[]>();


    // ApplyBorderCollisions() MUST be called after this, at each frame.
    public static void CheckForBorderCollisions() {
        float[] activeBallPosition = MyGLRenderer.shapes.get("Ball2D" + GameStatus.ActiveBallId()).GetShapePosition();
        float screenRatio = GameStatus.glWindowHeight / GameStatus.glWindowWidth;

        if (activeBallPosition[X] - ValueSheet.ballRadius * screenRatio < -1.0f + ValueSheet.borderWidthHoriz)
            borderCollisions.add(Borders.LEFT);

        if (activeBallPosition[X] + ValueSheet.ballRadius * screenRatio > 1.0f - ValueSheet.borderWidthHoriz)
            borderCollisions.add(Borders.RIGHT);

        if (activeBallPosition[Y] + ValueSheet.ballRadius > 1.0f - ValueSheet.borderWidthHoriz / screenRatio)
            borderCollisions.add(Borders.TOP);

        if (activeBallPosition[Y] - ValueSheet.ballRadius < -1.0f + ValueSheet.groundBorderHeight) {
            float[] platformPosition = MyGLRenderer.shapes.get("Platform").GetShapePosition();
            if (Math.abs(activeBallPosition[X] - platformPosition[X]) <= ValueSheet.platformWidth / 2.0f)
                borderCollisions.add(Borders.PLATFORM);
        }
    }

    // CheckForBorderCollisions() must be called before.
    public static void ApplyBorderCollisions() {
        int simulataneousCollisionsCount = borderCollisions.size();
        float[] activeBallPosition = MyGLRenderer.shapes.get("Ball2D" + GameStatus.ActiveBallId()).GetShapePosition();
        float[] platformPosition = MyGLRenderer.shapes.get("Platform").GetShapePosition();
        switch (simulataneousCollisionsCount) {
            case 0:
                break;
            case 1:
                Borders border = borderCollisions.get(0);
                switch (border) {
                    case LEFT:
                    case RIGHT:
                        GameStatus.ballDirection[X] = -GameStatus.ballDirection[X];
                        break;
                    case PLATFORM:
                        float platformIncidence = (activeBallPosition[X] - platformPosition[X]) / (ValueSheet.platformWidth / 2.0f);
                        GameStatus.ballDirection[X] = platformIncidence;
                        GameStatus.ballDirection[Y] = (float)Math.sqrt(1.0f - Math.pow(platformIncidence, 2));
                        break;
                    default:    // case TOP:
                        GameStatus.ballDirection[Y] = -GameStatus.ballDirection[Y];
                }
                break;
            default:    // 2 simultaneous collisions:
                ReverseDirection();
        }

        // Reset the collision list for the next check (next frame):
        borderCollisions = new ArrayList<Borders>();
    }

    private static void ReverseDirection() {
        System.out.println("reverse direction");
        GameStatus.ballDirection = new float[] {
                -GameStatus.ballDirection[Y],
                -GameStatus.ballDirection[X],
                0.0f
        };
    }

    private static float GetActiveBallGridPosition(int coord) {
        float[] activeBallPosition = MyGLRenderer.shapes.get("Ball2D" + GameStatus.ActiveBallId()).GetShapePosition();
        float d = activeBallPosition[coord] - BrickNetwork.networkCenterPosition[coord];

        if (BrickNetwork.size[coord] % 2 == 0) {
            return (d + (BrickNetwork.bricksGapSize[coord] / 2.0f + BrickNetwork.brickSize[coord] / 2.0f))
                    / (BrickNetwork.bricksGapSize[coord] + BrickNetwork.brickSize[coord])
                    + (BrickNetwork.size[coord] / 2 - 1);
        }
        return d / (BrickNetwork.bricksGapSize[coord] + BrickNetwork.brickSize[coord])
                + (float)Math.floor(BrickNetwork.size[coord] / 2);
    }

    private static void AddBrickGridCoordToCollisions(int gridPosX, int gridPosY) {
        // daca bila se afla intre coordonate din afara grid-ului de bricks, atunci ea sigur nu e in coliziune cu niciun brick:
        if (gridPosX < 0
                || gridPosX >= BrickNetwork.size[X]
                || gridPosY < 0
                || gridPosY >= BrickNetwork.size[Y])
            return;
        int brickId = gridPosY * BrickNetwork.size[X] + gridPosX;
        Brick brick = (Brick) MyGLRenderer.shapes.get("Brick" + brickId);
        if (brick.status == Brick.Status.ON)
            brickCollisions.add(brickId);
    }

    public static void CheckForBrickCollisions() {
        float gridPosX = GetActiveBallGridPosition(X);
        float gridPosY = GetActiveBallGridPosition(Y);

        if (gridPosX == (int)Math.floor(gridPosX) && gridPosY == (int)Math.floor(gridPosY)) {   // daca bila e deja "snapped" pe centrul unui brick:
            AddBrickGridCoordToCollisions((int)gridPosX, (int)gridPosY);
        } else if (gridPosX == (int)Math.floor(gridPosX) && gridPosY != (int)Math.floor(gridPosY)) {   // daca bila e "snapped" o axa X a grid-ului, dar nu e "snapped" pe nicio axa Y
            AddBrickGridCoordToCollisions((int)gridPosX, (int)Math.floor(gridPosY));
            AddBrickGridCoordToCollisions((int)gridPosX, (int)Math.floor(gridPosY) + 1);
        } else if (gridPosX != (int)Math.floor(gridPosX) && gridPosY == (int)Math.floor(gridPosY)) {   // daca bila e "snapped" o axa Y a grid-ului, dar nu e "snapped" pe nicio axa X
            AddBrickGridCoordToCollisions((int)Math.floor(gridPosX), (int)gridPosY);
            AddBrickGridCoordToCollisions((int)Math.floor(gridPosX) + 1, (int)gridPosY);
        } else if (gridPosX != (int)Math.floor(gridPosX) && gridPosY != (int)Math.floor(gridPosY)) {   // daca bila nu e "snapped" pe nicio axa (in 99.999% din cazuri se ajunge aici)
            AddBrickGridCoordToCollisions((int)Math.floor(gridPosX), (int)Math.floor(gridPosY));
            AddBrickGridCoordToCollisions((int)Math.floor(gridPosX), (int)Math.floor(gridPosY) + 1);
            AddBrickGridCoordToCollisions((int)Math.floor(gridPosX) + 1, (int)Math.floor(gridPosY));
            AddBrickGridCoordToCollisions((int)Math.floor(gridPosX) + 1, (int)Math.floor(gridPosY) + 1);
        }
    }

    public static void CheckForBrickCornerCollisions() {
        float[] activeBallPosition = MyGLRenderer.shapes.get("Ball2D" + GameStatus.ActiveBallId()).GetShapePosition();
        float screenRatio = GameStatus.glWindowHeight / GameStatus.glWindowWidth;

        for (int brickId = 0; brickId < brickCollisions.size(); brickId++) {
            Shape currentBrick = MyGLRenderer.shapes.get("Brick" + brickCollisions.get(brickId));
            float[] brickPosition = currentBrick.GetShapePosition();
            VertexFormat[] verticesPositions = currentBrick.vertices;
            for (int cornerId = 0; cornerId < verticesPositions.length; cornerId++) {
                float[] cornerPosition = new float[] {
                        brickPosition[X] + verticesPositions[cornerId].position[X],
                        brickPosition[Y] + verticesPositions[cornerId].position[Y],
                        0.0f
                };
                float distanceFromBallToCorner = (float)Math.sqrt(
                        Math.pow((cornerPosition[X] - activeBallPosition[X]) / screenRatio, 2)  // It works PERFECTLY by dividing by the screenRatio, but using my logic you would've needed to multiply by it instead. (!?)
                                + Math.pow(cornerPosition[Y] - activeBallPosition[Y], 2)
                );
                if (distanceFromBallToCorner <= ValueSheet.ballRadius) {
                    CornerType cornerType = GetCornerType(cornerId);
                    cornersInRange.add(new CornerInRange(brickCollisions.get(brickId), cornerType));
                }
            }
        }
    }

    public static void CheckForBrickStraightEdgeCollisions() {
        float[] activeBallPosition = MyGLRenderer.shapes.get("Ball2D" + GameStatus.ActiveBallId()).GetShapePosition();
        float screenRatio = GameStatus.glWindowHeight / GameStatus.glWindowWidth;

        for (int brickId = 0; brickId < brickCollisions.size(); brickId++) {
            boolean noCornerJoint = true;
            if (cornersInRange.size() > 0) {
                int cornerIdx = 0;
                while (cornerIdx < cornersInRange.size()) {
                    if (cornersInRange.get(cornerIdx).brickId == brickCollisions.get(brickId)) {
                        noCornerJoint = false;
                        break;
                    }
                    cornerIdx++;
                }
                if (!noCornerJoint)
                    continue;
            }

            Shape currentBrick = MyGLRenderer.shapes.get("Brick" + brickCollisions.get(brickId));
            float[] brickPosition = currentBrick.GetShapePosition();
            if (Math.abs(brickPosition[Y] - (activeBallPosition[Y] - ValueSheet.ballRadius)) <= BrickNetwork.brickSize[Y] / 2.0f
                    && Math.abs(brickPosition[X] - activeBallPosition[X]) <= BrickNetwork.brickSize[X] / 2.0f) {
                edgesInRange.add(new EdgeInRange(brickCollisions.get(brickId), EdgeType.TOP));
            }
            if (Math.abs(brickPosition[Y] - (activeBallPosition[Y] + ValueSheet.ballRadius)) <= BrickNetwork.brickSize[Y] / 2.0f
                    && Math.abs(brickPosition[X] - activeBallPosition[X]) <= BrickNetwork.brickSize[X] / 2.0f) {
                edgesInRange.add(new EdgeInRange(brickCollisions.get(brickId), EdgeType.BOTTOM));
            }
            if (Math.abs(brickPosition[Y] - activeBallPosition[Y]) <= BrickNetwork.brickSize[Y] / 2.0f
                    && Math.abs(brickPosition[X] - (activeBallPosition[X] - ValueSheet.ballRadius * screenRatio)) <= BrickNetwork.brickSize[X] / 2.0f) {
                edgesInRange.add(new EdgeInRange(brickCollisions.get(brickId), EdgeType.RIGHT));
            }
            if (Math.abs(brickPosition[Y] - activeBallPosition[Y]) <= BrickNetwork.brickSize[Y] / 2.0f
                    && Math.abs(brickPosition[X] - (activeBallPosition[X] + ValueSheet.ballRadius * screenRatio)) <= BrickNetwork.brickSize[X] / 2.0f) {
                edgesInRange.add(new EdgeInRange(brickCollisions.get(brickId), EdgeType.LEFT));
            }
        }
    }

    public static void ApplyBrickCollisions() {
        int collisionsCount = cornersInRange.size() + edgesInRange.size();

        if (collisionsCount == 0) {
            FreeCollisionData();
            return;
        }

        ProceedToDestructCollidedBricks();

        if (collisionsCount > 1) {
            ReverseDirection();
            FreeCollisionData();
            return;
        }

        // collisionsCount == 1
        float[] reflectedDirection;
        if (cornersInRange.size() > 0) {
            reflectedDirection = MyMath.Reflect(
                    GameStatus.ballDirection,
                    cornerNormal.get(cornersInRange.get(0).cornerType)
            );
            GameStatus.ballDirection = reflectedDirection;
            GameStatus.NormalizeDirection();
            FreeCollisionData();
            return;
        }

        if (edgesInRange.size() > 0) {
            reflectedDirection = MyMath.Reflect(
                    GameStatus.ballDirection,
                    edgeNormal.get(edgesInRange.get(0).edgeType)
            );
            GameStatus.ballDirection = reflectedDirection;
            GameStatus.NormalizeDirection();
        }

        FreeCollisionData();
    }

    private static void ProceedToDestructCollidedBricks() {
        if (cornersInRange.size() > 0)
            for (int i = 0; i < cornersInRange.size(); i++) {
                Brick brick = (Brick) MyGLRenderer.shapes.get("Brick" + cornersInRange.get(i).brickId);
                Powerup powerup = (Powerup) MyGLRenderer.shapes.get("Powerup" + cornersInRange.get(i).brickId);
                if (brick.status == Brick.Status.ON) {
                    brick.StartShrinking();
                    GameStatus.remainingBricksCount--;
                    powerup.StartDropping();
                }
            }

        if (edgesInRange.size() > 0)
            for (int i = 0; i < edgesInRange.size(); i++) {
                Brick brick = (Brick)MyGLRenderer.shapes.get("Brick" + edgesInRange.get(i).brickId);
                Powerup powerup = (Powerup) MyGLRenderer.shapes.get("Powerup" + edgesInRange.get(i).brickId);
                if (brick.status == Brick.Status.ON) {
                    brick.StartShrinking();
                    GameStatus.remainingBricksCount--;
                    powerup.StartDropping();
                }
            }
    }

    public static void FreeCollisionData() {
        brickCollisions = new ArrayList<Integer>();
        cornersInRange = new ArrayList<CornerInRange>();
        edgesInRange = new ArrayList<EdgeInRange>();
    }

    /*
    Brick corners topology:
            1 --- 3
            |     |
            0 --- 2
     */
    private static CornerType GetCornerType(int cornerId) {
        switch (cornerId) {
            case 0:
                return CornerType.BOTTOM_LEFT;
            case 1:
                return CornerType.TOP_LEFT;
            case 2:
                return CornerType.BOTTOM_RIGHT;
            default:    // case 3
                return CornerType.TOP_RIGHT;
        }
    }


    public static void Init() {
        {
            float screenRatio = GameStatus.glWindowHeight / GameStatus.glWindowWidth;
            float norm = (float) Math.sqrt(Math.pow(BrickNetwork.brickSize[X] / screenRatio, 2) + Math.pow(BrickNetwork.brickSize[Y], 2));
            cornerNormal.put(CornerType.BOTTOM_LEFT, new float[]{-BrickNetwork.brickSize[X] / screenRatio / norm, -BrickNetwork.brickSize[Y] / norm, 0.0f, 1.0f});
            cornerNormal.put(CornerType.BOTTOM_RIGHT, new float[]{BrickNetwork.brickSize[X] / screenRatio / norm, -BrickNetwork.brickSize[Y] / norm, 0.0f, 1.0f});
            cornerNormal.put(CornerType.TOP_LEFT, new float[]{-BrickNetwork.brickSize[X] / screenRatio / norm, BrickNetwork.brickSize[Y] / norm, 0.0f, 1.0f});
            cornerNormal.put(CornerType.TOP_RIGHT, new float[]{BrickNetwork.brickSize[X] / screenRatio / norm, BrickNetwork.brickSize[Y] / norm, 0.0f, 1.0f});
        }

        edgeNormal.put(EdgeType.BOTTOM, new float[] {0.0f, -1.0f, 0.0f, 1.0f});
        edgeNormal.put(EdgeType.LEFT, new float[] {-1.0f, 0.0f, 0.0f, 1.0f});
        edgeNormal.put(EdgeType.TOP, new float[] {0.0f, 1.0f, 0.0f, 1.0f});
        edgeNormal.put(EdgeType.RIGHT, new float[] {1.0f, 0.0f, 0.0f, 1.0f});
    }
}
