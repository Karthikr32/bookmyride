package com.BusReservation.dto;

import com.BusReservation.utils.RegExPatterns;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LocationEntryDto {

    @NotBlank(message = "City input is required.")
    @Pattern(regexp = RegExPatterns.LOCATION_REGEX, message = "Invalid city input. City must starts with capital and followed by sequence for small letters.")
    private String city;

    @NotBlank(message = "State input is required.")
    @Pattern(regexp = RegExPatterns.STATE_REGEX, message = "Invalid state input. Enter the state name that is legally authorized.")
    private String state;

    @NotBlank(message = "Country input is required.")
    @Pattern(regexp = RegExPatterns.COUNTRY_REGEX, message = "Invalid country input. Enter the country that is legally authorized.")
    private String country;
}
