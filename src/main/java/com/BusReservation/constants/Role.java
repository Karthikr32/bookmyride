package com.BusReservation.constants;

public enum Role {
    GUEST("Guest"),
    USER("User"),
    ADMIN("Admin");

    private final String name;

    Role(String name) {
        this.name = name;
    }

    public String getRoleName() {
        return this.name;
    }
}
