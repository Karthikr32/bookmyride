package com.BusReservation.controller;
import com.BusReservation.constants.Code;
import com.BusReservation.constants.Role;
import com.BusReservation.dto.SignUpLoginDto;
import com.BusReservation.model.AppUser;
import com.BusReservation.security.JwtService;
import com.BusReservation.security.UserPrincipal;
import com.BusReservation.service.ManagementService;
import com.BusReservation.service.AppUserService;
import com.BusReservation.utils.ApiResponse;
import com.BusReservation.utils.BindingResultUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/bookmyride/public")
public class UserAuthController {

    private final AppUserService appUserService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ManagementService managementService;


    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody SignUpLoginDto signUpDto, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) return ResponseEntity.badRequest().body(ApiResponse.errorStatusMsgErrors(HttpStatus.BAD_REQUEST.value(), Code.VALIDATION_FAILED, BindingResultUtils.getListOfStr(bindingResult)));

        boolean isExists = managementService.ifMobileNumberExists(signUpDto.getMobile());
        if(isExists) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.statusMsg(HttpStatus.FORBIDDEN.value(), Code.ACCESS_DENIED, "Access denied for this role."));

        Optional<AppUser> existedAppUser = appUserService.fetchByMobile(signUpDto.getMobile());
        if(existedAppUser.isPresent()) {    // existed user
            AppUser appUser = existedAppUser.get();

            if(appUser.getIsUser() && appUser.getRole() == Role.USER) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.statusMsg(HttpStatus.FORBIDDEN.value(), Code.ACCESS_DENIED, "Mobile number is already registered. Cannot register again."));

            AppUser updatedUser = appUserService.upgradeAppUserToUser(appUser, signUpDto);
            String token = jwtService.generateToken(updatedUser.getMobile(), updatedUser.getRole(), true);
            return ResponseEntity.ok(ApiResponse.successStatusMsgData(HttpStatus.OK.value(), "Upgrade complete. Guest user is now a registered user.", token));
        }  // new

        AppUser newUser = appUserService.newSignedUpUser(signUpDto);
        String token = jwtService.generateToken(newUser.getMobile(), newUser.getRole(), true);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.successStatusMsgData(HttpStatus.CREATED.value(), "Welcome! Your account has been created successfully.", token));
    }


    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody SignUpLoginDto loginDto, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) return ResponseEntity.badRequest().body(ApiResponse.errorStatusMsgErrors(HttpStatus.BAD_REQUEST.value(), Code.VALIDATION_FAILED, BindingResultUtils.getListOfStr(bindingResult)));

        boolean isExists = managementService.ifMobileNumberExists(loginDto.getMobile());
        if(isExists) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.statusMsg(HttpStatus.FORBIDDEN.value(), Code.ACCESS_DENIED, "Access denied for this role."));

        Optional<AppUser> userData = appUserService.fetchByMobile(loginDto.getMobile());
        if(userData.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.statusMsg(HttpStatus.UNAUTHORIZED.value(), Code.UNAUTHORIZED, "Invalid mobile number or password."));

        AppUser user = userData.get();
        if(user.getRole() == Role.GUEST) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.statusMsg(HttpStatus.FORBIDDEN.value(), Code.ACCESS_DENIED, "Access denied. Please complete registration before logging in."));

        Authentication authentication = new UsernamePasswordAuthenticationToken(loginDto.getMobile(), loginDto.getPassword());

        Authentication authResponse;
        try {
            authResponse = authenticationManager.authenticate(authentication);         // this line invokes our custom MyUserDetailsService class for serDetailsService to load user data
        }
        catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.statusMsg(HttpStatus.UNAUTHORIZED.value(), Code.UNAUTHORIZED, "Invalid credentials. Please check your mobile number or password."));
        }
        catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.statusMsg(HttpStatus.NOT_FOUND.value(), Code.NOT_FOUND, "No user account found with the given credentials."));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.statusMsg(HttpStatus.INTERNAL_SERVER_ERROR.value(), Code.INTERNAL_SERVER_ERROR, "An unexpected error occurred during login."));
        }

        UserPrincipal userPrincipal = (UserPrincipal) authResponse.getPrincipal();
        String token = jwtService.generateToken(userPrincipal.getUsername(), userPrincipal.getRole(), true);
        return ResponseEntity.ok(ApiResponse.successStatusMsgData(HttpStatus.OK.value(), "Welcome back! Login successful.", token));
    }
}
