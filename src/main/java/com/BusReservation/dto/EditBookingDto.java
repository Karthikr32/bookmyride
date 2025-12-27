package com.BusReservation.dto;

import com.BusReservation.utils.RegExPatterns;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import java.time.LocalDate;

@Data
public class EditBookingDto {

    @NotNull(message = "Travel date is required")
    @Pattern(regexp = RegExPatterns.DATE_REGEX, message = "Invalid date. Travel date must be in a format of either (dd-MM-yyyy) or (yyyy-MM-dd).")
    private String travelAt;

    @NotBlank(message = "Name is required")
    @Pattern(regexp = RegExPatterns.NAME_REGEX, message = "Invalid format. All name segments must start with an uppercase letter followed by lowercase letters.")
    private String name;

    @NotBlank(message = "Gender is required")
    @Pattern(regexp = RegExPatterns.GENDER_REGEX, message = "Only male/female are accepted.")
    private String gender;

    @NotBlank(message = "Email Id is required")
    @Pattern(regexp = RegExPatterns.EMAIL_REGEX, message = "Invalid domain format. Accepted domains: gmail.com, yahoo.com.")
    private String email;

    @NotNull(message = "Number of seats is required")
    @Min(value = 1, message = "Seats for Booking should be minimum 1.")
    private Long seatsBooked;
}
