package com.example.brickbreaker_try_0;

import java.util.Random;

public class MyRandom {
    private static Random random = new Random();

    public static Powerup.ShapeType GetRandomShapeType() {
        int randomTypeIndex = Math.abs(random.nextInt()) % Powerup.ShapeType.values().length;
        return Powerup.ShapeType.values()[randomTypeIndex];
    }

    public static Powerup.FunctionalType GetRandomFunctionalType() {
        int randomTypeIndex = Math.abs(random.nextInt()) % Powerup.FunctionalType.values().length;
        return Powerup.FunctionalType.values()[randomTypeIndex];
    }

    public static float[] GetRandomPowerupColor() {
        int randomColorIndex = Math.abs(random.nextInt()) % ValueSheet.powerupColorsPool.length;
        return ValueSheet.powerupColorsPool[randomColorIndex];
    }

    // it has a "chance" percentage (%) chance to return true, and thus a (100 - "chance")% chance to return false
    public static boolean ChanceForFact(int chance) {
        if (chance >= 100) return true;
        if (chance <= 0) return false;
        return random.nextInt() <= chance;
    }
}
