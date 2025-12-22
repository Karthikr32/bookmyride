package com.BusReservation.dto;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BookingPreviewDto {

    private BookingInfo booking;
    private BusInfo bus;
    private PassengerInfo passenger;

    @Data
    public static final class BookingInfo {
        private Long bookingId;
        private LocalDateTime bookedAt;
        private LocalDate travelAt;
        private String discount;
        private BigDecimal discountAmount;
        private BigDecimal totalCost;
        private BigDecimal finalCost;
        private LocalDateTime bookingExpiresAt;
    }

    @Data
    public static final class BusInfo {
        private String busNumber;
        private String busName;
        private String busType;
        private String fromLocation;
        private String toLocation;
        private LocalDateTime departureAt;
        private LocalDateTime arrivalAt;
        private BigDecimal busFare;
    }

    @Data
    public static final class PassengerInfo {
        private String name;
        private String email;
        private String mobile;
        private Long seatsBooked;
    }
}
