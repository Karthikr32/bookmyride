package com.BusReservation.utils;
import java.time.Duration;

public class DurationFormat {

    public static Duration convertTo(Integer hours, Integer minutes) {
        return Duration.ofHours(hours).plusMinutes(minutes);
    }


    public static String durationToStr(Duration duration) {
        int hour = duration.toHoursPart();
        int minute = duration.toMinutesPart();

        String hr = String.format("%02d", hour);
        String min = String.format("%02d", minute);

        return hr + "h " + min + "m";
    }
}
