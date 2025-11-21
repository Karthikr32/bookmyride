package com.BusReservation.utils;

import com.BusReservation.constants.ResponseStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Map;


public class DateParser {


    public static ServiceResponse<LocalDate> validateAndParseDate(String userDate, DateTimeFormatter format) {
        LocalDate date;    // NOTE: 2 ways of input --> 1.) via frontend date input (yyyy-MM-dd) | 2.) via Postman (for Testing - (dd-MM-yyyy))
        try {
            if (userDate.matches(RegExPatterns.HUMAN_READABLE_DATE_REGEX)) {   // for type 2 (dd-MM-yyyy | dd/MM/yyyy)
                date  = LocalDate.parse(userDate, format);
                if(date.isBefore(LocalDate.now())) {        // if travel date is in past
                    return new ServiceResponse<>(ResponseStatus.BAD_REQUEST, "Travel date/time cannot be in the past!");
                }
                return new ServiceResponse<>(ResponseStatus.SUCCESS, date);
            }

            date = LocalDate.parse(userDate, DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT));        // for type 1 (yyyy-MM-dd)
            if(date.isBefore(LocalDate.now())) {        // if travel date is in past
                return new ServiceResponse<>(ResponseStatus.BAD_REQUEST, "Travel date/time cannot be in the past!");
            }
            return new ServiceResponse<>(ResponseStatus.SUCCESS, date);

        } catch (DateTimeParseException e) {
            return new ServiceResponse<>(ResponseStatus.BAD_REQUEST, "Invalid date input. Please check and try again");
        }
    }



    public static ServiceResponse<LocalDate> parseDate(String dateStr, DateTimeFormatter format) {
        LocalDate date;
        try {
            if(dateStr.matches(RegExPatterns.HUMAN_READABLE_DATE_REGEX)) {     // type-1 (dd/MM/yyyy | dd-MM-yyyy)
                date = LocalDate.parse(dateStr, format);
                return new ServiceResponse<>(ResponseStatus.SUCCESS, date);
            }
            date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT));
            return new ServiceResponse<>(ResponseStatus.SUCCESS, date);
        }
        catch (DateTimeParseException e) {
            return new ServiceResponse<>(ResponseStatus.BAD_REQUEST, "Invalid date input. Please check and try again");
        }
    }


    public static ServiceResponse<Map<String, LocalDateTime>> getBothDateTime(String startDate, String endDate) {
        DateTimeFormatter formatter1 = startDate.contains("-") ? DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT) : DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);
        DateTimeFormatter formatter2 = endDate.contains("-") ? DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT) : DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

        ServiceResponse<LocalDate> startDateResponse = DateParser.parseDate(startDate, formatter1);
        ServiceResponse<LocalDate> endDateResponse = DateParser.parseDate(endDate, formatter2);

        if(startDateResponse.getStatus() == ResponseStatus.BAD_REQUEST) return new ServiceResponse<>(startDateResponse.getStatus(), startDateResponse.getMessage());
        else if(endDateResponse.getStatus() == ResponseStatus.BAD_REQUEST) return new ServiceResponse<>(endDateResponse.getStatus(), endDateResponse.getMessage());

        LocalDateTime startDateTime = startDateResponse.getData().atStartOfDay();
        LocalDateTime endDateTime = endDateResponse.getData().atTime(23, 59, 59);

        Map<String, LocalDateTime> map = Map.of("startDateTime", startDateTime, "endDateTime", endDateTime);
        return new ServiceResponse<>(ResponseStatus.SUCCESS, map);
    }

}
