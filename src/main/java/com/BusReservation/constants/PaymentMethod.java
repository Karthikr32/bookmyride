package com.BusReservation.constants;

public enum PaymentMethod {
    UPI("UPI"),
    CARD("Card"),
    QR_CODE("QR Code"),
    BANK_TRANSFER("Bank Transfer"),
    NET_BANKING("Net Banking"),
    NONE("None");

    private final String name;

    PaymentMethod(String name) {
        this.name = name;
    }

    public String getPaymentMethodName() {
        return this.name;
    }
}
