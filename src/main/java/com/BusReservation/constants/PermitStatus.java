package com.BusReservation.constants;

public enum PermitStatus {
    PERMITTED("Permitted"),
    NOT_PERMITTED("Not Permitted");

    private final String name;

    PermitStatus(String name) {
        this.name = name;
    }

    public String getPermitStatusName() {
        return this.name;
    }
}
