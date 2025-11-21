package com.BusReservation.controller;
import com.BusReservation.constants.Code;
import com.BusReservation.constants.ResponseStatus;
import com.BusReservation.constants.Role;
import com.BusReservation.dto.ManagementBusDataDto;
import com.BusReservation.dto.BusDto;
import com.BusReservation.dto.BusUserResponseDto;
import com.BusReservation.model.Management;
import com.BusReservation.security.UserPrincipal;
import com.BusReservation.service.ManagementService;
import com.BusReservation.service.BusService;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.*;


@RestController
@RequiredArgsConstructor
public class BusController {

    private final BusService busService;
    private final ManagementService managementService;


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/management/buses")
    public ResponseEntity<Map<String, Object>> getAllBusData(@RequestParam(required = false, defaultValue = "1") Integer page, @RequestParam(required = false, defaultValue = "10") Integer size,
                                                             @RequestParam(required = false, defaultValue = "id") String sortBy, @RequestParam(required = false, defaultValue = "ASC") String sortDir,
                                                             @RequestParam(required = false) String keyword) {
        List<String> busFields = List.of("id","busName", "busNumber", "busType", "createdAt", "updatedAt", "stateOfRegistration", "capacity", "availableSeats", "fromLocation", "toLocation", "fare", "departureAt", "arrivalAt");
        var validationResult = PaginationRequest.getRequestValidationForPagination(page, size, sortBy, sortDir, busFields);
        if(validationResult != null) return validationResult;

        Sort.Direction sort;
        if(sortDir.equalsIgnoreCase("ASC")) sort = Sort.Direction.ASC;
        else sort = Sort.Direction.DESC;

        final PaginationRequest request = new PaginationRequest(page, size, sortBy, sort);
        ServiceResponse<ApiPageResponse<List<ManagementBusDataDto>>> response = busService.getAllBusInfo(request, keyword);

        if (response.getStatus() == ResponseStatus.BAD_REQUEST) return ResponseEntity.badRequest().body(ApiResponse.statusMsg(HttpStatus.BAD_REQUEST.value(), Code.VALIDATION_FAILED, response.getMessage()));
        return ResponseEntity.ok(ApiResponse.successStatusMsgData(HttpStatus.OK.value(), response.getMessage(), response.getData()));
    }


    @GetMapping("/public/buses")   //  storing as String type for travelDate. bcz to handle edge cases
    public ResponseEntity<Map<String, Object>> getBusByUserCategory(@RequestParam(required = false) String from, @RequestParam(required = false) String to,
                                                                    @RequestParam(required = false) String travelDate, @RequestParam(required = false, defaultValue = "id") String sortBy,
                                                                    @RequestParam(required = false, defaultValue = "ASC") String sortDir, @RequestParam(required = false) String acType,
                                                                    @RequestParam(required = false) String seatType, @RequestParam(required = false) String timeRange) {    // timeRange value would be in 24 hr format. Eg: 19-20 (19 hr to 20 hr, exactly as 7PM to 8PM)
        List<String> errors = RequestParamValidationUtils.listOfErrors(from, to, travelDate);

        if(!errors.isEmpty()) return ResponseEntity.badRequest().body(ApiResponse.errorStatusMsgErrors(HttpStatus.BAD_REQUEST.value(), Code.VALIDATION_FAILED, errors));
        DateTimeFormatter format = (travelDate.contains("-")) ? DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT) : DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

        ServiceResponse<LocalDate> dateResponse = DateParser.validateAndParseDate(travelDate, format);
        if(dateResponse.getStatus() == ResponseStatus.BAD_REQUEST) {
            return ResponseEntity.badRequest().body(ApiResponse.statusMsg(HttpStatus.BAD_REQUEST.value(), Code.VALIDATION_FAILED, dateResponse.getMessage()));
        }
        LocalDate date = dateResponse.getData();

        List<String> sortingField = List.of("id", "departureAt", "fare");
        if(!sortingField.contains(sortBy)) return ResponseEntity.badRequest().body(ApiResponse.statusMsg(HttpStatus.BAD_REQUEST.value(), Code.VALIDATION_FAILED, "Invalid field name, Sorting wil only done for departure and fare."));

