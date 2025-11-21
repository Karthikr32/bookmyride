package com.BusReservation.config;

import com.BusReservation.constants.Gender;
import com.BusReservation.constants.Role;
import com.BusReservation.model.Management;
import com.BusReservation.repository.ManagementRepo;
import com.BusReservation.service.ManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Slf4j
@Component
public class ManagementBootstrap implements CommandLineRunner {

    private final String FULLNAME;
    private final String EMAIL;
    private final String MOBILE;
    private final String USERNAME;
    private final String PASSWORD;
    private final ManagementService managementService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ManagementRepo managementRepo;

    public ManagementBootstrap(@Value("${bookmyride.security.fullname}") String fullName,
                               @Value("${bookmyride.security.email}") String email,
                               @Value("${bookmyride.security.mobile}") String mobile,
                               @Value("${bookmyride.security.username}") String username,
                               @Value("${bookmyride.security.password}") String password,
                               ManagementService managementService, BCryptPasswordEncoder passwordEncoder, ManagementRepo managementRepo) {
        this.FULLNAME = fullName;
        this.EMAIL = email;
        this.MOBILE = mobile;
        this.USERNAME = username;
        this.PASSWORD = password;
        this.managementService = managementService;
        this.passwordEncoder = passwordEncoder;
        this.managementRepo = managementRepo;
    }

    @Override
    public void run(String... args) throws Exception {
        if(managementService.totalManagementCount() == 0) {
            Management newManagement = new Management();    // 10
            newManagement.setFullName(FULLNAME);
            newManagement.setGender(Gender.NOT_SPECIFIED);
            newManagement.setUsername(USERNAME);
            newManagement.setPassword(passwordEncoder.encode(PASSWORD));
            newManagement.setEmail(EMAIL);
            newManagement.setMobile(MOBILE);
            newManagement.setRole(Role.ADMIN);
            newManagement.setCreatedAt(LocalDateTime.now());
            newManagement.setPasswordLastUpdatedAt(null);
            newManagement.setProfileUpdatedAt(null);

            managementRepo.save(newManagement);
            log.info("System generated & added a new Management user successfully. Ready to log in.");
        }
        else log.info("No generation, Hence management user avail in DB.");
    }
}
