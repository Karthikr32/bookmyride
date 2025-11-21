package com.BusReservation.constants;


public enum BookingStatus {
    PENDING("Pending"),
    PROCESSING("Processing"),
    CONFIRMED("Confirmed"),
    CANCELLED("Cancelled"),
    EXPIRED("Expired");

    private final String name;

    BookingStatus(String name) {
        this.name = name;
    }

    public String getBookingStatusName() {
        return this.name;
    }
}