        if(!sortDir.matches(RegExPatterns.SORT_ORDER_REGEX)) return ResponseEntity.badRequest().body(ApiResponse.statusMsg(HttpStatus.BAD_REQUEST.value(), Code.VALIDATION_FAILED, "Invalid input. Sort direction will be either ASC/DESC"));

        Sort.Direction sort;
        if(sortDir.equalsIgnoreCase("ASC")) sort = Sort.Direction.ASC;
        else sort = Sort.Direction.DESC;

        final PaginationRequest request = new PaginationRequest(1, Integer.MAX_VALUE, sortBy, sort);
        ServiceResponse<List<BusUserResponseDto>> response = busService.searchBusByUserRequest(from, to, date, request, acType, seatType, timeRange);

        if(response.getStatus() == ResponseStatus.NOT_FOUND) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.statusMsg(HttpStatus.NOT_FOUND.value(), Code.NOT_FOUND, response.getMessage()));
        else if(response.getStatus() == ResponseStatus.BAD_REQUEST) return ResponseEntity.badRequest().body(ApiResponse.statusMsg(HttpStatus.BAD_REQUEST.value(), Code.VALIDATION_FAILED, response.getMessage()));
        return ResponseEntity.ok(ApiResponse.successStatusMsgData(HttpStatus.OK.value(), response.getMessage(), response.getData()));
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/management/buses")                   // getting POST request in a JSON body, using @RequestBody convert JSON to Java Objects (Remember: coming as request)
    public ResponseEntity<Map<String, Object>> addNewBusData(@AuthenticationPrincipal UserPrincipal userPrincipal, @Valid @RequestBody BusDto busDto, BindingResult bindingResult) {
        ResponseEntity<Map<String, Object>> userDetailsValidation = UserPrincipalValidationUtils.validateUserPrincipal(userPrincipal, Role.ADMIN, managementService);
        if(userDetailsValidation.getStatusCode() != HttpStatus.OK) return userDetailsValidation;
        assert userDetailsValidation.getBody() != null;
        Management management = (Management) userDetailsValidation.getBody().get("data");

        boolean isBusNumberExists = busService.existsBusNumber(busDto.getBusNumber());
        if(isBusNumberExists) return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.statusMsg(HttpStatus.CONFLICT.value(), Code.DUPLICATE_ENTRY, "Bus number " + busDto.getBusNumber() + " already exists in the system."));

        if(bindingResult.hasErrors()) return ResponseEntity.badRequest().body(ApiResponse.errorStatusMsgErrors(HttpStatus.BAD_REQUEST.value(), Code.VALIDATION_FAILED, BindingResultUtils.getListOfStr(bindingResult)));

        ServiceResponse<String> response;
        try {
            response = busService.addNewBusInfoData(management, busDto);
        }
        catch (ObjectOptimisticLockingFailureException | OptimisticLockException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.statusMsg(HttpStatus.CONFLICT.value(), Code.MODIFIED_BY_OTHER, "Bus with number '" + busDto.getBusNumber() + "' was recently added by another management authority. Please refresh and try again."));
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.statusMsg(HttpStatus.INTERNAL_SERVER_ERROR.value(), Code.INTERNAL_SERVER_ERROR, "Inserting a new bus data is failed due to internal server problem. Please try again later."));
        }
        if(response.getStatus() == ResponseStatus.BAD_REQUEST) return ResponseEntity.badRequest().body(ApiResponse.statusMsg(HttpStatus.BAD_REQUEST.value(), Code.VALIDATION_FAILED, response.getMessage()));
        else if(response.getStatus() == ResponseStatus.NOT_FOUND) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.statusMsg(HttpStatus.NOT_FOUND.value(), Code.NOT_FOUND, response.getMessage()));
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.statusMsg(HttpStatus.CREATED.value(), Code.CREATED, response.getMessage()));
    }


