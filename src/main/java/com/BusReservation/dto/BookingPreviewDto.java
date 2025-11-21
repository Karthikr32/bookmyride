package com.BusReservation.dto;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

// this will show to display if user press "CONTINUE" button, then user will see the data given below and need to press "CONTINUE BOOKING" button in order to process at background, they will receive the BookingSummary dto for confirmation
@Data
public class BookingPreviewDto {   // 20

    private BookingInfo booking;
    private BusInfo bus;
    private PassengerInfo passenger;

    @Data
    public static final class BookingInfo {   // 8
        private Long bookingId;
        private LocalDateTime bookedAt;
        private LocalDate travelAt;
        private String discount;      // discount + '%'
        private BigDecimal discountAmount;
        private BigDecimal totalCost;
        private BigDecimal finalCost;
        private LocalDateTime bookingExpiresAt;
    }

    @Data
    public static final class BusInfo {   // 8
        private String busNumber;
        private String busName;
        private String busType;
        private String fromLocation;
        private String toLocation;
        private LocalDateTime departureAt;   // +2
        private LocalDateTime arrivalAt;
        private BigDecimal busFare;
    }

    @Data
    public static final class PassengerInfo {   // 4
        private String name;
        private String email;
        private String mobile;
        private Long seatsBooked;
    }
}
