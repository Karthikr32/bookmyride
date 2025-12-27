package com.BusReservation.dto;

import com.BusReservation.utils.RegExPatterns;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ConfirmBookingDto {

    @NotNull(message = "Payment method is required.")
    @NotBlank(message = "Payment method is required.")
    @Pattern(regexp = RegExPatterns.PAYMENT_METHOD_REGEX, message = "Only Card/UPI/Bank Transfer/Net Banking or Qr code are accepted.")
    private String paymentMethod;
}
