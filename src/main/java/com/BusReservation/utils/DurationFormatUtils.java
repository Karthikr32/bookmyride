package com.BusReservation.utils;
import java.time.Duration;

public class DurationFormatUtils {

    public static Duration convertTo(Integer hours, Integer minutes) {
        return Duration.ofHours(hours).plusMinutes(minutes);
    }


    public static String durationToStr(Duration duration) {
        int hour = duration.toHoursPart();
        int minute = duration.toMinutesPart();

        String hrStr = String.format("%02d", hour);
        String minStr = String.format("%02d", minute);
        return hrStr + "h " + minStr + "m";
    }
}
