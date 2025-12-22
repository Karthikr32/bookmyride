package com.BusReservation.dto;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BusUserResponseDto {
    private LocalDate travelAt;
    private String busNumber;
    private String busName;
    private String busType;
    private Long capacity;
    private Long availableSeats;
    private String fromLocation;
    private String toLocation;
    private String duration;
    private LocalDateTime departureAt;
    private LocalDateTime arrivalAt;
    private BigDecimal fare;
}
