package com.BusReservation.dto;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.time.LocalDateTime;


@Data
public class ManagementBusDataDto {

    private BusInfo bus;
    private List<BookingInfo> bookings;


    @Data
    public static final class BusInfo {
        private Long id;
        private String busNumber;
        private String busName;
        private String busType;
        private String acType;
        private String seatType;
        private String stateOfRegistration;
        private String interStatePermitStatus;
        private Long capacity;
        private Long availableSeats;
        private String fromLocation;
        private String toLocation;
        private String duration;
        private BigDecimal busFare;
        private ManagementInfo createdBy;
        private ManagementInfo updatedBy;
    }


    @Data
    public static final class ManagementInfo {
        private Long id;
        private String username;
        private String mobile;
        private String role;
        private LocalDateTime actionAt;
    }


    @Data
    public static final class BookingInfo {
        private Long bookingId;
        private Long passengerId;
        private String passengerName;
        private String passengerMobile;
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
