package com.BusReservation.controller;

import com.BusReservation.constants.Code;
import com.BusReservation.constants.ResponseStatus;
import com.BusReservation.constants.Role;
import com.BusReservation.dto.LocationEntryDto;
import com.BusReservation.dto.LocationResponseDto;
import com.BusReservation.model.Management;
import com.BusReservation.security.UserPrincipal;
import com.BusReservation.service.ManagementService;
import com.BusReservation.service.LocationService;
import com.BusReservation.utils.*;
import jakarta.persistence.OptimisticLockException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/bookmyride/management/")
@PreAuthorize("hasRole('ADMIN')")
public class LocationController {

    private final LocationService locationService;
    private final ManagementService managementService;


    @GetMapping("/locations")
    public ResponseEntity<Map<String, Object>> getAllLocationData(@RequestParam(required = false, defaultValue = "1") Integer page, @RequestParam(required = false, defaultValue = "10") Integer size,
                                                                  @RequestParam(required = false, defaultValue = "id") String sortBy, @RequestParam(required = false, defaultValue = "ASC") String sortDir,
                                                                  @RequestParam(required = false) String country, @RequestParam(required = false) String state,
                                                                  @RequestParam(required = false) String city, @RequestParam(required = false) String role) {
        List<String> listOfFields = List.of("id", "name", "state.name", "state.country.name", "createdAt", "updatedAt");
        ResponseEntity<Map<String, Object>> requestValidation = PaginationRequest.getRequestValidationForPagination(page, size, sortBy, sortDir, listOfFields);
        if(requestValidation != null) return requestValidation;

        Sort.Direction sort;
        if(sortDir.equalsIgnoreCase("ASC")) sort = Sort.Direction.ASC;
        else sort = Sort.Direction.DESC;

        final PaginationRequest paginationRequest = new PaginationRequest(page, size, sortBy, sort);
        ServiceResponse<ApiPageResponse<List<LocationResponseDto>>> response = locationService.getLocationData(paginationRequest, country, state, city, role);
        if(response.getStatus() == ResponseStatus.BAD_REQUEST) return ResponseEntity.badRequest().body(ApiResponse.statusMsg(HttpStatus.BAD_REQUEST.value(), Code.VALIDATION_FAILED, response.getMessage()));
        return ResponseEntity.ok(ApiResponse.successStatusMsgData(HttpStatus.OK.value(), response.getMessage(), response.getData()));
    }


