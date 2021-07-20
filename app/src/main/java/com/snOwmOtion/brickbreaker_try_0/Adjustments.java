package com.snOwmOtion.brickbreaker_try_0;

public class Adjustments {
    protected static final int X = 0, Y = 1, Z = 2;

    public static void AdjustBallDirection() {
        if (GameStatus.ballDirection[X] == 0.0f) return;

        float currentSlope = Math.abs(GameStatus.ballDirection[Y] / GameStatus.ballDirection[X]);

        if (GameStatus.ballDirection[Y] == 0.0f
            || currentSlope < ValueSheet.ballDirectionFlatLimit) {

            GameStatus.ballDirection[X] =
                    Math.signum(GameStatus.ballDirection[X])
                    * (currentSlope + (ValueSheet.ballDirectionFlatLimit - currentSlope) * 0.01f)
                    //* ValueSheet.ballDirectionFlatLimit
                    * Math.abs(GameStatus.ballDirection[Y]
            );
        }
    }
}
