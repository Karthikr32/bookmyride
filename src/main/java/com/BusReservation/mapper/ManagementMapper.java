package com.BusReservation.mapper;

import com.BusReservation.constants.Gender;
import com.BusReservation.constants.Role;
import com.BusReservation.dto.ManagementProfileDto;
import com.BusReservation.dto.ManagementSignUpDto;
import com.BusReservation.model.Management;
import com.BusReservation.utils.UniqueGenerationUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

public class ManagementMapper {

    public static Management dtoToManagement(ManagementSignUpDto signUpDto, Gender gender, BCryptPasswordEncoder passwordEncoder) {
        Management newManagement = new Management();
        newManagement.setFullName(signUpDto.getFullName());
        newManagement.setGender(gender);
        newManagement.setUsername(UniqueGenerationUtils.getUsername(newManagement.getFullName()));
        newManagement.setPassword(passwordEncoder.encode(signUpDto.getPassword()));
        newManagement.setEmail(signUpDto.getEmail());
        newManagement.setMobile(signUpDto.getMobile());
        newManagement.setRole(Role.ADMIN);
        newManagement.setCreatedAt(LocalDateTime.now());
        newManagement.setPasswordLastUpdatedAt(null);
        newManagement.setProfileUpdatedAt(null);
        return newManagement;
    }


    public static ManagementProfileDto managementToDto(Management management) {
        ManagementProfileDto managementProfileDto = new ManagementProfileDto();
        managementProfileDto.setId(management.getId());
        managementProfileDto.setFullName(management.getFullName());
        managementProfileDto.setGender(management.getGender().getGenderName());
        managementProfileDto.setUsername(management.getUsername());
        managementProfileDto.setMobile(management.getMobile());
        managementProfileDto.setEmail(management.getEmail());
        managementProfileDto.setRole(management.getRole().getRoleName());
        managementProfileDto.setPasswordLastUpdatedAt(management.getPasswordLastUpdatedAt());
        return managementProfileDto;
    }
}
