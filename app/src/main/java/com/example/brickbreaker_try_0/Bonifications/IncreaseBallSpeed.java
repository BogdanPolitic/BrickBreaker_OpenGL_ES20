package com.example.brickbreaker_try_0.Bonifications;

import com.example.brickbreaker_try_0.GameStatus;

public class IncreaseBallSpeed extends Bonifications {
    public void ApplyBonus() {
        if (GameStatus.score > 200) {
            GameStatus.ballSpeed = 0.03f;
        } else if (GameStatus.score > 150) {
            GameStatus.ballSpeed = 0.025f;
        } else if (GameStatus.score > 100) {
            GameStatus.ballSpeed = 0.02f;
        } else if (GameStatus.score > 50) {
            GameStatus.ballSpeed = 0.015f;
        }
    }
}
