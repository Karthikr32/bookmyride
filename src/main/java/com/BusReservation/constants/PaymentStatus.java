package com.BusReservation.constants;

public enum PaymentStatus {
    PAID("Paid"),
    UNPAID("Not Paid"),
    PENDING("Pending"),
    FAILED("Failed");

    private final String name;

    PaymentStatus(String name) {
        this.name = name;
    }

    public String getPaymentStatusName() {
        return this.name;
    }
}
