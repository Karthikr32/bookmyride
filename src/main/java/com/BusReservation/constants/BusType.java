package com.BusReservation.constants;

public enum BusType {     // 13
    AC_SLEEPER("A/C Sleeper"),
    NON_AC_SLEEPER("Non A/C Sleeper"),
    AC_SEATER_SLEEPER("A/C Seater / Sleeper"),
    SEMI_SLEEPER("Non A/C Semi Sleeper"),
    AC_SEMI_SLEEPER("A/C Semi Sleeper"),
    DELUXE_AC("Deluxe A/C Seater"),
    DELUXE_NON_AC("Deluxe Non A/C Seater"),
    ULTRA_DELUXE("Ultra Deluxe A/C Recline Seater"),
    VOLVO_AC("Volvo A/C Premium Seater / Sleeper"),
    SCANIA_AC("Scania A/C Premium Seater / Sleeper"),
    MERCEDES_BENZ("Mercedes-Benz A/C Premium Seater / Sleeper"),
    BHARAT_BENZ_AC("Bharat Benz A/C Sleeper (2+1)"),
    ELECTRIC_AC("Electric A/C Seater"),
    ELECTRIC_NON_AC("Electric Non A/C Seater"),
    MULTI_AXLE("Multi-Axle Luxury A/C Seater / Sleeper");

    private final String name;

    BusType(String name) {
        this.name = name;
    }

    public String getBusTypeName() {
        return name;
    }
}
