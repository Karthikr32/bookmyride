package com.BusReservation.dto;
import com.BusReservation.utils.RegExPatterns;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class BusDto {            // for ADMIN to create a new bus data  // 12

    // Bus Number may start with
    @NotBlank(message = "Bus number is required.")
    @Pattern(regexp = RegExPatterns.BUS_NUMBER_REGEX, message = "Invalid bus number. Bus number must match the official pattern.")
    private String busNumber;

    @NotBlank(message = "Bus name field is required.")
    @Pattern(regexp = RegExPatterns.BUS_NAME_REGEX, message = "Invalid Bus name. Bus name must start with a capital letter and can include letters, numbers, spaces, and symbols like @, (), /, &, .")
    private String busName;

    @NotBlank(message = "Bus type field is required.")
    @Pattern(regexp = RegExPatterns.BUS_TYPE_REGEX, message = "Invalid bus type, Enter the type of bus that is legally authorized in database.")
    private String busType;

    @NotBlank(message = "State of registration is required.")
    @Pattern(regexp = RegExPatterns.STATE_REGEX, message = "Invalid state, Enter the state name that is legally authorized.")
    private String stateOfRegistration;

    @NotNull(message = "Permit field should not be empty.")
    @Pattern(regexp = RegExPatterns.PERMIT_STATUS_REGEX, message = "Must use _ to join 2 words for internal permit field.")
    private String interStatePermitStatus;    // permitted/not_permitted

    @NotNull(message = "Capacity field is empty.")
    @Min(value = 15, message = "Capacity count must greater than 15.")
    private Long capacity;

    @NotBlank(message = "Pick up location is required.")
    @Pattern(regexp = RegExPatterns.LOCATION_REGEX, message = "Pickup location must starts with capital and followed by sequence for small letters.")
    private String fromLocation;

    @NotBlank(message = "Destination field is required.")
    @Pattern(regexp = RegExPatterns.LOCATION_REGEX, message = "Drop location must starts with capital and followed by sequence for small letters.")
    private String toLocation;

    @NotNull(message = "Hours data required.")
    @Min(0)
    @Max(value = 12, message = "Hours will be 0 to 12.")
    private Integer hours;           // for duration

    @NotNull(message = "Minutes data required.")
    @Min(0)
    @Max(value = 59, message = "Minutes will be 0 to 59.")
    private Integer minutes;        // for duration

    @NotBlank(message = "Departure at field is required.")
    @Pattern(regexp = RegExPatterns.DEPARTURE_AT_REGEX, message = "Departure at must be in (HH:mm:ss) format.")   // HH:mm:ss
    private String departureAt;

    @NotNull(message = "Fare amount field is required.")           // if null
    @DecimalMax(value = "9999.99", message = "Bus fare is too high.")    // if exceeds the 9999.99 limit
    @Digits(integer = 4, fraction = 2, message = "Fare must be a valid number with up to 4 digits before the decimal and up to 2 digits after (e.g., 100, 9999.99, or 75.5).")
    private BigDecimal fare;
}


// @NotNull & @Min or @Max for Numbers
// @NotBlank & @Size for String  checks for ""/" "