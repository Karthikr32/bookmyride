package com.BusReservation.utils;

public class MockDataUtils {

    public static String getDummyName(String mobile) {
        String unique = mobile.trim().substring(5);
        return "USER_" + unique;
    }

    public static String getDummyEmail(String mobile) {
        return mobile + "@dummy.com";
    }

    public static String getDummyPassword() {
        return "GUEST_NO_AUTH";
    }
}
