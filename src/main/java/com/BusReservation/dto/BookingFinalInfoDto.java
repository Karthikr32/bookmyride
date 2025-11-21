package com.BusReservation.dto;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BookingFinalInfoDto {    // 24

    private BookingInfo booking;
    private BusInfo bus;
    private PassengerInfo passenger;


    @Data
    public static final class BookingInfo {     // 12
        private Long bookingId;
        private LocalDateTime bookedAt;
        private LocalDate travelAt;
        private String bookingStatus;
        private String paymentStatus;
        private String paymentMethod;
        private String busTicket;
        private String transactionId;
        private String discount;      // discount + '%'
        private BigDecimal discountAmount;
        private BigDecimal totalCost;
        private BigDecimal finalCost;
    }

    @Data
    public static final class BusInfo {    // 11
        private String busNumber;
        private String busName;
        private String busType;    // modified
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
    public static final class PassengerInfo {    // 5
        private String name;
        private String email;
        private String mobile;
        private String gender;
        private Long seatsBooked;
    }
}
