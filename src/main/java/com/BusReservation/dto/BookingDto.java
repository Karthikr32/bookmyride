package com.BusReservation.dto;
import com.BusReservation.utils.RegExPatterns;
import jakarta.validation.constraints.*;
import lombok.Data;


@Data
public class BookingDto {

    @NotBlank(message = "Travel date is required")
    @Pattern(regexp = RegExPatterns.DATE_REGEX, message = "Invalid date input. Travel date must be in a format of either (dd-MM-yyyy) or (yyyy-MM-dd).")
    private String travelAt;

    @NotBlank(message = "Name is required")
    @Pattern(regexp = RegExPatterns.NAME_REGEX, message = "Invalid format. All name segments must start with an uppercase letter followed by lowercase letters.")
    private String name;

    @NotBlank(message = "Gender is required")
    @Pattern(regexp = RegExPatterns.GENDER_REGEX, message = "Invalid input. Only male/female are accepted.")
    private String gender;

    @NotBlank(message = "Email Id is required")
    @Pattern(regexp = RegExPatterns.EMAIL_REGEX, message = "Invalid domain format. Accepted domains: gmail.com, yahoo.com.")
    private String email;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = RegExPatterns.MOBILE_REGEX, message = "Invalid length or starting digit. Expected a 10-digit number starting from 6–9.")
    private String mobile;

    @NotBlank(message = "Bus number is required")
    @Pattern(regexp = RegExPatterns.BUS_NUMBER_REGEX, message = "Invalid bus number. Please provide official format like (TN01AB1234).")
    private String busNumber;

    @NotNull(message = "Number of seats is required.")
    @Min(value = 1, message = "Seats for Booking should be minimum 1.")
    private Long seatsBooked;
}
