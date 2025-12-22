package com.BusReservation.controller;
import com.BusReservation.constants.Code;
import com.BusReservation.constants.ResponseStatus;
import com.BusReservation.constants.Role;
import com.BusReservation.dto.*;
import com.BusReservation.model.Management;
import com.BusReservation.security.UserPrincipal;
import com.BusReservation.service.ManagementService;
import com.BusReservation.service.AppUserService;
import com.BusReservation.service.BusService;
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
import java.time.LocalDateTime;
import java.util.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/management")
@PreAuthorize("hasRole('ADMIN')")
public class ManagementController {

    private final ManagementService managementService;
    private final BusService busService;
    private final AppUserService appUserService;
    private final AuthenticationManager authenticationManager;


    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getProfile(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        ResponseEntity<Map<String, Object>> userDetailsValidation = UserPrincipalValidationUtils.validateUserPrincipal(userPrincipal, Role.ADMIN, managementService);
        if(userDetailsValidation.getStatusCode() != HttpStatus.OK) return userDetailsValidation;
        assert userDetailsValidation.getBody() != null;
        Management management = (Management) userDetailsValidation.getBody().get("data");

        ServiceResponse<ManagementProfileDto> response = managementService.fetchProfileData(management);
        return ResponseEntity.ok(ApiResponse.successStatusMsgData(HttpStatus.OK.value(), response.getMessage(), response.getData()));
    }


