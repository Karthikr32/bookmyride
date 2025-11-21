package com.BusReservation.controller;

import com.BusReservation.constants.Code;
import com.BusReservation.constants.ResponseStatus;
import com.BusReservation.dto.BookingListDto;
import com.BusReservation.dto.ChangeUserPasswordDto;
import com.BusReservation.dto.UpdateUserProfileDto;
import com.BusReservation.dto.UserProfileDto;
import com.BusReservation.model.AppUser;
import com.BusReservation.security.UserPrincipal;
import com.BusReservation.service.AppUserService;
import com.BusReservation.utils.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@PreAuthorize("hasRole('USER')")
public class UserController {

    private final AppUserService appUserService;
    private final AuthenticationManager authenticationManager;


    @GetMapping("/profile")    // User's personal info
    public ResponseEntity<Map<String, Object>> viewProfile(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        ResponseEntity<Map<String, Object>> userPrincipalValidation = UserPrincipalValidationUtils.validateUserPrincipal(userPrincipal, appUserService);
        if(userPrincipalValidation.getStatusCode() != HttpStatus.OK) return userPrincipalValidation;
        assert userPrincipalValidation.getBody() != null;
        AppUser user = (AppUser) userPrincipalValidation.getBody().get("data");

        ServiceResponse<UserProfileDto> response = appUserService.fetchUserProfile(user);
        if (response.getStatus() == ResponseStatus.FORBIDDEN) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.profileStatus(HttpStatus.FORBIDDEN.value(), Code.ACCESS_DENIED, response.getMessage(), false));
        return ResponseEntity.ok(ApiResponse.successStatusMsgData(HttpStatus.OK.value(), response.getMessage(), response.getData()));
    }

    @PutMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateProfile(@AuthenticationPrincipal UserPrincipal userPrincipal, @Valid @RequestBody UpdateUserProfileDto userProfileDto, BindingResult bindingResult) {
        ResponseEntity<Map<String, Object>> userPrincipalValidation = UserPrincipalValidationUtils.validateUserPrincipal(userPrincipal, appUserService);
        if(userPrincipalValidation.getStatusCode() != HttpStatus.OK) return userPrincipalValidation;
        assert userPrincipalValidation.getBody() != null;
        AppUser user = (AppUser) userPrincipalValidation.getBody().get("data");

        if(bindingResult.hasErrors()) return ResponseEntity.badRequest().body(ApiResponse.errorStatusMsgErrors(HttpStatus.BAD_REQUEST.value(), Code.VALIDATION_FAILED, BindingResultUtils.getListOfStr(bindingResult)));

        ServiceResponse<UserProfileDto> response = appUserService.updateProfile(user, userProfileDto);
        if(response.getStatus() == ResponseStatus.CONFLICT) return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.statusMsg(HttpStatus.CONFLICT.value(), Code.DUPLICATE_ENTRY, response.getMessage()));
        else if(response.getStatus() == ResponseStatus.BAD_REQUEST) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.statusMsg(HttpStatus.BAD_REQUEST.value(), Code.VALIDATION_FAILED, response.getMessage()));
        return ResponseEntity.ok(ApiResponse.successStatusMsgData(HttpStatus.OK.value(), response.getMessage(), response.getData()));
    }

    @PatchMapping("/change-password")
    public ResponseEntity<Map<String, Object>> changePassword(@AuthenticationPrincipal UserPrincipal userPrincipal, @Valid @RequestBody ChangeUserPasswordDto changeUserPasswordDto, BindingResult bindingResult) {
        ResponseEntity<Map<String, Object>> userPrincipalValidation = UserPrincipalValidationUtils.validateUserPrincipal(userPrincipal, appUserService);
        if(userPrincipalValidation.getStatusCode() != HttpStatus.OK) return userPrincipalValidation;
        assert userPrincipalValidation.getBody() != null;
        AppUser user = (AppUser) userPrincipalValidation.getBody().get("data");

        if(bindingResult.hasErrors()) return ResponseEntity.badRequest().body(ApiResponse.errorStatusMsgErrors(HttpStatus.BAD_REQUEST.value(), Code.VALIDATION_FAILED, BindingResultUtils.getListOfStr(bindingResult)));

        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getMobile(), changeUserPasswordDto.getOldPassword());
        try {
            authenticationManager.authenticate(authentication);
        }
        catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.statusMsg(HttpStatus.UNAUTHORIZED.value(), Code.UNAUTHORIZED, "Invalid credentials. Given old password is incorrect."));
        }
        catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.statusMsg(HttpStatus.NOT_FOUND.value(), Code.NOT_FOUND, "No user account found with the given credentials."));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.statusMsg(HttpStatus.INTERNAL_SERVER_ERROR.value(), Code.INTERNAL_SERVER_ERROR,"An unexpected error occurred during password change."));
        }

        ServiceResponse<String> response = appUserService.changeUserNewPassword(user, changeUserPasswordDto);
        return ResponseEntity.ok(ApiResponse.statusMsg(HttpStatus.OK.value(), Code.SUCCESS, response.getMessage()));
    }


    @GetMapping("/bookings")
    public ResponseEntity<Map<String, Object>> BookingsList(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                @RequestParam(required = false, defaultValue = "1") Integer page,
                                                                @RequestParam(required = false, defaultValue = "10") Integer size,
                                                                @RequestParam(required = false, defaultValue = "id") String sortBy,
                                                                @RequestParam(required = false, defaultValue = "ASC") String sortDir) {

        List<String> bookingFields = List.of("id", "fromLocation", "toLocation", "seatsBooked", "bookedAt", "travelAt", "departureAt", "arrivalAt", "bookingStatus", "paymentStatus", "paymentMethod", "bookingExpiresAt", "discountPct", "totalCost", "discountAmount", "finalCost");
        var validationResult = PaginationRequest.getRequestValidationForPagination(page, size, sortBy, sortDir, bookingFields);
        if(validationResult != null) return validationResult;

        Sort.Direction sort;
        if(sortDir.equalsIgnoreCase("ASC")) sort = Sort.Direction.ASC;
        else sort = Sort.Direction.DESC;

        final PaginationRequest request = new PaginationRequest(page, size, sortBy, sort);
        ServiceResponse<ApiPageResponse<List<BookingListDto>>> response = appUserService.getBookingList(userPrincipal.getId(), request);

        return ResponseEntity.ok(ApiResponse.successStatusMsgData(HttpStatus.OK.value(), response.getMessage(), response.getData()));
    }
}
