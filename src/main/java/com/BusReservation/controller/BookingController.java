package com.BusReservation.controller;
import com.BusReservation.constants.Code;
import com.BusReservation.constants.PaymentMethod;
import com.BusReservation.constants.ResponseStatus;
import com.BusReservation.dto.*;
import com.BusReservation.model.Bus;
import com.BusReservation.model.AppUser;
import com.BusReservation.service.ManagementService;
import com.BusReservation.service.BookingService;
import com.BusReservation.service.BusService;
import com.BusReservation.service.AppUserService;
import com.BusReservation.utils.*;
import jakarta.persistence.OptimisticLockException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.*;


@RestController
@RequiredArgsConstructor
public class BookingController {

    /* LOGIC:
    -> An user make booking ==> validate by bindingResult ==> check whether the user new/existing ==> if(new) -> register/save into AppUser repo |
    -> An user make booking ==> validate by bindingResult ==> check whether the user new/existing ==> if(new) -> register/save into AppUser repo |
    -> else proceed booking logic ==>
    */

    private final AppUserService appUserService;
    private final BusService busService;
    private final BookingService bookingService;
    private final ManagementService managementService;


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/management/bookings")
    public ResponseEntity<Map<String, Object>> getBookingsList(@RequestParam(required = false, defaultValue = "1") Integer page, @RequestParam(required = false, defaultValue = "10") Integer size,
                                                               @RequestParam(required = false, defaultValue = "id") String sortBy, @RequestParam(required = false, defaultValue = "ASC") String sortDir,
                                                               @RequestParam(required = false) String keyword) {

        List<String> bookingFields = List.of("id", "fromLocation", "toLocation", "seatsBooked", "bookedAt", "travelAt", "departureAt", "arrivalAt", "bookingExpiresAt", "discountPct", "totalCost", "discountAmount", "finalCost");
        var validationResult = PaginationRequest.getRequestValidationForPagination(page, size, sortBy, sortDir, bookingFields);
        if(validationResult != null) return validationResult;

        Sort.Direction sort;
        if(sortDir.equalsIgnoreCase("ASC")) sort = Sort.Direction.ASC;
        else sort = Sort.Direction.DESC;

        final PaginationRequest request = new PaginationRequest(page, size, sortBy, sort);
        ServiceResponse<ApiPageResponse<List<ManagementBookingDataDto>>> response = bookingService.getAllBookingList(request, keyword);

        if (response.getStatus() == ResponseStatus.BAD_REQUEST) return ResponseEntity.badRequest().body(ApiResponse.statusMsg(HttpStatus.BAD_REQUEST.value(), Code.VALIDATION_FAILED, response.getMessage()));
        return ResponseEntity.ok(ApiResponse.successStatusMsgData(HttpStatus.OK.value(), response.getMessage(), response.getData()));
    }


    @PostMapping("/public/bookings")
    public ResponseEntity<Map<String, Object>> makeABooking(@Valid @RequestBody BookingDto bookingDto, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) return ResponseEntity.badRequest().body(ApiResponse.errorStatusMsgErrors(HttpStatus.BAD_REQUEST.value(), Code.VALIDATION_FAILED, BindingResultUtils.getListOfStr(bindingResult)));