    @PutMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateProfile(@AuthenticationPrincipal UserPrincipal userPrincipal, @Valid @RequestBody UpdateManagementProfileDto updateProfileDto, BindingResult bindingResult) {
        ResponseEntity<Map<String, Object>> userDetailsValidation = UserPrincipalValidationUtils.validateUserPrincipal(userPrincipal, Role.ADMIN, managementService);
        if(userDetailsValidation.getStatusCode() != HttpStatus.OK) return userDetailsValidation;
        assert userDetailsValidation.getBody() != null;
        Management management = (Management) userDetailsValidation.getBody().get("data");

        if(bindingResult.hasErrors()) return ResponseEntity.badRequest().body(ApiResponse.errorStatusMsgErrors(HttpStatus.BAD_REQUEST.value(), Code.VALIDATION_FAILED, BindingResultUtils.getListOfStr(bindingResult)));

        boolean isExists = appUserService.fetchByEmailOrMobile(updateProfileDto.getEmail(), updateProfileDto.getMobile());
        if(isExists) return ResponseEntity.badRequest().body(ApiResponse.statusMsg(HttpStatus.FORBIDDEN.value(), Code.ACCESS_DENIED, "The provided email or mobile number is already registered to a non-management user. Management user accounts must use unique credentials not associated with regular users."));

        ServiceResponse<ManagementProfileDto> response = managementService.updateProfile(management, updateProfileDto);
        if(response.getStatus() == ResponseStatus.NOT_FOUND) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.statusMsg(HttpStatus.NOT_FOUND.value(), Code.NOT_FOUND, response.getMessage()));
        else if(response.getStatus() == ResponseStatus.CONFLICT) return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.statusMsg(HttpStatus.CONFLICT.value(), Code.DUPLICATE_ENTRY, response.getMessage()));
        else if(response.getStatus() == ResponseStatus.BAD_REQUEST) return ResponseEntity.badRequest().body(ApiResponse.statusMsg(HttpStatus.BAD_REQUEST.value(), Code.VALIDATION_FAILED, response.getMessage()));
        return ResponseEntity.ok(ApiResponse.successStatusMsgData(HttpStatus.OK.value(), response.getMessage(), response.getData()));
    }


    @PatchMapping("/change-password")
    public ResponseEntity<Map<String, Object>> changePassword(@AuthenticationPrincipal UserPrincipal userPrincipal, @Valid @RequestBody ChangeManagementPasswordDto changePasswordDto, BindingResult bindingResult) {
        ResponseEntity<Map<String, Object>> userDetailsValidation = UserPrincipalValidationUtils.validateUserPrincipal(userPrincipal, Role.ADMIN, managementService);
        if(userDetailsValidation.getStatusCode() != HttpStatus.OK) return userDetailsValidation;
        assert userDetailsValidation.getBody() != null;
        Management management = (Management) userDetailsValidation.getBody().get("data");

        if(bindingResult.hasErrors()) return ResponseEntity.badRequest().body(ApiResponse.errorStatusMsgErrors(HttpStatus.BAD_REQUEST.value(), Code.VALIDATION_FAILED, BindingResultUtils.getListOfStr(bindingResult)));

        Authentication authentication = new UsernamePasswordAuthenticationToken(management.getUsername(), changePasswordDto.getOldPassword());
        try {
            authenticationManager.authenticate(authentication);
        }
        catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.statusMsg(HttpStatus.UNAUTHORIZED.value(), Code.UNAUTHORIZED, "Invalid credentials. Given old password is incorrect."));
        }
        catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.statusMsg(HttpStatus.NOT_FOUND.value(), Code.NOT_FOUND, "No management user account found with the given credentials."));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.statusMsg(HttpStatus.INTERNAL_SERVER_ERROR.value(), Code.INTERNAL_SERVER_ERROR,"An unexpected error occurred during password change."));
        }

        ServiceResponse<String> response = managementService.changeNewPassword(management, changePasswordDto);
        return ResponseEntity.ok(ApiResponse.statusMsg(HttpStatus.OK.value(), Code.SUCCESS, response.getMessage()));
    }


    @GetMapping("/stats/buses")
    public ResponseEntity<Map<String, Object>> getBookedBusesStats(@RequestParam(required = false, defaultValue = "1") Integer page, @RequestParam(required = false, defaultValue = "10") Integer size,
                                                                   @RequestParam(required = false, defaultValue = "totalBookings") String sortBy, @RequestParam(required = false, defaultValue = "DESC") String sortDir,
                                                                   @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate,
                                                                   @RequestParam(required = false) String category) {

        List<String> statsFields = List.of("totalBookings", "totalRevenue", "occupancy", "availability");
        var validationResult = PaginationRequest.getRequestValidationForPagination(page, size, sortBy, sortDir, statsFields);
        if(validationResult != null) return validationResult;

        Sort.Direction sort;
        if(sortDir.equalsIgnoreCase("ASC")) sort = Sort.Direction.ASC;
        else sort = Sort.Direction.DESC;

        List<String> errors = RequestParamValidationUtils.listOfErrors(startDate, endDate);
        if(!errors.isEmpty()) return ResponseEntity.badRequest().body(ApiResponse.errorStatusMsgErrors(HttpStatus.BAD_REQUEST.value(), Code.VALIDATION_FAILED, errors));

        ServiceResponse<Map<String, LocalDateTime>> parsedDateResponse = DateParserUtils.getBothDateTime(startDate, endDate);
        if(parsedDateResponse.getStatus() == ResponseStatus.BAD_REQUEST) return ResponseEntity.badRequest().body(ApiResponse.statusMsg(HttpStatus.BAD_REQUEST.value(), Code.VALIDATION_FAILED, parsedDateResponse.getMessage()));
        Map<String, LocalDateTime> dateTime = parsedDateResponse.getData();

        final PaginationRequest request = new PaginationRequest(page, size, sortBy, sort);
        ServiceResponse<ApiPageResponse<List<BookedBusReportDto>>> responseStats = busService.getBookedBusReport(dateTime.get("startDateTime"), dateTime.get("endDateTime"), request, category);

        if(responseStats.getStatus() == ResponseStatus.BAD_REQUEST) return ResponseEntity.badRequest().body(ApiResponse.statusMsg(HttpStatus.BAD_REQUEST.value(), Code.VALIDATION_FAILED, responseStats.getMessage()));
        return ResponseEntity.ok(ApiResponse.successStatusMsgData(HttpStatus.OK.value(), responseStats.getMessage(), responseStats.getData()));
    }


    @GetMapping("/stats/passengers")
    public ResponseEntity<Map<String, Object>> getAppUsersStats(@RequestParam(required = false, defaultValue = "1") Integer page, @RequestParam(required = false, defaultValue = "10") Integer size,
                                                                @RequestParam(required = false, defaultValue = "totalBookings") String sortBy, @RequestParam(required = false, defaultValue = "DESC") String sortDir,
                                                                @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate,
                                                                @RequestParam(required = false) String gender, @RequestParam(required = false) String role) {

        List<String> appUserFields = List.of("totalBookings", "totalRevenue", "recentBookedAt");
        var validationResult = PaginationRequest.getRequestValidationForPagination(page, size, sortBy, sortDir, appUserFields);
        if(validationResult != null) return validationResult;

        Sort.Direction sort;
        if(sortDir.equalsIgnoreCase("ASC")) sort = Sort.Direction.ASC;
        else sort = Sort.Direction.DESC;

        List<String> errors = RequestParamValidationUtils.listOfErrors(startDate, endDate);
        if(!errors.isEmpty()) return ResponseEntity.badRequest().body(ApiResponse.errorStatusMsgErrors(HttpStatus.BAD_REQUEST.value(), Code.VALIDATION_FAILED, errors));

        ServiceResponse<Map<String, LocalDateTime>> parsedDateResponse = DateParserUtils.getBothDateTime(startDate, endDate);
        if(parsedDateResponse.getStatus() == ResponseStatus.BAD_REQUEST) return ResponseEntity.badRequest().body(ApiResponse.statusMsg(HttpStatus.BAD_REQUEST.value(), Code.VALIDATION_FAILED, parsedDateResponse.getMessage()));
        Map<String, LocalDateTime> dateTime = parsedDateResponse.getData();

        PaginationRequest request = new PaginationRequest(page, size, sortBy, sort);
        ServiceResponse<ApiPageResponse<List<BookedAppUserReportDto>>> response = appUserService.getBookedAppUserReport(dateTime.get("startDateTime"), dateTime.get("endDateTime"), request, gender, role);

        if(response.getStatus() == ResponseStatus.BAD_REQUEST) return ResponseEntity.badRequest().body(ApiResponse.statusMsg(HttpStatus.BAD_REQUEST.value(), Code.VALIDATION_FAILED,response.getMessage()));
        return ResponseEntity.ok(ApiResponse.successStatusMsgData(HttpStatus.OK.value(), response.getMessage(), response.getData()));
    }
}
