package com.BusReservation.controller;

import com.BusReservation.constants.Code;
import com.BusReservation.constants.ResponseStatus;
import com.BusReservation.dto.ManagementAppUserDataDto;
import com.BusReservation.utils.*;
import com.BusReservation.service.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/management")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AppUserController {

    private final AppUserService appUserService;


    @GetMapping("/passengers")
    public ResponseEntity<Map<String, Object>> getAllUsersInfo(@RequestParam(required = false, defaultValue = "1") Integer page, @RequestParam(required = false, defaultValue = "10") Integer size,
                                                               @RequestParam(required = false, defaultValue = "id") String sortBy, @RequestParam(required = false, defaultValue = "ASC") String sortDir,
                                                               @RequestParam(required = false) String keyword) {

        List<String> listOfFields = List.of("id", "name", "mobile", "gender", "email", "role");
        var validationResult = PaginationRequest.getRequestValidationForPagination(page, size, sortBy, sortDir, listOfFields);
        if(validationResult != null) return validationResult;

        Sort.Direction sort;
        if(sortDir.equalsIgnoreCase("ASC")) sort = Sort.Direction.ASC;
        else sort = Sort.Direction.DESC;

        final PaginationRequest request = new PaginationRequest(page, size, sortBy, sort);    // without "final" somewhere value of the sortBy field mis-taken as default value "id". with final keyword it would be prevented.
        ServiceResponse<ApiPageResponse<List<ManagementAppUserDataDto>>> response = appUserService.getAllAppUserData(request, keyword);
        if(response.getStatus() == ResponseStatus.BAD_REQUEST) return ResponseEntity.badRequest().body(ApiResponse.statusMsg(HttpStatus.BAD_REQUEST.value(), Code.VALIDATION_FAILED, response.getMessage()));
        return ResponseEntity.ok(ApiResponse.successStatusMsgData(HttpStatus.OK.value(), response.getMessage(), response.getData()));
    }
}


/* NOTE
 @RequestParam(required = false)
 -> Using RequestParam for a Get request is the best way of processing, it helps to extract the value/data from URL that had passed with query?
 -> required = false, ensures that no strict needed for query data from the received request. i.e: "URL/query?key=value" is Optional whether, can receive with or without key/value query like "URL/query?" also can be accepted.
*/