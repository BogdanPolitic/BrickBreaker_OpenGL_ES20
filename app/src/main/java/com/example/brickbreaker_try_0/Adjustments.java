package com.example.brickbreaker_try_0;

public class Adjustments {
    protected static final int X = 0, Y = 1, Z = 2;

    public static void AdjustBallDirection() {
        if (GameStatus.ballDirection[Y] == 0.0f
            || Math.abs(GameStatus.ballDirection[Y] / GameStatus.ballDirection[X])
                < ValueSheet.ballDirectionFlatLimit) {

            GameStatus.ballDirection[X] =
                    Math.signum(GameStatus.ballDirection[X])
                    * ValueSheet.ballDirectionFlatLimit
                    * Math.abs(GameStatus.ballDirection[Y]
            );
        }
    }
}
