package com.BusReservation.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


// This will come after user presses "CONTINUE BOOKING". Now they need to press "CONFIRM BOOKING" button to confirm the booking by DB and they will receive the final booking statement with ticket, transaction id, etc...
@Data
public class BookingSummaryDto {      // 22

    private BookingInfo booking;
    private BusInfo bus;
    private PassengerInfo passenger;


    @Data
    public static final class BookingInfo {     // 10
        private Long bookingId;
        private LocalDateTime bookedAt;
        private LocalDate travelAt;
        private String bookingStatus;
        private String paymentStatus;
        private String discount;      // discount + '%'
        private BigDecimal discountAmount;
        private BigDecimal totalCost;
        private BigDecimal finalCost;
        private LocalDateTime bookingExpiresAt;
    }

    @Data
    public static final class BusInfo {    // 9
        private String busNumber;
        private String busName;
        private String busType;    // modified
        private String duration;
        private String fromLocation;
        private String toLocation;
        private LocalDateTime departureAt;
        private LocalDateTime arrivalAt;
        private BigDecimal busFare;
    }

    @Data
    public static final class PassengerInfo {    // 5
        private String name;
        private String email;
        private String mobile;
        private String gender;
        private Long seatsBooked;
    }
}
