package com.BusReservation.dto;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BookingListDto {     // 12 + 9 == 21

    private BookingInfo booking;
    private BusInfo bus;

    @Data
    public static final class BookingInfo {    // 16
        private Long id;
        private Long seatsBooked;
        private LocalDateTime bookedAt;
        private LocalDate travelAt;
        private LocalDateTime departureAt;
        private LocalDateTime arrivalAt;
        private String busTicket;
        private String transactionId;
        private String bookingStatus;
        private String paymentStatus;
        private String paymentMethod;
        private  BigDecimal totalCost;
        private String discount;
        private BigDecimal discountAmount;
        private BigDecimal finalCost;
        private LocalDateTime cancelledAt;
    }

    @Data
    public static final class BusInfo {  // 6
        private String busNumber;
        private String busName;
        private String busType;
        private String fromLocation;
        private String toLocation;
        private BigDecimal busFare;
    }
}
