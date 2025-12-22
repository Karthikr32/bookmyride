package com.BusReservation.dto;
import com.BusReservation.constants.Gender;
import com.BusReservation.constants.Role;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
public class BookedAppUserReportDto {

    private Long appUserId;
    private String name;
    private String email;
    private String mobile;
    private String gender;
    private String role;
    private Long totalBookings;
    private BigDecimal totalRevenue;
    private LocalDateTime recentBookedAt;


    // Avoid @AllArgsConstructor/@NoArgsConstructor — breaks JPQL value assignment
    public BookedAppUserReportDto(Long appUserId, String name, String email, String mobile, Gender gender, Role role, Long totalBookings, BigDecimal totalRevenue, LocalDateTime recentBookedAt) {
        this.appUserId = appUserId;
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.gender = gender.getGenderName();
        this.role = role.getRoleName();
        this.totalBookings = totalBookings;
        this.totalRevenue = totalRevenue;
        this.recentBookedAt = recentBookedAt;
    }
}
