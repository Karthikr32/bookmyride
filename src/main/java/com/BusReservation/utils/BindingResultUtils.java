package com.BusReservation.utils;

import org.springframework.validation.BindingResult;
import java.util.List;

public class BindingResultUtils {

    public static List<String> getListOfStr(BindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream().map(error -> error.getField() + ": " + error.getDefaultMessage()).toList();
    }
}
