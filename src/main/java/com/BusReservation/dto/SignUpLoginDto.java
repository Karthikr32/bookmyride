package com.BusReservation.dto;

import com.BusReservation.utils.RegExPatterns;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SignUpLoginDto {

    @NotBlank(message = "Mobile number is required.")
    @Pattern(regexp = RegExPatterns.MOBILE_REGEX, message = "Invalid mobile number. Officially must starts between 6-9")
    private String mobile;

    @NotBlank(message = "Password is required.")
    @Pattern(regexp = RegExPatterns.USER_PASSWORD_REGEX, message = "Invalid password. Password must be at least 10 characters and include upper, lower, digit, and special character")
    private String password;
}
