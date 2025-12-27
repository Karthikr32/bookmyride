package com.BusReservation.dto;

import com.BusReservation.utils.RegExPatterns;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateUserProfileDto {

    @NotBlank(message = "Name is required.")
    @Pattern(regexp = RegExPatterns.NAME_REGEX, message = "Invalid format. All name segments must start with an uppercase letter followed by lowercase letters.")
    private String name;

    @NotBlank(message = "Gender is required.")
    @Pattern(regexp = RegExPatterns.GENDER_REGEX, message = "Invalid input. Only male/female are accepted.")
    private String gender;

    @NotBlank(message = "Email Id is required.")
    @Pattern(regexp = RegExPatterns.EMAIL_REGEX, message = "Invalid domain format. Accepted domains: gmail.com, yahoo.com.")
    private String email;
}
