package com.snOwmOtion.brickbreaker_try_0.Bonifications;

import com.snOwmOtion.brickbreaker_try_0.GameStatus;

public class IncreaseBallSpeed extends Bonifications {
    public void ApplyBonus() {
        if (GameStatus.score > 200) {
            GameStatus.ballSpeedMultiplier = 9.0f;
        } else if (GameStatus.score > 150) {
            GameStatus.ballSpeedMultiplier = 7.5f;
        } else if (GameStatus.score > 100) {
            GameStatus.ballSpeedMultiplier = 6.0f;
        } else if (GameStatus.score > 50) {
            GameStatus.ballSpeedMultiplier = 4.5f;
        }
    }
}
