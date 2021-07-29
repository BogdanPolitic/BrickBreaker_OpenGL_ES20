package com.snOwmOtion.brickbreaker_try_0;

import android.os.SystemClock;

import com.snOwmOtion.brickbreaker_try_0.Bonifications.Bonifications;

import java.util.ArrayList;

public class GameStatus {
    enum GameState {
        IN_PLAY,
        LOST,
        WON
    }

    enum AppState {
        INGAME,
        WONGAME_POPUP,
        LOSTGAME_POPUP,
        WONGAME,
        LOSTGAME,
        WONGAME_POPOFF,
        LOSTGAME_POPOFF
    }

    // BallStatus count on the currently active ball
    enum BallStatus {
        ON_PLATFORM,
        FLOATING,
        LOST
    }

    public static AppState appState;
    public static GameState gameState;

    public static int score = 0;
    public static int bestScore = 0;

    public static float glWindowWidth;
    public static float glWindowHeight;
    public static long time;
    public static int deltaTime;

    public static float platformSpeed = 0.1f;  // [0.0f, 1.0f]

    public static int supplyBallsCount;
    public static BallStatus ballStatus = BallStatus.ON_PLATFORM;
    public static float initialBallSpeedMultiplier = 3.0f;
    public static float ballSpeedMultiplier = initialBallSpeedMultiplier;
    public static float[] ballDirection = new float[] {0.0f, 1.0f, 0.0f, 1.0f};
    // We get the real velocity by multiplying the direction with the amount of speed.

    public static GradientLine gradientLine1;

    public static int remainingBricksCount;
    public static long brickShrinkingAwayDuration = 500l;    // milliseconds

    public static int remainingPowerupsCount;
    public static long powerupDroppingDefaultDuration = 2500l;  // milliseconds
    public static long powerupStickingToPlatformDuration = 1500l;   // milliseconds

    public static Bonifications bonifications;

    private static int X = 0, Y = 1, Z = 2;
    private static long popupTimer, popoffTimer;
    private static float poppingPercent;

    public static int ActiveBallId() {
        return supplyBallsCount;
    }

    public static void NormalizeDirection() {
        float norm = (float)Math.sqrt(Math.pow(ballDirection[X], 2) + Math.pow(ballDirection[Y], 2));
        ballDirection = new float[] {ballDirection[X] / norm, ballDirection[Y] / norm, 0.0f, 1.0f};
    }

    public static void OnWindowChanged(float newWidth, float newHeight) {
        glWindowWidth = newWidth;
        glWindowHeight = newHeight;
    }

    public static void UpdateTime() {
        long newTime = SystemClock.uptimeMillis() % 10000000L;
        deltaTime = (int)(newTime - time);
        time = newTime;
    }

    public static void UnloadCurrentAndLoadNextBall() {
        if (supplyBallsCount == 0) {
            OnGameLostPopup();
            return;
        }

        supplyBallsCount--;
        ballStatus = BallStatus.ON_PLATFORM;
        ballDirection = new float[] {0.0f, 1.0f, 0.0f};
    }

    public static void ReleaseBall() {
        if (ballStatus == BallStatus.LOST) return;
        ballStatus = BallStatus.FLOATING;
    }

    public static void OnPowerupGainedOrDestroyed() {
        remainingPowerupsCount--;
        if (remainingPowerupsCount == 0)
            OnGameWonPopup();
    }

    public static void InitBonifications() {
        bonifications = new Bonifications();
        bonifications.Init();
    }

    public static void ApplyBonuses() {
        bonifications.ApplyBonuses();
    }

    public static void RevertBonuses() {
        ValueSheet.platformHeight = ValueSheet.initialPlatformHeight;
        ValueSheet.platformWidth = ValueSheet.initialPlatformWidth;
        ballSpeedMultiplier = initialBallSpeedMultiplier;

        ((Platform)MyGLRenderer.shapes.get("Platform")).ChangeSizeCoord(X, ValueSheet.initialPlatformWidth);
        ((Platform)MyGLRenderer.shapes.get("Platform")).ChangeSizeCoord(Y, ValueSheet.initialPlatformHeight);
    }

    public static void OnGameLostPopup() {
        ballDirection = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
        MainActivity.GetInstance().OnGameLostPopup();
        appState = AppState.LOSTGAME_POPUP;
        gameState = GameState.LOST;
        popupTimer = InterpolationTimer.AddTimer(ValueSheet.windowPopDuration);
    }

    public static void OnGameWonPopup() {
        if (score > bestScore)
            bestScore = score;
        ballDirection = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
        MainActivity.GetInstance().OnGameWonPopup();
        appState = AppState.WONGAME_POPUP;
        gameState = GameState.WON;
        popupTimer = InterpolationTimer.AddTimer(ValueSheet.windowPopDuration);
    }

    private static ArrayList<ScoreAnimation> scoreTriggers = new ArrayList<ScoreAnimation>();

