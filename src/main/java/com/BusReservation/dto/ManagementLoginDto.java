package com.BusReservation.dto;

import com.BusReservation.utils.RegExPatterns;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ManagementLoginDto {

    @NotBlank(message = "Username is required.")
    @Pattern(regexp = RegExPatterns.ADMIN_USERNAME_REGEX, message = "Invalid username. Please check your credentials.")
    private String username;

    @NotBlank(message = "Password is required.")
    @Pattern(regexp = RegExPatterns.ADMIN_PASSWORD_REGEX, message = "Invalid password. Password must be at least 12 characters and include upper, lower, digit, and special character.")
    private String password;
}
