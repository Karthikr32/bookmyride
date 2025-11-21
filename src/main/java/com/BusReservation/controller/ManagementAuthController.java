package com.BusReservation.controller;

import com.BusReservation.constants.Code;
import com.BusReservation.constants.ResponseStatus;
import com.BusReservation.constants.Role;
import com.BusReservation.dto.ManagementLoginDto;
import com.BusReservation.dto.ManagementSignUpDto;
import com.BusReservation.model.Management;
import com.BusReservation.security.JwtService;
import com.BusReservation.security.UserPrincipal;
import com.BusReservation.service.ManagementService;
import com.BusReservation.service.AppUserService;
import com.BusReservation.utils.ApiResponse;
import com.BusReservation.utils.BindingResultUtils;
import com.BusReservation.utils.ServiceResponse;
import com.BusReservation.utils.UserPrincipalValidationUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/bookmyride/management")
public class ManagementAuthController {

    private final ManagementService managementService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final AppUserService appUserService;


    @PostMapping("/signup")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> register(@AuthenticationPrincipal UserPrincipal userPrincipal, @Valid @RequestBody ManagementSignUpDto signUpDto, BindingResult bindingResult) {
        var userPrincipalValidation = UserPrincipalValidationUtils.validateUserPrincipal(userPrincipal, Role.ADMIN, managementService);
        if(userPrincipalValidation.getStatusCode() != HttpStatus.OK) return userPrincipalValidation;
        assert userPrincipalValidation.getBody() != null;
        Management management = (Management) userPrincipalValidation.getBody().get("data");

        if(bindingResult.hasErrors()) return ResponseEntity.badRequest().body(ApiResponse.errorStatusMsgErrors(HttpStatus.BAD_REQUEST.value(), Code.VALIDATION_FAILED, BindingResultUtils.getListOfStr(bindingResult)));

        boolean isExistsInAppUser = appUserService.fetchByEmailOrMobile(signUpDto.getEmail(), signUpDto.getMobile());
        if(isExistsInAppUser) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.statusMsg(HttpStatus.FORBIDDEN.value(), Code.ACCESS_DENIED, "The provided email or mobile number is already registered to a non-management user. Management user accounts must use unique credentials not associated with regular users."));

        // checking for conflicts
        boolean isExistsAny = managementService.existsByEmailOrMobile(signUpDto.getEmail(), signUpDto.getMobile());
        if(isExistsAny) return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.statusMsg(HttpStatus.CONFLICT.value(), Code.DUPLICATE_ENTRY, "An management user account with the provided email or mobile number already exists. Please provide unique one."));

        boolean isExistsBoth =  managementService.existsByEmailAndMobile(signUpDto.getEmail(), signUpDto.getMobile());
        if(isExistsBoth) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.statusMsg(HttpStatus.FORBIDDEN.value(), Code.ACCESS_DENIED, "The provided email and mobile number are already registered. If you already have access, please log in."));

        ServiceResponse<Management> savedNewManagementData = managementService.addNewManagementUser(signUpDto);
        if(savedNewManagementData.getStatus() == ResponseStatus.BAD_REQUEST) return ResponseEntity.badRequest().body(ApiResponse.statusMsg(HttpStatus.BAD_REQUEST.value(), Code.VALIDATION_FAILED, savedNewManagementData.getMessage()));

        Management newManagementUser = savedNewManagementData.getData();
        String token = jwtService.generateToken(newManagementUser.getUsername(), newManagementUser.getRole(), true);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.successStatusMsgData(HttpStatus.CREATED.value(),
                "New Management user account was created successfully by Management ID: " + management.getId() + "| Username: '" + management.getUsername() + "'. System-generated username and secure access token have been issued.",
                ApiResponse.usernameAndToken(newManagementUser.getUsername(), token)));
    }


    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody ManagementLoginDto loginDto, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) return ResponseEntity.badRequest().body(ApiResponse.errorStatusMsgErrors(HttpStatus.BAD_REQUEST.value(), Code.VALIDATION_FAILED, BindingResultUtils.getListOfStr(bindingResult)));

        Optional<Management> managementUser = managementService.fetchByUsername(loginDto.getUsername());
        if(managementUser.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.statusMsg(HttpStatus.UNAUTHORIZED.value(), Code.UNAUTHORIZED, "Access denied. Invalid username or password."));

        Authentication authentication = new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());   // must give the data from DTO not the entity

        Authentication authResponse;
        try {
            authResponse = authenticationManager.authenticate(authentication);
        }
        catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.statusMsg(HttpStatus.UNAUTHORIZED.value(), Code.UNAUTHORIZED, "Invalid credentials. Please check your username or password."));
        }
        catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.statusMsg(HttpStatus.NOT_FOUND.value(), Code.NOT_FOUND, "No management user account found with the given credentials."));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.statusMsg(HttpStatus.INTERNAL_SERVER_ERROR.value(), Code.INTERNAL_SERVER_ERROR,"An unexpected error occurred during login."));
        }

        UserPrincipal userPrincipal = (UserPrincipal) authResponse.getPrincipal();

        String token = jwtService.generateToken(userPrincipal.getUsername(), userPrincipal.getRole(), true);
        return ResponseEntity.ok(ApiResponse.successStatusMsgData(HttpStatus.OK.value(), "Login successful. Welcome " + userPrincipal.getFullName() , token));
    }
}
