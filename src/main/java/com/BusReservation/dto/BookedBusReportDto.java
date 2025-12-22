package com.BusReservation.dto;
import com.BusReservation.constants.AcType;
import com.BusReservation.constants.BusType;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class BookedBusReportDto {

    private Long busId;
    private String busNumber;
    private String busName;
    private String busType;
    private String acOrNonAc;
    private Long totalBookings;
    private BigDecimal totalRevenue;
    private String occupancy;
    private String availability;


    // Avoid @AllArgsConstructor/@NoArgsConstructor — breaks JPQL value assignment
    public BookedBusReportDto(Long busId, String busNumber, String busName, BusType busType, AcType acType, Long totalBookings, BigDecimal totalRevenue, Long occupancy, Long availability) {
        this.busId = busId;
        this.busNumber = busNumber;
        this.busName = busName;
        this.busType = busType.getBusTypeName();
        this.acOrNonAc = acType.name();
        this.totalBookings = totalBookings;
        this.totalRevenue = totalRevenue;
        this.occupancy = occupancy + "%";
        this.availability = availability + "%";
    }
}
