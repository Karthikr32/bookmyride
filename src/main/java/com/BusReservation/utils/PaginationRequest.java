package com.BusReservation.utils;
import com.BusReservation.constants.Code;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class PaginationRequest {

    private Integer page;
    private Integer size;
    private String sortBy;
    private Sort.Direction sortDir;


    public PaginationRequest(Integer page, Integer size, String sortBy, Sort.Direction sortDir) {
        this.page = page - 1;
        this.size = size;
        this.sortBy = sortBy;
        this.sortDir = sortDir;
    }

    public static Pageable getPageable(PaginationRequest request) {
        return PageRequest.of(request.getPage(), request.getSize(), request.getSortDir(), request.getSortBy());
    }


    public static ResponseEntity<Map<String, Object>> getRequestValidationForPagination(Integer page, Integer size, String sortBy, String sortDir, List<String> listOfFields) {
        if(page <= 0) return ResponseEntity.badRequest().body(ApiResponse.statusMsg(HttpStatus.BAD_REQUEST.value(), Code.VALIDATION_FAILED, "Invalid page number. Page number must start from 1"));
        else if(size <= 0) return ResponseEntity.badRequest().body(ApiResponse.statusMsg(HttpStatus.BAD_REQUEST.value(), Code.VALIDATION_FAILED, "Invalid size. Size must be minimum 1"));
        else if(!listOfFields.contains(sortBy)) return ResponseEntity.badRequest().body(ApiResponse.statusMsg(HttpStatus.BAD_REQUEST.value(), Code.VALIDATION_FAILED, "Invalid field name, Please check and try again"));
        else if(!sortDir.matches(RegExPatterns.SORT_ORDER_REGEX)) return ResponseEntity.badRequest().body(ApiResponse.statusMsg(HttpStatus.BAD_REQUEST.value(), Code.VALIDATION_FAILED, "Invalid input. Sort direction will be either ASC/DESC"));
        return null;
    }
}