        DateTimeFormatter format = (bookingDto.getTravelAt().contains("-")) ? DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT) : DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);
        ServiceResponse<LocalDate> dateResponse = DateParser.validateAndParseDate(bookingDto.getTravelAt(), format);

        if(dateResponse.getStatus() == ResponseStatus.BAD_REQUEST) return ResponseEntity.badRequest().body(ApiResponse.statusMsg(HttpStatus.BAD_REQUEST.value(), Code.VALIDATION_FAILED, dateResponse.getMessage()));
        LocalDate date = dateResponse.getData();

        boolean isExists = managementService.ifMobileNumberExists(bookingDto.getMobile());
        if(isExists) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.statusMsg(HttpStatus.FORBIDDEN.value(), Code.ACCESS_DENIED, "Access denied for this role."));

        Optional<AppUser> existingAppUser = appUserService.fetchByMobile(bookingDto.getMobile());

        ServiceResponse<AppUser> appUserServiceResponse = appUserService.createOrUpdateAppUserForBooking(existingAppUser, bookingDto);
        if(appUserServiceResponse.getStatus() == ResponseStatus.BAD_REQUEST) return ResponseEntity.badRequest().body(ApiResponse.statusMsg(HttpStatus.BAD_REQUEST.value(), Code.VALIDATION_FAILED, appUserServiceResponse.getMessage()));
        else if(appUserServiceResponse.getStatus() == ResponseStatus.CONFLICT) return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.statusMsg(HttpStatus.CONFLICT.value(), Code.DUPLICATE_ENTRY, appUserServiceResponse.getMessage()));
        AppUser appUser = appUserServiceResponse.getData();

        Optional<Bus> bookedBus = busService.fetchByBusNumber(bookingDto.getBusNumber());
        if(bookedBus.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.statusMsg(HttpStatus.NOT_FOUND.value(), Code.NOT_FOUND, "No bus data found for the given bus number. Please check the bus number and try again"));

        ServiceResponse<BookingPreviewDto> response;
        try {
            response = bookingService.createNewBooking(appUser, bookedBus.get(), bookingDto, date);
        }
        catch (OptimisticLockException | ObjectOptimisticLockingFailureException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.statusMsg(HttpStatus.CONFLICT.value(), Code.MODIFIED_BY_OTHER, "Unfortunately, your booking could not be completed as seats were just booked by others. Please try again with updated availability."));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.statusMsg(HttpStatus.INTERNAL_SERVER_ERROR.value(), Code.INTERNAL_SERVER_ERROR, "Booking failed due to internal server problem. Please try again later."));
        }
        if(response.getStatus() == ResponseStatus.BAD_REQUEST) return ResponseEntity.ok(ApiResponse.statusMsg(HttpStatus.BAD_REQUEST.value(), Code.VALIDATION_FAILED, response.getMessage()));
        else if(response.getStatus() == ResponseStatus.INSUFFICIENT) return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.statusMsg(HttpStatus.CONFLICT.value(), Code.INSUFFICIENT_SEATS, response.getMessage()));
        else if(response.getStatus() == ResponseStatus.CONFLICT) return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.statusMsg(HttpStatus.CONFLICT.value(), Code.MODIFIED_BY_OTHER, response.getMessage()));
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.successStatusMsgData(HttpStatus.CREATED.value(), response.getMessage(), response.getData()));
    }


    // This method only after the new booking and before continue method...
    @PutMapping("/public/bookings/{id}/edit")
    public ResponseEntity<Map<String, Object>> editUserDataInBooking(@PathVariable Long id, @Valid @RequestBody EditBookingDto bookingDto, BindingResult bindingResult) {
        if(id == null || id <= 0) return ResponseEntity.badRequest().body(ApiResponse.statusMsg(HttpStatus.BAD_REQUEST.value(), Code.VALIDATION_FAILED, "Invalid Booking ID. Please check and try again"));

        if(bindingResult.hasErrors()) return ResponseEntity.badRequest().body(ApiResponse.errorStatusMsgErrors(HttpStatus.BAD_REQUEST.value(), Code.VALIDATION_FAILED, BindingResultUtils.getListOfStr(bindingResult)));

        DateTimeFormatter format = (bookingDto.getTravelAt().contains("-")) ? DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT) : DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);
        ServiceResponse<LocalDate> dateResponse = DateParser.validateAndParseDate(bookingDto.getTravelAt(), format);

        if(dateResponse.getStatus() == ResponseStatus.BAD_REQUEST) {
            return ResponseEntity.badRequest().body(ApiResponse.statusMsg(HttpStatus.BAD_REQUEST.value(), Code.VALIDATION_FAILED, dateResponse.getMessage()));
        }
        LocalDate date = dateResponse.getData();

        ServiceResponse<?> response;
        try {
            response = bookingService.editUserBookingData(id, bookingDto, appUserService, date);
        }
        catch (OptimisticLockException | ObjectOptimisticLockingFailureException e) {
            return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(ApiResponse.statusMsg(HttpStatus.REQUEST_TIMEOUT.value(), Code.REQUEST_TIMEOUT, "Oops! Your booking has expired, and couldn't process booking with edited changes. Please make a new booking to continue."));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.statusMsg(HttpStatus.INTERNAL_SERVER_ERROR.value(), Code.INTERNAL_SERVER_ERROR, "Booking failed due to internal server problem. Please try again later."));
        }
        if (response.getStatus() == ResponseStatus.NOT_FOUND) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.statusMsg(HttpStatus.NOT_FOUND.value(), Code.NOT_FOUND, response.getMessage()));
        else if (response.getStatus() == ResponseStatus.BAD_REQUEST) return ResponseEntity.badRequest().body(ApiResponse.statusMsg(HttpStatus.BAD_REQUEST.value(), Code.VALIDATION_FAILED, response.getMessage()));
        else if (response.getStatus() == ResponseStatus.CONFLICT) return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.statusMsg(HttpStatus.CONFLICT.value(), Code.DUPLICATE_ENTRY, response.getMessage()));
        else if (response.getStatus() == ResponseStatus.INSUFFICIENT) return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.statusMsg(HttpStatus.CONFLICT.value(), Code.INSUFFICIENT_SEATS, response.getMessage()));
        else if(response.getStatus() == ResponseStatus.FORBIDDEN) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.statusMsg(HttpStatus.FORBIDDEN.value(), Code.ACCESS_DENIED, response.getMessage()));
        return ResponseEntity.ok(ApiResponse.successStatusMsgData(HttpStatus.OK.value(), response.getMessage(), response.getData()));
    }


    // updating status when user clicks continue booking and pass the necessary data for confirm booking summary view
    @PatchMapping("/public/bookings/{id}/continue")
    public ResponseEntity<Map<String, Object>> continueBooking(@PathVariable Long id) {
        if(id == null || id <= 0) return ResponseEntity.badRequest().body(ApiResponse.statusMsg(HttpStatus.BAD_REQUEST.value(), Code.VALIDATION_FAILED, "Invalid Booking ID. Please check and try again"));

        ServiceResponse<BookingSummaryDto> response;
        try {
            response = bookingService.continueBooking(id);
        }
        catch (ObjectOptimisticLockingFailureException | OptimisticLockException e) {
            return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(ApiResponse.statusMsg(HttpStatus.REQUEST_TIMEOUT.value(), Code.REQUEST_TIMEOUT, "Oops! Your booking has expired, and couldn't continue the process. Please make a new booking to continue."));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.statusMsg(HttpStatus.INTERNAL_SERVER_ERROR.value(), Code.INTERNAL_SERVER_ERROR, "Booking failed to continue due to internal server problem. Please try again later."));
        }
        if(response.getStatus() == ResponseStatus.NOT_FOUND) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.statusMsg(HttpStatus.NOT_FOUND.value(), Code.NOT_FOUND, response.getMessage()));
        else if(response.getStatus() == ResponseStatus.TIMEOUT) return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(ApiResponse.statusMsg(HttpStatus.REQUEST_TIMEOUT.value(), Code.REQUEST_TIMEOUT, response.getMessage()));
        else if(response.getStatus() == ResponseStatus.FORBIDDEN) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.statusMsg(HttpStatus.FORBIDDEN.value(), Code.ACCESS_DENIED, response.getMessage()));
        return ResponseEntity.ok(ApiResponse.successStatusMsgData(HttpStatus.OK.value(), response.getMessage(), response.getData()));
    }


    // method for after clicking confirm booking button, check the expire_date and the return the finalDTO
    @PatchMapping("/public/bookings/{id}/confirm")
    public ResponseEntity<Map<String, Object>> confirmBooking(@PathVariable Long id, @RequestBody ConfirmBookingDto confirmBookingDto, BindingResult bindingResult) {
        if(id == null || id <= 0) return ResponseEntity.badRequest().body(ApiResponse.statusMsg(HttpStatus.BAD_REQUEST.value(), Code.VALIDATION_FAILED, "Invalid Booking ID. Please check and try again"));
        if(bindingResult.hasErrors()) return ResponseEntity.badRequest().body(ApiResponse.errorStatusMsgErrors(HttpStatus.BAD_REQUEST.value(), Code.VALIDATION_FAILED, BindingResultUtils.getListOfStr(bindingResult)));

        ServiceResponse<PaymentMethod> paymentMethodEnum = ParsingEnumUtils.getParsedEnumType(PaymentMethod.class, confirmBookingDto.getPaymentMethod(), "Payment Method");
        if(paymentMethodEnum.getStatus() == ResponseStatus.BAD_REQUEST) return ResponseEntity.badRequest().body(ApiResponse.statusMsg(HttpStatus.BAD_REQUEST.value(), Code.VALIDATION_FAILED, paymentMethodEnum.getMessage()));
        PaymentMethod paymentMethod = paymentMethodEnum.getData();

        if(paymentMethod == PaymentMethod.NONE) return ResponseEntity.badRequest().body(ApiResponse.statusMsg(HttpStatus.BAD_REQUEST.value(), Code.INVALID, "Invalid payment method. Only Card/UPI/Bank Transfer/Net Banking or Qr code are accepted"));

        ServiceResponse<BookingFinalInfoDto> response;
        try {
            response = bookingService.confirmBooking(id, paymentMethod);
        }
        catch (ObjectOptimisticLockingFailureException | OptimisticLockException e) {
            return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(ApiResponse.statusMsg(HttpStatus.REQUEST_TIMEOUT.value(), Code.REQUEST_TIMEOUT, "Oops! Your booking has expired, and the payment couldn’t be processed. Please make a new booking to continue."));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.statusMsg(HttpStatus.INTERNAL_SERVER_ERROR.value(), Code.INTERNAL_SERVER_ERROR, "Booking failed to confirm due to internal server problem. Please try again later."));
        }
        if(response.getStatus() == ResponseStatus.NOT_FOUND) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.statusMsg(HttpStatus.NOT_FOUND.value(), Code.NOT_FOUND, response.getMessage()));
        else if(response.getStatus() == ResponseStatus.TIMEOUT) return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(ApiResponse.statusMsg(HttpStatus.REQUEST_TIMEOUT.value(), Code.REQUEST_TIMEOUT, response.getMessage()));
        else if(response.getStatus() == ResponseStatus.FORBIDDEN) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.statusMsg(HttpStatus.FORBIDDEN.value(), Code.ACCESS_DENIED, response.getMessage()));
        return ResponseEntity.ok(ApiResponse.successStatusMsgData(HttpStatus.OK.value(), response.getMessage(), response.getData()));
    }



    // what if user cancels the booking explicitly by using "CANCEL BOOKING" button
    @PatchMapping("/public/bookings/{id}/cancel")
    public ResponseEntity<Map<String, Object>> cancelBooking(@PathVariable Long id) {
        if(id == null || id <= 0) return ResponseEntity.badRequest().body(ApiResponse.statusMsg(HttpStatus.BAD_REQUEST.value(), Code.VALIDATION_FAILED, "Invalid Booking ID. Please check and try again"));

        ServiceResponse<String> response;
        try {
            response = bookingService.cancelBooking(id);
        }
        catch (ObjectOptimisticLockingFailureException | OptimisticLockException e) {
            return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(ApiResponse.statusMsg(HttpStatus.REQUEST_TIMEOUT.value(), Code.REQUEST_TIMEOUT, "This booking has expired and can no longer be cancelled."));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.statusMsg(HttpStatus.INTERNAL_SERVER_ERROR.value(), Code.INTERNAL_SERVER_ERROR, "Booking failed to cancel due to internal server problem. Please try again later."));
        }
        if(response.getStatus() == ResponseStatus.NOT_FOUND) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.statusMsg(HttpStatus.NOT_FOUND.value(), Code.NOT_FOUND, response.getMessage()));
        else if (response.getStatus() == ResponseStatus.FORBIDDEN) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.statusMsg(HttpStatus.FORBIDDEN.value(), Code.ACCESS_DENIED, response.getMessage()));
        return ResponseEntity.ok(ApiResponse.statusMsg(HttpStatus.OK.value(), Code.SUCCESS, response.getMessage()));
    }
}

/*
1. AppUser,
2. bus
*/