    @PostMapping("/locations")
    public ResponseEntity<Map<String, Object>> addNewLocation(@AuthenticationPrincipal UserPrincipal userPrincipal, @Valid @RequestBody LocationEntryDto locationEntryDto, BindingResult bindingResult) {
        ResponseEntity<Map<String, Object>> validationResponse =  UserPrincipalValidationUtils.validateUserPrincipal(userPrincipal, Role.ADMIN, managementService);
        if(validationResponse.getStatusCode() != HttpStatus.OK) return validationResponse;
        assert validationResponse.getBody() != null;
        Management management = (Management) validationResponse.getBody().get("data");

        if(bindingResult.hasErrors()) return ResponseEntity.badRequest().body(ApiResponse.errorStatusMsgErrors(HttpStatus.BAD_REQUEST.value(), Code.VALIDATION_FAILED, BindingResultUtils.getListOfStr(bindingResult)));
        ServiceResponse<String> response;
        try {
            response = locationService.postNewLocationData(locationEntryDto, management);
        }
        catch (ObjectOptimisticLockingFailureException | OptimisticLockException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.statusMsg(HttpStatus.CONFLICT.value(), Code.MODIFIED_BY_OTHER, "The combination of City: '" + locationEntryDto.getCity() + "', State: '" + locationEntryDto.getState() + "', Country: '" + locationEntryDto.getCountry() + "' was already added by another management authority. Please refresh and try again."));
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.statusMsg(HttpStatus.INTERNAL_SERVER_ERROR.value(), Code.INTERNAL_SERVER_ERROR, "Inserting a new location data is failed due to internal server problem. Please try again later."));
        }
        if(response.getStatus() == ResponseStatus.CONFLICT) return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.statusMsg(HttpStatus.CONFLICT.value(), Code.DUPLICATE_ENTRY, response.getMessage()));
        else if(response.getStatus() == ResponseStatus.BAD_REQUEST) return ResponseEntity.badRequest().body(ApiResponse.statusMsg(HttpStatus.BAD_REQUEST.value(), Code.VALIDATION_FAILED, response.getMessage()));
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.statusMsg(HttpStatus.CREATED.value(), Code.CREATED, response.getMessage()));
    }


    @PostMapping("/locations/list")
    public ResponseEntity<Map<String, Object>> postListOfLocations(@AuthenticationPrincipal UserPrincipal userPrincipal, @Valid @RequestBody List<LocationEntryDto> locationEntryDtoList, BindingResult bindingResult) {
        ResponseEntity<Map<String, Object>> validationResponse =  UserPrincipalValidationUtils.validateUserPrincipal(userPrincipal, Role.ADMIN, managementService);
        if(validationResponse.getStatusCode() != HttpStatus.OK) return validationResponse;
        assert validationResponse.getBody() != null;
        Management management = (Management) validationResponse.getBody().get("data");

        if(locationEntryDtoList.isEmpty()) return ResponseEntity.badRequest().body(ApiResponse.statusMsg(HttpStatus.BAD_REQUEST.value(), Code.INVALID, "Invalid request. Requested list of location could be empty."));

        if(bindingResult.hasErrors()) return ResponseEntity.badRequest().body(ApiResponse.errorStatusMsgErrors(HttpStatus.BAD_REQUEST.value(), Code.VALIDATION_FAILED, BindingResultUtils.getListOfStr(bindingResult)));
        ServiceResponse<List<String>> response;
        try {
            response = locationService.postListOfNewLocationData(locationEntryDtoList, management);
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.statusMsg(HttpStatus.INTERNAL_SERVER_ERROR.value(), Code.INTERNAL_SERVER_ERROR, "Bulk Inserting of location data is failed due to internal server problem. Please try again later."));
        }
        if(response.getStatus() == ResponseStatus.BAD_REQUEST) return ResponseEntity.badRequest().body(ApiResponse.statusMsg(HttpStatus.BAD_REQUEST.value(), Code.VALIDATION_FAILED, response.getMessage()));
        else if(response.getStatus() == ResponseStatus.CONFLICT) return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body(ApiResponse.partialSuccessStatus(HttpStatus.PARTIAL_CONTENT.value(), Code.PARTIAL_UPDATE, response.getMessage(), response.getData()));
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.statusMsg(HttpStatus.CREATED.value(), Code.CREATED, response.getMessage()));
    }


    @PutMapping("/locations/{id}")
    public ResponseEntity<Map<String, Object>> updateLocation(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long id, @Valid @RequestBody LocationEntryDto locationEntryDto, BindingResult bindingResult) {
        ResponseEntity<Map<String, Object>> validationResponse =  UserPrincipalValidationUtils.validateUserPrincipal(userPrincipal, Role.ADMIN, managementService);
        if(validationResponse.getStatusCode() != HttpStatus.OK) return validationResponse;
        assert validationResponse.getBody() != null;
        Management management = (Management) validationResponse.getBody().get("data");

        if(id == null || id <= 0) return ResponseEntity.badRequest().body(ApiResponse.statusMsg(HttpStatus.BAD_REQUEST.value(), Code.VALIDATION_FAILED, "Invalid ID. Please verify the value and try again."));

        if(bindingResult.hasErrors()) return ResponseEntity.badRequest().body(ApiResponse.errorStatusMsgErrors(HttpStatus.BAD_REQUEST.value(), Code.VALIDATION_FAILED, BindingResultUtils.getListOfStr(bindingResult)));
        ServiceResponse<String> response;
        try {
            response = locationService.updateLocationData(id, locationEntryDto, management);
        }
        catch (ObjectOptimisticLockingFailureException | OptimisticLockException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.statusMsg(HttpStatus.CONFLICT.value(), Code.MODIFIED_BY_OTHER, "The combination of City: '" + locationEntryDto.getCity() + "', State: '" + locationEntryDto.getState() + "', Country: '" + locationEntryDto.getCountry() + "' was modified by another management user. Please refresh and try again."));
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.statusMsg(HttpStatus.INTERNAL_SERVER_ERROR.value(), Code.INTERNAL_SERVER_ERROR, "Updating the location failed due to an internal server error. Please try again later."));
        }
        if(response.getStatus() == ResponseStatus.NOT_FOUND) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.statusMsg(HttpStatus.NOT_FOUND.value(), Code.NOT_FOUND, response.getMessage()));
        else if(response.getStatus() == ResponseStatus.BAD_REQUEST) return ResponseEntity.badRequest().body(ApiResponse.statusMsg(HttpStatus.BAD_REQUEST.value(), Code.VALIDATION_FAILED, response.getMessage()));
        else if(response.getStatus() == ResponseStatus.CONFLICT) return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.statusMsg(HttpStatus.CONFLICT.value(), Code.DUPLICATE_ENTRY, response.getMessage()));
        return ResponseEntity.ok(ApiResponse.statusMsg(HttpStatus.OK.value(), Code.SUCCESS, response.getMessage()));
    }


    @DeleteMapping("/locations/{id}")
    public ResponseEntity<Map<String, Object>> deleteLocationMap(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long id) {
        ResponseEntity<Map<String, Object>> validationResponse =  UserPrincipalValidationUtils.validateUserPrincipal(userPrincipal, Role.ADMIN, managementService);
        if(validationResponse.getStatusCode() != HttpStatus.OK) return validationResponse;
        assert validationResponse.getBody() != null;
        Management management = (Management) validationResponse.getBody().get("data");

        if(id == null || id <= 0) return ResponseEntity.badRequest().body(ApiResponse.statusMsg(HttpStatus.BAD_REQUEST.value(), Code.VALIDATION_FAILED, "Invalid ID. Please verify the value and try again."));

        ServiceResponse<String> response;
        try {
            response = locationService.deleteLocationDataById(id, management);
        }
        catch (OptimisticLockException | ObjectOptimisticLockingFailureException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.statusMsg(HttpStatus.CONFLICT.value(), Code.MODIFIED_BY_OTHER, "Location Id: " + id + "was already deleted by other authority."));
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.statusMsg(HttpStatus.INTERNAL_SERVER_ERROR.value(), Code.INTERNAL_SERVER_ERROR, "Deletion of location data is failed due to internal server problem. Please try again later."));
        }
        if(response.getStatus() == ResponseStatus.NOT_FOUND) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.statusMsg(HttpStatus.NOT_FOUND.value(), Code.NOT_FOUND, response.getMessage()));
        return ResponseEntity.ok(ApiResponse.statusMsg(HttpStatus.OK.value(), Code.DELETED, response.getMessage()));
    }


    @DeleteMapping("/locations/all")
    public ResponseEntity<Map<String, Object>> deleteAllLocationData(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        ResponseEntity<Map<String, Object>> validationResponse =  UserPrincipalValidationUtils.validateUserPrincipal(userPrincipal, Role.ADMIN, managementService);
        if(validationResponse.getStatusCode() != HttpStatus.OK) return validationResponse;
        assert validationResponse.getBody() != null;
        Management management = (Management) validationResponse.getBody().get("data");

        ServiceResponse<String> response;
        try {
            response = locationService.clearAllLocationData(management);
        }
        catch (OptimisticLockException | ObjectOptimisticLockingFailureException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.statusMsg(HttpStatus.CONFLICT.value(), Code.MODIFIED_BY_OTHER, "All location data was already deleted by other authority."));
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.statusMsg(HttpStatus.INTERNAL_SERVER_ERROR.value(), Code.INTERNAL_SERVER_ERROR, "Deletion of all location data is failed due to internal server problem. Please try again later."));
        }
        return ResponseEntity.ok(ApiResponse.statusMsg(HttpStatus.OK.value(), Code.DELETED, response.getMessage()));
    }
}
