package com.BusReservation.utils;

public class NormalizeStringUtils {

    public static String getNormalize(String str) {
        return str.trim().toUpperCase().replaceAll("[_ -]", "_");
    }
}
