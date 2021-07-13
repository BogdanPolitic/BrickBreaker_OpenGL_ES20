package com.example.brickbreaker_try_0;

import android.os.SystemClock;

public class GameStatus {
    // BallStatus count on the currently active ball
    enum BallStatus {
        ON_PLATFORM,
        FLOATING,
        LOST
    }

    public static float glWindowWidth;
    public static float glWindowHeight;
    public static long time;

    public static int remainingBallsCount = 5;
    public static BallStatus ballStatus = BallStatus.ON_PLATFORM;
    public static float ballSpeed = 0.01f;
    public static float[] ballDirection = new float[] {0.0f, 1.0f, 0.0f};
    // We get the real velocity by multiplying the direction with the amount of speed.

    public static long brickShrinkingAwayDuration = 500l;    // milliseconds

    public static int ActiveBallId() {
        return remainingBallsCount;
    }

    public static void NormalizeDirection() {
        int X = 0, Y = 1;
        float norm = (float)Math.sqrt(Math.pow(ballDirection[X], 2) + Math.pow(ballDirection[Y], 2));
        ballDirection = new float[] {ballDirection[X] / norm, ballDirection[Y] / norm, 0.0f};
    }

    public static void OnWindowChanged(float newWidth, float newHeight) {
        glWindowWidth = newWidth;
        glWindowHeight = newHeight;
    }

    public static void UpdateTime() {
        time = SystemClock.uptimeMillis() % 10000000L;
    }

    public static void RestartGame() {
        remainingBallsCount = 5;
        ballStatus = BallStatus.ON_PLATFORM;
        ballSpeed = 0.01f;
        ballDirection = new float[] {0.0f, 1.0f, 0.0f};
    }

    public static void UnloadCurrentAndLoadNextBall() {
        /*if (remainingBricksCount == 0) {
            OnGameWon();
            return;
        }*/

        if (remainingBallsCount == 0) {
            OnGameLost();
            return;
        }

        remainingBallsCount--;
        ballStatus = BallStatus.ON_PLATFORM;
        ballDirection = new float[] {0.0f, 1.0f, 0.0f};
    }

    public static void ReleaseBall() {
        if (ballStatus == BallStatus.LOST) return;
        ballStatus = BallStatus.FLOATING;
    }

    public static void OnGameLost() {
        System.out.println("Game lost!");
    }

    public static void OnGameWon() {
        System.out.println("Game won!");
    }
}
