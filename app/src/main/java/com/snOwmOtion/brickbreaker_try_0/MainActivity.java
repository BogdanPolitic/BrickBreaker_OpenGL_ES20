package com.snOwmOtion.brickbreaker_try_0;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static MainActivity instance;
    TextView scoreTextView;
    LinearLayout gameWonLayout;
    LinearLayout gameLostLayout;
    LinearLayout gameFrozenLayout;
    TextView gameWonScore;
    TextView gameWonBestScore;
    TextView gameLostBestScore;
    public boolean gameInteractionEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance = this;
        gameInteractionEnabled = true;

        Button restartGameButton = (Button) findViewById(R.id.restartGameButton);
        Button releaseBallButton = (Button) findViewById(R.id.releaseBallButton);
        gameWonLayout = (LinearLayout) findViewById(R.id.gameWonLayout);
        gameLostLayout = (LinearLayout) findViewById(R.id.gameLostLayout);
        gameFrozenLayout = (LinearLayout) findViewById(R.id.gameFrozenLayout);
        Button gameWonRestartGameButtom = (Button) findViewById(R.id.gameWonRestartGameButton);
        Button gameLostRestartGameButtom = (Button) findViewById(R.id.gameLostRestartGameButton);
        gameWonScore = (TextView) findViewById(R.id.gameWonScore);
        gameWonBestScore = (TextView) findViewById(R.id.gameWonBestScore);
        gameLostBestScore = (TextView) findViewById(R.id.gameLostBestScore);

        scoreTextView = (TextView) findViewById(R.id.totalPointsTextView);

        restartGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gameInteractionEnabled)
                    GameStatus.RestartGame(true);
            }
        });

        releaseBallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gameInteractionEnabled)
                    GameStatus.ReleaseBall();
            }
        });

        gameWonRestartGameButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!gameInteractionEnabled) {
                    gameInteractionEnabled = true;
                    SwitchFadeWindow(0.0f);
                    MyGLSurfaceView.GetInstance().SetGameInteraction(true);
                    GameStatus.RestartGame(false);
                }
            }
        });

        gameLostRestartGameButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GameStatus.appState == GameStatus.AppState.LOSTGAME
                || GameStatus.appState == GameStatus.AppState.WONGAME) {
                    gameInteractionEnabled = true;
                    SwitchFadeWindow(0.0f);
                    MyGLSurfaceView.GetInstance().SetGameInteraction(true);
                    GameStatus.RestartGame(false);
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        GameStatus.RestartGame(true);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        GameStatus.RestartGame(true);
    }

    public static MainActivity GetInstance() {
        return instance;
    }

    public void OnGameWonPopup() {
        gameInteractionEnabled = false;
        MyGLSurfaceView.GetInstance().SetGameInteraction(false);
        String scoreStr = "Score: " + GameStatus.score;
        String bestScoreStr = "Best score: " + GameStatus.bestScore;
        gameWonScore.setText(scoreStr);
        gameWonBestScore.setText(bestScoreStr);
        SwitchFadeWindow(1.0f);
    }

    public void OnGameLostPopup() {
        gameInteractionEnabled = false;
        MyGLSurfaceView.GetInstance().SetGameInteraction(false);
        String bestScoreStr = "Best score: " + GameStatus.bestScore;
        gameLostBestScore.setText(bestScoreStr);
        SwitchFadeWindow(1.0f);
    }

    public void OnGameWon() {

    }

    public void OnGameLost() {

    }

    public void SwitchFadeWindow(float windowSize) {
        gameFrozenLayout.setScaleX(windowSize);
        gameFrozenLayout.setScaleY(windowSize);
    }

    public void SetWindowSize(float size) {
        switch (GameStatus.appState) {
            case LOSTGAME_POPUP:
            case LOSTGAME_POPOFF:
                gameLostLayout.setScaleX(size);
                gameLostLayout.setScaleY(size);
                break;
            case WONGAME_POPUP:
            case WONGAME_POPOFF:
                gameWonLayout.setScaleX(size);
                gameWonLayout.setScaleY(size);
                break;
        }
    }

    public void SetScore(int score) {
        String totalPointsStr = "Total points: " + score;
        if (scoreTextView != null)
            scoreTextView.setText(totalPointsStr);
    }

    public void SetScoreScale(float scoreScale) {
        if (scoreTextView != null)
            scoreTextView.setScaleX(scoreScale);
    }

    public void SetScoreColor(int scoreColor) {
        if (scoreTextView != null)
            scoreTextView.setTextColor(scoreColor);
    }
}