package com.BusReservation.utils;
import java.util.ArrayList;
import java.util.List;

public class RequestParamValidationUtils {

    public static List<String> listOfErrors(String fromLocation, String toLocation, String travelDate) {
        List<String> errors = new ArrayList<>();            // instead of if-else if, use separate if for all to get list of errors

        if((fromLocation == null || fromLocation.isBlank()) && (toLocation == null || toLocation.isBlank()) && (travelDate == null || travelDate.isBlank())) {       // if any field are missed
            errors.add("From & To locations and travel date fields are required.");
        }
        if(fromLocation == null || fromLocation.isBlank()) {   // if from location is null/blank
            errors.add("from: Invalid input, From location is required.");
        }
        if(toLocation == null || toLocation.isBlank()) {  // if from location is not null AND not blank AND to location is null/blank. then this will execute
            errors.add("to: Invalid input, To field is required.");
        }
        if(travelDate == null || travelDate.isBlank()) {       // Only String has .isEmpty() & isBlank() methods, so that!
            errors.add("travelDate: Invalid input, Travel date is required");
        }
        if(travelDate != null && !travelDate.matches(RegExPatterns.DATE_REGEX)) {
            errors.add("travelDate: Invalid date input. Travel date must be in a format of either (dd-MM-yyyy) or (dd/MM/yyyy) or (yyyy-MM-dd)");
        }
        return errors;
    }


    public static List<String> listOfErrors(String startDateStr, String endDateStr) {
        List<String> errors = new ArrayList<>();

        if((startDateStr == null || startDateStr.isBlank()) && (endDateStr == null || endDateStr.isBlank())) errors.add("Both starting and ending date is required.");
        if(startDateStr == null || startDateStr.isBlank()) errors.add("Starting date is required.");
        if(endDateStr == null || endDateStr.isBlank()) errors.add("Ending date is required.");
        if((startDateStr != null && !startDateStr.matches(RegExPatterns.DATE_REGEX)) || (endDateStr != null && !endDateStr.matches(RegExPatterns.DATE_REGEX))) errors.add("Invalid date input. Travel date must be in a format of either (dd-MM-yyyy) or (dd/MM/yyyy) or (yyyy-MM-dd)");

        return errors;
    }
}
