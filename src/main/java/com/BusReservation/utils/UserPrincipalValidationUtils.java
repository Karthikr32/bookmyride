package com.BusReservation.utils;

import com.BusReservation.constants.Code;
import com.BusReservation.constants.Role;
import com.BusReservation.model.Management;
import com.BusReservation.model.AppUser;
import com.BusReservation.security.UserPrincipal;
import com.BusReservation.service.ManagementService;
import com.BusReservation.service.AppUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import java.util.Optional;

public class UserPrincipalValidationUtils {

    public static ResponseEntity<Map<String, Object>> validateUserPrincipal(UserPrincipal userPrincipal, Role role, ManagementService managementService) {
        if(userPrincipal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.statusMsg(HttpStatus.UNAUTHORIZED.value(), Code.UNAUTHORIZED, "Invalid token. Please try after re-login."));

        Optional<Management> managementOptional = managementService.fetchById(userPrincipal.getId());
        if(managementOptional.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.statusMsg(HttpStatus.NOT_FOUND.value(), Code.NOT_FOUND, "Account not found or no longer exists in the system."));
        Management management = managementOptional.get();

        if(management.getRole() != role) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.statusMsg(HttpStatus.FORBIDDEN.value(), Code.ACCESS_DENIED, "Access denied for this role."));
        return ResponseEntity.ok(ApiResponse.successStatusMsgData(HttpStatus.OK.value(), null, management));
    }


    public static ResponseEntity<Map<String, Object>> validateUserPrincipal(UserPrincipal userPrincipal, AppUserService appUserService) {
        if(userPrincipal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.statusMsg(HttpStatus.UNAUTHORIZED.value(), Code.UNAUTHORIZED, "Invalid token. Please try after re-login."));
        Optional<AppUser> appUserOptional = appUserService.fetchById(userPrincipal.getId());

        if(appUserOptional.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.statusMsg(HttpStatus.NOT_FOUND.value(), Code.NOT_FOUND, "Account not found or no longer exists in the system."));
        AppUser appUser = appUserOptional.get();

        if(appUser.getRole() != Role.USER) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.statusMsg(HttpStatus.FORBIDDEN.value(), Code.ACCESS_DENIED, "Access denied for this role."));
        return ResponseEntity.ok(ApiResponse.successStatusMsgData(HttpStatus.OK.value(), null, appUser));
    }
}
