package com.BusReservation.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ManagementProfileDto {  // 8

    private Long id;
    private String fullName;
    private String gender;
    private String username;
    private String mobile;
    private String email;
    private String role;
    private LocalDateTime passwordLastUpdatedAt;
}
