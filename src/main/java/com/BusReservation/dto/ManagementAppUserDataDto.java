package com.BusReservation.dto;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ManagementAppUserDataDto {

    private PassengerInfo passenger;
    private List<BookingInfo> bookings;

    @Data
    public static final class PassengerInfo {
        private Long id;
        private String name;
        private String email;
        private String mobile;
        private String gender;
        private String role;
        private String profileStatus;
        private LocalDateTime registerAt;
        private LocalDateTime profileLastUpdate;
        private Integer totalBookingsCount;
    }

    @Data
    public static final class BookingInfo {
        private Long bookingId;
        private Long busId;
        private String busNumber;
        private String busName;
        private LocalDateTime bookedAt;
        private Long seatsBooked;
        private LocalDate travelAt;
        private LocalDateTime departureAt;
        private LocalDateTime arrivalAt;
        private String busTicket;
        private String transactionId;
        private String bookingStatus;
        private String paymentStatus;
        private String paymentMethod;
        private BigDecimal finalCost;
    }
}
