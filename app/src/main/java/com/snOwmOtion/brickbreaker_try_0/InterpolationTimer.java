package com.snOwmOtion.brickbreaker_try_0;

import java.util.HashMap;

public class InterpolationTimer {
    private static HashMap<Long, InterpolationTimer> timers = new HashMap<>();
    private static long lastId = Long.MIN_VALUE;
    private long duration;
    private long timeLapsed;

    private InterpolationTimer(long duration) {
        this.duration = duration;
        timeLapsed = 0;
    }

    public static long AddTimer(long duration) {
        timers.put(lastId, new InterpolationTimer(duration));
        return lastId++;
    }

    public static float GetPercent(long id) {
        if (!timers.containsKey(id)) return 1.0f;

        InterpolationTimer iT = timers.get(id);
        if (iT.timeLapsed == iT.duration)
            timers.remove(id);
        return (float)((double)iT.timeLapsed / iT.duration);
    }

    public static void Update() {
        if (timers.size() == 0) return;
        for (InterpolationTimer iT : timers.values()) {
            iT.timeLapsed = Math.min(iT.timeLapsed + GameStatus.deltaTime, iT.duration);
        }
    }

    public static void ClearAllTimers() {
        timers = new HashMap<>();
    }
}
