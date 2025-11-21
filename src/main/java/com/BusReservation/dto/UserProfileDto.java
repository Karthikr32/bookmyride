package com.BusReservation.dto;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class UserProfileDto {   // 9

    private Long id;
    private String name;
    private String email;
    private String mobile;
    private String gender;
    private String role;
    private String profileStatus;
    private LocalDateTime passwordLastUpdatedAt;
    private Integer totalBookingsCount;
}
