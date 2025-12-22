package com.BusReservation.utils;

import com.BusReservation.constants.ResponseStatus;

public class TimeParserUtils {

    public static ServiceResponse<Integer[]> timeRangeParser(String timeRange) {
        Integer[] parsedTimeRange = new Integer[2];
        String[] split = timeRange.split("-");

        try {
            parsedTimeRange[0] = Integer.parseInt(split[0].trim());
            parsedTimeRange[1] = Integer.parseInt(split[1].trim());
            return new ServiceResponse<>(ResponseStatus.SUCCESS, parsedTimeRange);
        }
        catch (IllegalArgumentException e) {
            return new ServiceResponse<>(ResponseStatus.BAD_REQUEST, "Invalid input. Time range should always be numeric in format of 00-23");
        }
    }
}
