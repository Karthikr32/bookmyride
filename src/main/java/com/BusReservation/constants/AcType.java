package com.BusReservation.constants;

public enum AcType {
    AC("A/C"),
    NON_AC("Non A/C"),
    NOT_SPECIFIED("Not Specified");

    private final String name;

    AcType(String name) {
        this.name = name;
    }

    public String getAcTypeName() {
        return this.name;
    }
}
