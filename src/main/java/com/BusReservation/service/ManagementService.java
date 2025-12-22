package com.BusReservation.service;
import com.BusReservation.constants.*;
import com.BusReservation.dto.*;
import com.BusReservation.mapper.ManagementMapper;
import com.BusReservation.model.Management;
import com.BusReservation.repository.ManagementRepo;
import com.BusReservation.utils.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ManagementService {

    private final ManagementRepo managementRepo;
    private final BCryptPasswordEncoder passwordEncoder;


    public Long totalManagementCount() {
        return managementRepo.count();
    }


    public Optional<Management> fetchById(Long id) {
        return managementRepo.findById(id);
    }


    public boolean existsByEmailOrMobile(String email, String mobile) {
        return managementRepo.existsByEmailOrMobile(email, mobile);
    }


    public boolean existsByEmailAndMobile(String email, String mobile) {
        return managementRepo.existsByEmailAndMobile(email, mobile);
    }


    public ServiceResponse<Management> addNewManagementUser(@Valid ManagementSignUpDto signUpDto) {
        ServiceResponse<Gender> genderEnumParsing = ParsingEnumUtils.getParsedEnumType(Gender.class, signUpDto.getGender(), "Gender");
        if(genderEnumParsing.getStatus() == ResponseStatus.BAD_REQUEST) return new ServiceResponse<>(genderEnumParsing.getStatus(), genderEnumParsing.getMessage());
        Gender gender = genderEnumParsing.getData();
        Management savedManagementData = managementRepo.save(ManagementMapper.dtoToManagement(signUpDto, gender, passwordEncoder));
        return new ServiceResponse<>(ResponseStatus.SUCCESS, savedManagementData);
    }


    public Optional<Management> fetchByUsername(String username) {
        return managementRepo.findByUsername(username);
    }


    public ServiceResponse<ManagementProfileDto> fetchProfileData(Management management) {
        return new ServiceResponse<>(ResponseStatus.SUCCESS, "Management user profile loaded successfully.", ManagementMapper.managementToDto(management));
    }


    public boolean ifMobileNumberExists(String mobile) {
        return managementRepo.existsByMobile(mobile);
    }


    public ServiceResponse<ManagementProfileDto> updateProfile(Management management, @Valid UpdateManagementProfileDto updateProfileDto) {
        boolean mobileIsExists = this.ifMobileNumberExists(updateProfileDto.getMobile());
        boolean emailIsExists = managementRepo.existsByEmail(updateProfileDto.getEmail());

        Management updatedManagementData;
        boolean isModified = false;
        boolean isFullNameModified = false;
        String message;

        if((mobileIsExists && !management.getMobile().equals(updateProfileDto.getMobile())))
            return new ServiceResponse<>(ResponseStatus.CONFLICT, "The provided mobile number is already in use. Please provide an unique one.");

        if((emailIsExists && !management.getEmail().equalsIgnoreCase(updateProfileDto.getEmail())))
            return new ServiceResponse<>(ResponseStatus.CONFLICT, "The provided email ID is already in use. Please provide an unique one.");

        if(!management.getFullName().equalsIgnoreCase(updateProfileDto.getFullName())) {
            isModified = true;
            isFullNameModified = true;
            management.setFullName(updateProfileDto.getFullName());
            management.setUsername(UniqueGenerationUtils.getUsername(management.getFullName()));
        }

        if(!management.getGender().getGenderName().equalsIgnoreCase(updateProfileDto.getGender().trim().toUpperCase())) {
            isModified = true;
            ServiceResponse<Gender> genderEnumResult = ParsingEnumUtils.getParsedEnumType(Gender.class, updateProfileDto.getGender(), "Gender");
            if(genderEnumResult.getStatus() == ResponseStatus.BAD_REQUEST) return new ServiceResponse<>(genderEnumResult.getStatus(), genderEnumResult.getMessage());
            management.setGender(genderEnumResult.getData());
        }

        if(!management.getEmail().equalsIgnoreCase(updateProfileDto.getEmail())) {
            isModified = true;
            management.setEmail(updateProfileDto.getEmail());
        }

        if(!management.getMobile().equalsIgnoreCase(updateProfileDto.getMobile())) {
            isModified = true;
            management.setMobile(updateProfileDto.getMobile());
        }

        if(isModified) {
            management.setProfileUpdatedAt(LocalDateTime.now());
            updatedManagementData = managementRepo.save(management);
            message = (isFullNameModified) ? " Your updated username is '" + updatedManagementData.getUsername() + "'. For security purposes, Please log in again." : "";
        }
        else {
            updatedManagementData = management;
            message = "";
        }
        return new ServiceResponse<>(ResponseStatus.SUCCESS, "Profile data updated successfully." + message, ManagementMapper.managementToDto(updatedManagementData));
    }


    public ServiceResponse<String> changeNewPassword(Management management, @Valid ChangeManagementPasswordDto changePasswordDto) {
        management.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
        management.setPasswordLastUpdatedAt(LocalDateTime.now());
        managementRepo.save(management);
        return new ServiceResponse<>(ResponseStatus.SUCCESS, "Password updated successfully.");
    }
}