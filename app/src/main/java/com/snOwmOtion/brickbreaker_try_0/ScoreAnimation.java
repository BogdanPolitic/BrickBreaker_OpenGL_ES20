package com.snOwmOtion.brickbreaker_try_0;

public class ScoreAnimation {
    public float currentFont;   // according to THIS CURRENT score text
    public int currentColor;    // according to THIS CURRENT score text
    private boolean ascending;
    private final long startAnimationTime;

    public ScoreAnimation() {
        this.currentFont = ValueSheet.scoreFontScaleX.min;
        ascending = true;
        startAnimationTime = GameStatus.time;
    }

    public void UpdateScoreFontSize() {
        if (RequiresDestruction()) {
            currentFont = ValueSheet.scoreFontScaleX.min;
            currentColor = ValueSheet.scoreColor.min;
            return;
        }

        float updatePercentage;
        long timeLapsed = GameStatus.time - startAnimationTime;
        if (timeLapsed >= ValueSheet.scoreAnimationTime / 2) {
            if (ascending)
                ascending = false;
            updatePercentage = (float)(timeLapsed - ValueSheet.scoreAnimationTime / 2) / (float)(ValueSheet.scoreAnimationTime / 2);
        } else
            updatePercentage = (float)timeLapsed / (float)(ValueSheet.scoreAnimationTime / 2);

        if (ascending) {
            currentFont = ValueSheet.scoreFontScaleX.min + updatePercentage * (ValueSheet.scoreFontScaleX.max - ValueSheet.scoreFontScaleX.min);
            currentColor = (int)(ValueSheet.scoreColor.min + updatePercentage * (ValueSheet.scoreColor.max - ValueSheet.scoreColor.min));
        }
        else {
            currentFont = ValueSheet.scoreFontScaleX.max - updatePercentage * (ValueSheet.scoreFontScaleX.max - ValueSheet.scoreFontScaleX.min);
            currentColor = (int)(ValueSheet.scoreColor.max - updatePercentage * (ValueSheet.scoreColor.max - ValueSheet.scoreColor.min));
        }

    }

    public boolean RequiresDestruction() {
        return GameStatus.time - startAnimationTime > ValueSheet.scoreAnimationTime;
    }
}
