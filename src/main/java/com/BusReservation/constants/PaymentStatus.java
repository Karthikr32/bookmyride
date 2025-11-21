package com.BusReservation.constants;

public enum PaymentStatus {
    PAID("Paid"),       // if success payment
    UNPAID("Not Paid"),     // no attempt made for payment | this was the initial step
    PENDING("Pending"),    // attempt made but waiting for outcome whether the payment might success|failed
    FAILED("Failed");     // payment failed due to timeout (5 min)

    private final String name;

    PaymentStatus(String name) {
        this.name = name;
    }

    public String getPaymentStatusName() {
        return this.name;
    }
}