    public static void AddToScore(int points) {
        score += points;
        MainActivity.GetInstance().SetScore(score);
        scoreTriggers.add(new ScoreAnimation());
    }

    public static void UpdateScoreFont() {
        if (scoreTriggers == null || scoreTriggers.size() == 0)
            return;

        // this new list will swap with the old one at each frame, because deleting multiple elements from a list is difficult
        ArrayList<ScoreAnimation> scoreTriggersSwap = new ArrayList<ScoreAnimation>();
        float scoreFontScale = (int)(float)ValueSheet.scoreFontScaleX.min;
        float scoreFontColor = (int)(float)ValueSheet.scoreColor.min;
        for (ScoreAnimation scoreAnimation : scoreTriggers) {
            scoreAnimation.UpdateScoreFontSize();
            scoreFontScale = MyMath.Clamp(
                    scoreFontScale + scoreAnimation.currentFont - ValueSheet.scoreFontScaleX.min,
                    ValueSheet.scoreFontScaleX.min,
                    ValueSheet.scoreFontScaleX.max
            );
            scoreFontColor = MyMath.Clamp(
                    scoreFontColor + scoreAnimation.currentColor - ValueSheet.scoreColor.min,
                    ValueSheet.scoreColor.min,
                    ValueSheet.scoreColor.max
            );
            if (!scoreAnimation.RequiresDestruction())
                scoreTriggersSwap.add(scoreAnimation);
        }
        scoreTriggers = scoreTriggersSwap;
        MainActivity.GetInstance().SetScoreScale(scoreFontScale);
        MainActivity.GetInstance().SetScoreColor((int)scoreFontColor + (0xFF << 24));
    }

    public static void RestartGame(boolean inputFromInPlayOrResume) {
        score = 0;
        MainActivity.GetInstance().SetScore(score);
        ballStatus = BallStatus.ON_PLATFORM;
        ballSpeedMultiplier = 0.01f;
        ballDirection = new float[] {0.0f, 1.0f, 0.0f, 1.0f};
        supplyBallsCount = ValueSheet.initialSupplyBallsCount;
        // To be changed if powerups are configured not to have a 100% chance drop on every break!
        remainingPowerupsCount = BrickNetwork.size[X] * BrickNetwork.size[Y];

        MyGLRenderer.ResetBrickNetwork();
        MyGLRenderer.ResetPowerups();
        MyGLRenderer.ResetBalls();
        MyGLRenderer.ResetPlatform();
        RevertBonuses();

        if (!inputFromInPlayOrResume) {
            appState = (appState == AppState.LOSTGAME) ? AppState.LOSTGAME_POPOFF : AppState.WONGAME_POPOFF;
            popoffTimer = InterpolationTimer.AddTimer(ValueSheet.windowPopDuration);
        }
    }

    public static void Init() {
        appState = AppState.INGAME;
        gameState = GameState.IN_PLAY;
        remainingBricksCount = BrickNetwork.size[X] * BrickNetwork.size[Y];
        // To be changed if powerups are configured not to have a 100% chance drop on every break!
        remainingPowerupsCount = BrickNetwork.size[X] * BrickNetwork.size[Y];
        supplyBallsCount = ValueSheet.initialSupplyBallsCount;
        MainActivity.GetInstance().SetScore(GameStatus.score);
    }

    public static void Update() {
        UpdateScoreFont();

        ApplyBonuses();
        Adjustments.AdjustBallDirection();
        NormalizeDirection();

        if (gameState == GameState.IN_PLAY && remainingBricksCount == 0 && remainingPowerupsCount == 0)
            OnGameWonPopup();

        switch (appState) {
            case LOSTGAME_POPUP:
                poppingPercent = InterpolationTimer.GetPercent(popupTimer);
                MainActivity.GetInstance().SetWindowSize(poppingPercent);
                if (poppingPercent == 1.0f)
                    appState = AppState.LOSTGAME;
                break;
            case WONGAME_POPUP:
                poppingPercent = InterpolationTimer.GetPercent(popupTimer);
                MainActivity.GetInstance().SetWindowSize(poppingPercent);
                if (poppingPercent == 1.0f)
                    appState = AppState.WONGAME;
                break;
            case LOSTGAME:
                MainActivity.GetInstance().OnGameLost();
                //InterpolationTimer.ClearAllTimers();
                break;
            case WONGAME:
                MainActivity.GetInstance().OnGameWon();
                //InterpolationTimer.ClearAllTimers();
                break;
            case LOSTGAME_POPOFF:
            case WONGAME_POPOFF:
                poppingPercent = InterpolationTimer.GetPercent(popoffTimer);
                MainActivity.GetInstance().SetWindowSize(1.0f - poppingPercent);
                if (poppingPercent == 1.0f) {
                    appState = AppState.INGAME;
                    gameState = GameState.IN_PLAY;
                }
                break;
            default:
                break;
        }

        UpdateTime();
        InterpolationTimer.Update();
    }
}
