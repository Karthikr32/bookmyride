package com.BusReservation.constants;

import lombok.Getter;

public enum SeatType {
    SEATER("Seater"),
    SLEEPER("Sleeper"),
    SEATER_SLEEPER("Seater / Sleeper"),
    NOT_SPECIFIED("Not Specified");

    private final String name;

    SeatType(String name) {
        this.name = name;
    }

    public String getSeatTypeName() {
        return this.name;
    }
}
