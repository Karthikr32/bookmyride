package com.BusReservation.dto;

import com.BusReservation.utils.RegExPatterns;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ManagementSignUpDto {

    @NotBlank(message = "Full name is required.")
    @Pattern(regexp = RegExPatterns.NAME_REGEX, message = "Invalid input. Name must starts with capital letter followed by small letter. Can be single or full name.")
    private String fullName;

    @NotBlank(message = "Gender is required")
    @Pattern(regexp = RegExPatterns.GENDER_REGEX, message = "Invalid input. Only male/female are accepted")
    private String gender;

    @NotBlank(message = "Email ID is required.")
    @Pattern(regexp = RegExPatterns.EMAIL_REGEX, message = "Invalid email, Only gmail & yahoo domains are accepted.")
    private String email;

    @NotBlank(message = "Mobile number is required.")
    @Pattern(regexp = RegExPatterns.MOBILE_REGEX, message = "Invalid mobile number. Officially must starts between 6-9.")
    private String mobile;

    @NotBlank(message = "Password is required")
    @Pattern(regexp = RegExPatterns.ADMIN_PASSWORD_REGEX, message = "Invalid password. Password must be at least 12 characters and include upper, lower, digit, and special character.")
    private String password;
}
