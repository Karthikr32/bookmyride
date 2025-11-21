package com.BusReservation.dto;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ManagementBookingDataDto {     // 32

    private BookingInfo booking;
    private BusInfo bus;
    private PassengerInfo passenger;

    @Data
    public static final class BookingInfo {    // 15
        private Long id;
        private LocalDateTime bookedAt;
        private LocalDate travelAt;
        private String bookingStatus;
        private String  paymentStatus;
        private String paymentMethod;
        private String busTicket;
        private String transactionId;
        private String discount;
        private BigDecimal discountAmount;
        private BigDecimal totalCost;
        private BigDecimal finalCost;
        private LocalDateTime bookingEditedAt;
        private LocalDateTime bookingExpiresAt;
        private LocalDateTime canceledAt;
    }

    @Data
    public static final class BusInfo {     // 12
        private Long id;
        private String busNumber;
        private String busName;
        private String busType;
        private String acType;
        private String seatType;
        private String duration;
        private String fromLocation;
        private String toLocation;
        private LocalDateTime departureAt;
        private LocalDateTime arrivalAt;
        private BigDecimal busFare;
    }

    @Data
    public static final class PassengerInfo {   // 7
        private Long id;
        private String name;
        private String email;
        private String mobile;
        private String gender;
        private String role;
        private Long seatsBooked;
    }

}
