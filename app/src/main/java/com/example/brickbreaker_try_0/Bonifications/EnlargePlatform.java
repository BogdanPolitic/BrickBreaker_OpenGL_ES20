package com.example.brickbreaker_try_0.Bonifications;

import com.example.brickbreaker_try_0.*;

public class EnlargePlatform extends Bonifications {
    public void ApplyBonus() {
        Platform platform = (Platform)MyGLRenderer.shapes.get("Platform");
        if (GameStatus.score > 200) {
            platform.ChangeSizeCoord(X, 0.8f);
        } else if (GameStatus.score > 150) {
            platform.ChangeSizeCoord(X, 0.7f);
        } else if (GameStatus.score > 100) {
            platform.ChangeSizeCoord(X, 0.6f);
        } else if (GameStatus.score > 50) {
            platform.ChangeSizeCoord(X, 0.5f);
        }
    }
}
