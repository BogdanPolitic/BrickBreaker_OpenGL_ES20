package com.example.brickbreaker_try_0.Bonifications;

import java.util.ArrayList;

public class Bonifications {
    protected final int X = 0, Y = 1, Z = 2;
    private ArrayList<Bonifications> bonusList = new ArrayList<Bonifications>();

    public void Init() {
        bonusList.add(new EnlargePlatform());
        bonusList.add(new IncreaseBallSpeed());
    }

    public void ApplyBonuses() {
        if (bonusList == null) return;

        for (Bonifications bonifications : bonusList) {
            bonifications.ApplyBonus();
        }
    }

    public void ApplyBonus() {}
}
