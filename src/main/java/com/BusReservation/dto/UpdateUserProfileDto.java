package com.BusReservation.dto;

import com.BusReservation.utils.RegExPatterns;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateUserProfileDto {

    @NotBlank(message = "Name is required")
    @Pattern(regexp = RegExPatterns.NAME_REGEX, message = "Invalid input. Name must starts with capital letter followed by small letter")
    private String name;

    @NotBlank(message = "Gender is required")
    @Pattern(regexp = RegExPatterns.GENDER_REGEX, message = "Invalid input. Only male/female are accepted")
    private String gender;

    @NotBlank(message = "Email Id is required")
    @Pattern(regexp = RegExPatterns.EMAIL_REGEX, message = "Invalid email, Only gmail & yahoo domains are accepted")
    private String email;
}
