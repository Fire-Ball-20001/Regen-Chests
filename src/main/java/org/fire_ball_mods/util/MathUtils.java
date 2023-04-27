package org.fire_ball_mods.util;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class MathUtils {

    public static boolean isTimeBeforeOnDelta(Instant time, long durationOnMinutes) {
        return Instant.now().getEpochSecond() - time.getEpochSecond() >= Duration.of(durationOnMinutes, ChronoUnit.MINUTES).getSeconds();
    }

    public static Instant getMinTime(List<Instant> times) {
        Instant time = Instant.MAX;
        for (Instant oneTime: times) {
            if(time.isAfter(oneTime)) {
                time = oneTime;
            }
        }
        return time;
    }
}
