package com.BusReservation.constants;


public enum Country {
    INDIA("India");

    private final String name;

    Country(String name) {
        this.name = name;
    }

    public String getCountryName() {
        return name;
    }
}
