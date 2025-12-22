package com.BusReservation.service;
import com.BusReservation.model.Management;
import com.BusReservation.model.AppUser;
import com.BusReservation.security.UserPrincipal;
import com.BusReservation.utils.RegExPatterns;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final AppUserService appUserService;
    private final ManagementService managementService;


    @Override
    public UserDetails loadUserByUsername(String subject) throws UsernameNotFoundException {
        if(subject.matches(RegExPatterns.MOBILE_REGEX)) {
            Optional<AppUser> appUserData = appUserService.fetchByMobile(subject);
            return appUserData.map(UserPrincipal::new).orElseThrow(() -> new UsernameNotFoundException("No user data found for the given mobile number."));
        }
        Optional<Management> adminUser = managementService.fetchByUsername(subject);
        return adminUser.map(UserPrincipal::new).orElseThrow(() ->  new UsernameNotFoundException("No management user data found for the given Username."));
    }
}