/* While working on Put Request, frontend sends "id" via URL and JSON as body
   backend needs to find the entry by using the given id, and update the old data to a new one that we'd received using "RequestBody of existing entry
*/
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/management/buses/{id}")
    public ResponseEntity<Map<String, Object>> updateExistingBusInfo(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long id, @Valid @RequestBody BusDto busDto, BindingResult bindingResult) {
        ResponseEntity<Map<String, Object>> userDetailsValidation = UserPrincipalValidationUtils.validateUserPrincipal(userPrincipal, Role.ADMIN, managementService);
        if(userDetailsValidation.getStatusCode() != HttpStatus.OK) return userDetailsValidation;
        assert userDetailsValidation.getBody() != null;
        Management management = (Management) userDetailsValidation.getBody().get("data");

        if(id == null || id <= 0) return ResponseEntity.badRequest().body(ApiResponse.statusMsg(HttpStatus.BAD_REQUEST.value(), Code.VALIDATION_FAILED, "Invalid ID. Please verify the value and try again."));
        if(bindingResult.hasErrors()) return ResponseEntity.badRequest().body(ApiResponse.errorStatusMsgErrors(HttpStatus.BAD_REQUEST.value(), Code.VALIDATION_FAILED, BindingResultUtils.getListOfStr(bindingResult)));

        ServiceResponse<String> response;
        try {
            response = busService.updateExistingBusInfo(id, busDto, management);
        }
        catch (ObjectOptimisticLockingFailureException | OptimisticLockException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.statusMsg(HttpStatus.CONFLICT.value(), Code.MODIFIED_BY_OTHER, "Bus with number '" + busDto.getBusNumber() + "' was already modified by another management authority. Please refresh and try again."));
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.statusMsg(HttpStatus.INTERNAL_SERVER_ERROR.value(), Code.INTERNAL_SERVER_ERROR, "Updating a bus data is failed due to internal server problem. Please try again later."));
        }
        if(response.getStatus() == ResponseStatus.NOT_FOUND) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.statusMsg(HttpStatus.NOT_FOUND.value(), Code.NOT_FOUND, response.getMessage()));
        else if(response.getStatus() == ResponseStatus.BAD_REQUEST) return ResponseEntity.badRequest().body(ApiResponse.statusMsg(HttpStatus.BAD_REQUEST.value(), Code.VALIDATION_FAILED, response.getMessage()));
        return ResponseEntity.ok(ApiResponse.statusMsg(HttpStatus.OK.value(), Code.SUCCESS, response.getMessage()));
    }



    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/management/buses/{id}")
    public ResponseEntity<Map<String, Object>> deleteBus(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long id) {       // no need any validity check for path variable, That'll handle by Spring itself
        ResponseEntity<Map<String, Object>> userDetailsValidation = UserPrincipalValidationUtils.validateUserPrincipal(userPrincipal, Role.ADMIN, managementService);
        if(userDetailsValidation.getStatusCode() != HttpStatus.OK) return userDetailsValidation;
        assert userDetailsValidation.getBody() != null;
        Management management = (Management) userDetailsValidation.getBody().get("data");

        if(id == null || id <= 0) return ResponseEntity.badRequest().body(ApiResponse.statusMsg(HttpStatus.BAD_REQUEST.value(), Code.VALIDATION_FAILED, "Invalid ID. Please verify the value and try again."));

        ServiceResponse<String> response;
        try {
            response= busService.deleteByBusId(id, management);
        }
        catch (ObjectOptimisticLockingFailureException | OptimisticLockException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.statusMsg(HttpStatus.CONFLICT.value(), Code.MODIFIED_BY_OTHER, "Bus ID '" + id + "' was been deleted by another management authority. Please reload before retrying."));
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.statusMsg(HttpStatus.INTERNAL_SERVER_ERROR.value(), Code.INTERNAL_SERVER_ERROR, "Updating a bus data is failed due to internal server problem. Please try again later."));
        }
        if (response.getStatus() == ResponseStatus.NOT_FOUND) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.statusMsg(HttpStatus.NOT_FOUND.value(), Code.NOT_FOUND, response.getMessage()));
        return ResponseEntity.ok(ApiResponse.statusMsg(HttpStatus.OK.value(), Code.SUCCESS, response.getMessage()));
    }

}


/* NOTE:
 1.) response.put("status", HttpStatus.NOT_FOUND);
  This will send JSON as "status" : "NOT_FOUND" (which is not good practice for API's)
  INSTEAD
  -> response.put("status", HttpStatus.NOT_FOUND.value());
  This will send JSON as "status" : 404   (Good practice)


  2.)  Creating more/diff map for every response(success/failure) Instead create 1 and use for all.

  3.) When to use .badRequest()?
  -> When any DTO has error like field mismatch, field empty or email invalid, etc.
*/

// Using try/catch tried to delete, but not worked as expected, why?
//  "EmptyResultDataAccessException" will occur when trying to perform something on the non-existed element in DB