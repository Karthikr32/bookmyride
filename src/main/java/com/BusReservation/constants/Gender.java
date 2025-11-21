package com.BusReservation.constants;

public enum Gender {
    MALE("Male"),
    FEMALE("Female"),
    NOT_SPECIFIED("Not Specified");

    private final String name;

    Gender(String name) {
        this.name = name;
    }

    public String getGenderName() {
        return this.name;
    }
}
