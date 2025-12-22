package com.BusReservation.service;

import com.BusReservation.constants.Gender;
import com.BusReservation.constants.ResponseStatus;
import com.BusReservation.constants.Role;
import com.BusReservation.dto.*;
import com.BusReservation.mapper.AppUserMapper;
import com.BusReservation.model.AppUser;
import com.BusReservation.model.Booking;
import com.BusReservation.repository.AppUserRepo;
import com.BusReservation.utils.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppUserService {

    private final AppUserRepo appUserRepo;
    private final BCryptPasswordEncoder passwordEncoder;
    private final BookingService bookingService;


    public Optional<AppUser> fetchById(Long id) {
        return appUserRepo.findById(id);
    }


    public ServiceResponse<ApiPageResponse<List<ManagementAppUserDataDto>>> getAllAppUserData(PaginationRequest request, String keyword) {
        Pageable pageable = PaginationRequest.getPageable(request);
        Integer pageNum = pageable.getPageNumber() + 1;

        if(keyword == null || keyword.isBlank()) {
            Page<AppUser> pageData = appUserRepo.findAll(pageable);
            List<ManagementAppUserDataDto> appUserList = pageData.stream().map(AppUserMapper::appUserToManagementAppUserDataDto).toList();

            if(pageData.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No users data found in this page " + pageNum, new ApiPageResponse<>(appUserList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
            return new ServiceResponse<>(ResponseStatus.SUCCESS, "The available users data list in this page " + pageNum, new ApiPageResponse<>(appUserList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
        }

        if(keyword.startsWith("id_")) {
            Long appUserId =  Long.parseLong(keyword.substring(3).trim());
            if(appUserId <= 0) return new ServiceResponse<>(ResponseStatus.BAD_REQUEST, "Invalid ID. ID cannot be 0 or less than 0");

            Page<AppUser> pageData = appUserRepo.findById(appUserId, pageable);
            List<ManagementAppUserDataDto> appUserList = pageData.stream().map(AppUserMapper::appUserToManagementAppUserDataDto).toList();

            if(pageData.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No users data found for the given ID " + appUserId + " in this page " + pageNum, new ApiPageResponse<>(appUserList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
            return new ServiceResponse<>(ResponseStatus.SUCCESS, "The users data found for the given ID " + appUserId + " in this page " + pageNum, new ApiPageResponse<>(appUserList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
        }
        else if(keyword.startsWith("mobile_")) {
            String mobile = keyword.substring(7).trim();

            if(!mobile.matches(RegExPatterns.MOBILE_REGEX)) return new ServiceResponse<>(ResponseStatus.BAD_REQUEST, "Invalid mobile number. Mobile number must starts with either 6 or 7 or 8 or 9 and followed by 9 digits");

            Page<AppUser> pageData = appUserRepo.findByMobile(mobile, pageable);
            List<ManagementAppUserDataDto> appUserList = pageData.stream().map(AppUserMapper::appUserToManagementAppUserDataDto).toList();

            if(pageData.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No users data found for the given Mobile Number in this page " + pageNum, new ApiPageResponse<>(appUserList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
            return new ServiceResponse<>(ResponseStatus.SUCCESS, "The users data found for the given Mobile Number in this page " + pageNum, new ApiPageResponse<>(appUserList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
        }
        else if(keyword.matches(RegExPatterns.EMAIL_REGEX)) {
            Page<AppUser> pageData = appUserRepo.findByEmail(keyword, pageable);
            List<ManagementAppUserDataDto> appUserList = pageData.stream().map(AppUserMapper::appUserToManagementAppUserDataDto).toList();

            if(pageData.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No users data found for the given Email in this page " + pageNum, new ApiPageResponse<>(appUserList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
            return new ServiceResponse<>(ResponseStatus.SUCCESS, "The users data found for the given Email in this page " + pageNum, new ApiPageResponse<>(appUserList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
        }
        else if(keyword.startsWith("user_")) {
            String appUserName = keyword.substring(5).trim();
            if(!appUserName.matches(RegExPatterns.NAME_REGEX)) return new ServiceResponse<>(ResponseStatus.BAD_REQUEST, "Invalid App-User Name. Only alphabets and spaces are allowed (e.g., 'John Doe'). No numbers or special characters.");

            Page<AppUser> pageData = appUserRepo.findByName(appUserName, pageable);
            List<ManagementAppUserDataDto> appUserList = pageData.stream().map(AppUserMapper::appUserToManagementAppUserDataDto).toList();

            if(pageData.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No App-user data found for the given Name in this page " + pageNum, new ApiPageResponse<>(appUserList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
            return new ServiceResponse<>(ResponseStatus.SUCCESS, "users data found for the given Name in this page " + pageNum, new ApiPageResponse<>(appUserList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
        }
        return this.getDataByKeyword(keyword, pageable);
    }


    public Optional<AppUser> fetchByMobile(String mobile) {
        return appUserRepo.findByMobile(mobile);
    }


    public boolean fetchByEmailOrMobile(String email, String mobile) {
        return appUserRepo.existsByEmailOrMobile(email, mobile);
    }


    public ServiceResponse<ApiPageResponse<List<ManagementAppUserDataDto>>> getDataByKeyword(String keyword, Pageable pageable) {
        Integer pageNum = pageable.getPageNumber() + 1;

        if(keyword.matches(RegExPatterns.GENDER_REGEX)) {
            ServiceResponse<Gender> genderEnumResult = ParsingEnumUtils.getParsedEnumType(Gender.class, keyword, "Gender");
            if(genderEnumResult.getStatus() == ResponseStatus.BAD_REQUEST) return new ServiceResponse<>(genderEnumResult.getStatus(), genderEnumResult.getMessage());

            Page<AppUser> pageData = appUserRepo.findByGender(genderEnumResult.getData(), pageable);
            List<ManagementAppUserDataDto> appUserList = pageData.stream().map(AppUserMapper::appUserToManagementAppUserDataDto).toList();

            if(pageData.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No users data found for the given Gender in this page " + pageNum, new ApiPageResponse<>(appUserList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
            return new ServiceResponse<>(ResponseStatus.SUCCESS, "Users data found for the given Gender in this page " + pageNum, new ApiPageResponse<>(appUserList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
        }
        else if(keyword.matches(RegExPatterns.ROLE_REGEX)) {
            ServiceResponse<Role> roleEnumResult = ParsingEnumUtils.getParsedEnumType(Role.class, keyword, "Role");
            if(roleEnumResult.getStatus() == ResponseStatus.BAD_REQUEST) return new ServiceResponse<>(roleEnumResult.getStatus(), roleEnumResult.getMessage());

            Page<AppUser> pageData = appUserRepo.findByRole(roleEnumResult.getData(), pageable);
            List<ManagementAppUserDataDto> appUserList = pageData.stream().map(AppUserMapper::appUserToManagementAppUserDataDto).toList();

            if(pageData.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No users data found for the given Role in this page " + pageNum, new ApiPageResponse<>(appUserList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
            return new ServiceResponse<>(ResponseStatus.SUCCESS, "Users data found for the given Role in this page " + pageNum, new ApiPageResponse<>(appUserList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
        }
        return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No users data found for the given Input in this page " + pageNum, new ApiPageResponse<>(List.of(), 0, 0L, pageable.getPageNumber(), pageable.getPageSize(), true, true));
    }


    public AppUser newSignedUpUser(SignUpLoginDto signUpDto) {
        return appUserRepo.save(AppUserMapper.signUpDtoToAppUser(signUpDto, passwordEncoder));
    }


    public AppUser upgradeAppUserToUser(AppUser appUser, SignUpLoginDto signUpDto) {
        appUser.setRole(Role.USER);
        appUser.setPassword(passwordEncoder.encode(signUpDto.getPassword()));
        appUser.setIsUser(true);
        appUser.setRegisteredAt(LocalDateTime.now());
        appUser.setIsProfileCompleted(true);
        appUser.setPasswordLastUpdatedAt(null);
        return appUserRepo.save(appUser);
    }


    public ServiceResponse<AppUser> createNewAppUser(BookingDto bookingDto) {
        boolean isExists = appUserRepo.existsByEmail(bookingDto.getEmail());
        if(isExists) return new ServiceResponse<>(ResponseStatus.CONFLICT, "Given Email ID is already in use. Please verify and try again.");

        ServiceResponse<Gender> genderEnumResult = ParsingEnumUtils.getParsedEnumType(Gender.class, bookingDto.getGender(), "Gender");
        if(genderEnumResult.getStatus() == ResponseStatus.BAD_REQUEST) return new ServiceResponse<>(genderEnumResult.getStatus(), genderEnumResult.getMessage());
        Gender gender = genderEnumResult.getData();

        AppUser savedUser = appUserRepo.save(AppUserMapper.bookingDtoToAppUser(bookingDto, gender));
        return new ServiceResponse<>(ResponseStatus.SUCCESS, savedUser);
    }


    public ServiceResponse<AppUser> updateAppUserProfile(AppUser appUser, BookingDto bookingDto) {
        boolean isExists = appUserRepo.existsByEmail(bookingDto.getEmail());
        if(isExists) return new ServiceResponse<>(ResponseStatus.CONFLICT, "Given Email ID is already in use. Please verify and try again.");

        ServiceResponse<Gender> genderEnumResult = ParsingEnumUtils.getParsedEnumType(Gender.class, bookingDto.getGender(), "Gender");
        if(genderEnumResult.getStatus() == ResponseStatus.BAD_REQUEST) return new ServiceResponse<>(genderEnumResult.getStatus(), genderEnumResult.getMessage());
        Gender gender = genderEnumResult.getData();

        appUser.setName(bookingDto.getName());
        appUser.setEmail(bookingDto.getEmail());
        appUser.setGender(gender);
        appUser.setIsProfileCompleted(true);
        AppUser savedUser = appUserRepo.save(appUser);
        return new ServiceResponse<>(ResponseStatus.SUCCESS, savedUser);
    }


    public ServiceResponse<AppUser> updateExistedAppUserData(AppUser appUser, @Valid EditBookingDto bookingDto) {
        boolean isEmailExists = appUserRepo.existsByEmail(bookingDto.getEmail());

        if(isEmailExists && !appUser.getEmail().equalsIgnoreCase(bookingDto.getEmail()))
            return new ServiceResponse<>(ResponseStatus.CONFLICT, "The provided email address is already in use. Please use a different one.");

        boolean isModified = false;
        if (!appUser.getName().equalsIgnoreCase(bookingDto.getName())) {
            isModified = true;
            appUser.setName(bookingDto.getName());
        }
        if(!appUser.getGender().getGenderName().equalsIgnoreCase(bookingDto.getGender())) {
            isModified = true;
            ServiceResponse<Gender> genderEnumResult = ParsingEnumUtils.getParsedEnumType(Gender.class, bookingDto.getGender(), "Gender");
            if(genderEnumResult.getStatus() == ResponseStatus.BAD_REQUEST) return new ServiceResponse<>(genderEnumResult.getStatus(), genderEnumResult.getMessage());
            appUser.setGender(genderEnumResult.getData());
        }
        if(!appUser.getEmail().equalsIgnoreCase(bookingDto.getEmail())) {
            isModified = true;
            appUser.setEmail(bookingDto.getEmail());
        }
        AppUser updatedUser = (isModified) ? appUserRepo.save(appUser) : appUser;
        return new ServiceResponse<>(ResponseStatus.SUCCESS, updatedUser);
    }


    public ServiceResponse<UserProfileDto> fetchUserProfile(AppUser appUser) {
        if(appUser.getIsProfileCompleted()) return new ServiceResponse<>(ResponseStatus.SUCCESS, "Profile loaded successfully.", AppUserMapper.appUserToUserProfileDto(appUser));
        return new ServiceResponse<>(ResponseStatus.FORBIDDEN, "Your profile is not completed yet. Please complete your profile by using update profile API in order to view.");
    }


    public ServiceResponse<ApiPageResponse<List<BookingListDto>>> getBookingList(Long id, PaginationRequest request) {
        Pageable pageable = PaginationRequest.getPageable(request);

        Page<Booking> pageData = bookingService.fetchUserBooking(id, pageable);
        List<BookingListDto> bookingList = pageData.stream().map(AppUserMapper::bookingListDto).toList();

        if(pageData.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No bookings found yet. Start your first journey by booking a ride now!", new ApiPageResponse<>(bookingList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
        return new ServiceResponse<>(ResponseStatus.SUCCESS, "Here’s your booking history. You can view, manage your upcoming trips here.", new ApiPageResponse<>(bookingList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
    }


    public ServiceResponse<ApiPageResponse<List<BookedAppUserReportDto>>> getBookedAppUserReport(LocalDateTime startDateTime, LocalDateTime endDateTime, PaginationRequest request, String gender, String role) {
        Pageable pageable = PaginationRequest.getPageable(request);
        Integer pageNum = pageable.getPageNumber() + 1;

        if((gender != null && !gender.isBlank()) && (role != null && !role.isBlank())) {
            if(gender.matches(RegExPatterns.GENDER_REGEX) && role.matches(RegExPatterns.ROLE_REGEX)) {
                ServiceResponse<Gender> genderEnumResult = ParsingEnumUtils.getParsedEnumType(Gender.class, gender, "Gender");
                ServiceResponse<Role> roleEnumResult = ParsingEnumUtils.getParsedEnumType(Role.class, role, "Role");

                if(genderEnumResult.getStatus() == ResponseStatus.BAD_REQUEST) return new ServiceResponse<>(genderEnumResult.getStatus(), genderEnumResult.getMessage());
                else if(roleEnumResult.getStatus() == ResponseStatus.BAD_REQUEST) return new ServiceResponse<>(roleEnumResult.getStatus(), roleEnumResult.getMessage());

                Page<BookedAppUserReportDto> response =  appUserRepo.findByBookedAppUserDataByGenderAndRole(startDateTime, endDateTime, genderEnumResult.getData(), roleEnumResult.getData(), pageable);
                if(response.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No Users data found for the given gender in this page " + pageNum, new ApiPageResponse<>(response.getContent(), response.getTotalPages(), response.getTotalElements(), response.getNumber(), response.getSize(), response.isFirst(), response.isEmpty()));
                return new ServiceResponse<>(ResponseStatus.SUCCESS, "Users data found for the given gender in this page " + pageNum, new ApiPageResponse<>(response.getContent(), response.getTotalPages(), response.getTotalElements(), response.getNumber(), response.getSize(), response.isFirst(), response.isEmpty()));
            }
            return new ServiceResponse<>(ResponseStatus.BAD_REQUEST, "Invalid gender & role input. Gender would be either male or female and Role would be User or Guest.");
        }
        else if(gender != null && !gender.isBlank()) {
            if(gender.matches(RegExPatterns.GENDER_REGEX)) {
                ServiceResponse<Gender> genderEnumResult = ParsingEnumUtils.getParsedEnumType(Gender.class, gender, "Gender");
                if(genderEnumResult.getStatus() == ResponseStatus.BAD_REQUEST) return new ServiceResponse<>(genderEnumResult.getStatus(), genderEnumResult.getMessage());

                Page<BookedAppUserReportDto> response = appUserRepo.findByBookedAppUserDataByGender(startDateTime, endDateTime, genderEnumResult.getData(), pageable);

                if(response.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No Users data found for the given gender in this page " + pageNum, new ApiPageResponse<>(response.getContent(), response.getTotalPages(), response.getTotalElements(), response.getNumber(), response.getSize(), response.isFirst(), response.isEmpty()));
                return new ServiceResponse<>(ResponseStatus.SUCCESS, "Users data found for the given gender in this page " + pageNum, new ApiPageResponse<>(response.getContent(), response.getTotalPages(), response.getTotalElements(), response.getNumber(), response.getSize(), response.isFirst(), response.isEmpty()));
            }
            return new ServiceResponse<>(ResponseStatus.BAD_REQUEST, "Invalid gender input. Gender would be either male or female.");
        }
        else if(role != null && !role.isBlank()) {
            if(role.matches(RegExPatterns.ROLE_REGEX)) {
                ServiceResponse<Role> roleEnumResult = ParsingEnumUtils.getParsedEnumType(Role.class, role, "Role");
                if(roleEnumResult.getStatus() == ResponseStatus.BAD_REQUEST) return new ServiceResponse<>(roleEnumResult.getStatus(), roleEnumResult.getMessage());

                Page<BookedAppUserReportDto> response = appUserRepo.findByBookedAppUserDataByRole(startDateTime, endDateTime, roleEnumResult.getData(), pageable);

                if(response.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No Users data found for the given role in this page " + pageNum, new ApiPageResponse<>(response.getContent(), response.getTotalPages(), response.getTotalElements(), response.getNumber(), response.getSize(), response.isFirst(), response.isEmpty()));
                return new ServiceResponse<>(ResponseStatus.SUCCESS, "Users data found for the given role in this page " + pageNum, new ApiPageResponse<>(response.getContent(), response.getTotalPages(), response.getTotalElements(), response.getNumber(), response.getSize(), response.isFirst(), response.isEmpty()));
            }
            return new ServiceResponse<>(ResponseStatus.BAD_REQUEST, "Invalid role input. Role would be either guest or user.");
        }
        Page<BookedAppUserReportDto> pageData =  appUserRepo.findByBookedAppUserData(startDateTime, endDateTime, pageable);
        if(pageData.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No Users data found for the given dates in this page " + pageNum, new ApiPageResponse<>(pageData.getContent(), pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
        return new ServiceResponse<>(ResponseStatus.SUCCESS, "Users data found for the given dates in this page " + pageNum, new ApiPageResponse<>(pageData.getContent(), pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
    }


    public ServiceResponse<UserProfileDto> updateProfile(AppUser appUser, UpdateUserProfileDto userProfileDto) {
        boolean isEmailExists = appUserRepo.existsByEmail(userProfileDto.getEmail());

        if(!appUser.getIsProfileCompleted()) {
            if(isEmailExists) return new ServiceResponse<>(ResponseStatus.CONFLICT, "The provided email address is already in use. Please use a different one.");

            ServiceResponse<AppUser> updateResponse = this.applyProfileUpdates(appUser, userProfileDto);
            if(updateResponse.getStatus() == ResponseStatus.BAD_REQUEST) return new ServiceResponse<>(updateResponse.getStatus(), updateResponse.getMessage());

            updateResponse.getData().setIsProfileCompleted(true);
            AppUser updatedUser = appUserRepo.save(updateResponse.getData());
            return new ServiceResponse<>(ResponseStatus.SUCCESS, "Your profile updated successfully.", AppUserMapper.appUserToUserProfileDto(updatedUser));
        }

        if(isEmailExists && !appUser.getEmail().equalsIgnoreCase(userProfileDto.getEmail()))
            return new ServiceResponse<>(ResponseStatus.CONFLICT, "The provided email address is already in use. Please provide an unique one.");

        ServiceResponse<AppUser> updateResponse = this.applyProfileUpdates(appUser, userProfileDto);
        if(updateResponse.getStatus() == ResponseStatus.BAD_REQUEST) return new ServiceResponse<>(updateResponse.getStatus(), updateResponse.getMessage());

        AppUser updatedUser = appUserRepo.save(updateResponse.getData());
        return new ServiceResponse<>(ResponseStatus.SUCCESS, "Your profile updated successfully.", AppUserMapper.appUserToUserProfileDto(updatedUser));
    }


    public ServiceResponse<AppUser> applyProfileUpdates(AppUser appUser, UpdateUserProfileDto userProfileDto) {
        ServiceResponse<Gender> genderEnumResult = ParsingEnumUtils.getParsedEnumType(Gender.class, userProfileDto.getGender(), "Gender");
        if(genderEnumResult.getStatus() == ResponseStatus.BAD_REQUEST) return new ServiceResponse<>(genderEnumResult.getStatus(), genderEnumResult.getMessage());

        appUser.setName(userProfileDto.getName());
        appUser.setEmail(userProfileDto.getEmail());
        appUser.setGender(genderEnumResult.getData());
        appUser.setProfileUpdatedAt(LocalDateTime.now());
        return new ServiceResponse<>(ResponseStatus.SUCCESS, appUser);
    }


    public ServiceResponse<String> changeUserNewPassword(AppUser user, @Valid ChangeUserPasswordDto changeUserPasswordDto) {
        user.setPassword(passwordEncoder.encode(changeUserPasswordDto.getNewPassword()));
        user.setPasswordLastUpdatedAt(LocalDateTime.now());
        appUserRepo.save(user);

        return new ServiceResponse<>(ResponseStatus.SUCCESS, "New password updated successfully.");
    }


    public ServiceResponse<AppUser> createOrUpdateAppUserForBooking(Optional<AppUser> existingAppUser, BookingDto bookingDto) {
        AppUser appUser;

        if(existingAppUser.isPresent()) {
            if(!existingAppUser.get().getIsProfileCompleted()) {
                ServiceResponse<AppUser> updatedAppUser = this.updateAppUserProfile(existingAppUser.get(), bookingDto);
                if(updatedAppUser.getStatus() == ResponseStatus.BAD_REQUEST) return new ServiceResponse<>(ResponseStatus.BAD_REQUEST, updatedAppUser.getMessage());
                else if(updatedAppUser.getStatus() == ResponseStatus.CONFLICT) return new ServiceResponse<>(ResponseStatus.CONFLICT, updatedAppUser.getMessage());
                else appUser = updatedAppUser.getData();
            }
            else {
                if(!existingAppUser.get().getEmail().equalsIgnoreCase(bookingDto.getEmail())) return new ServiceResponse<>(ResponseStatus.CONFLICT, "The provided mobile number is already registered with a different email. Please verify your details or use a different mobile/email for booking.");
                else appUser = existingAppUser.get();
            }
        }
        else {
            ServiceResponse<AppUser> newAppUser = this.createNewAppUser(bookingDto);
            if (newAppUser.getStatus() == ResponseStatus.BAD_REQUEST) return new ServiceResponse<>(ResponseStatus.BAD_REQUEST, newAppUser.getMessage());
            else if(newAppUser.getStatus() == ResponseStatus.CONFLICT) return new ServiceResponse<>(ResponseStatus.CONFLICT, newAppUser.getMessage());
            else appUser = newAppUser.getData();
        }
        return new ServiceResponse<>(ResponseStatus.SUCCESS, appUser);
    }
}
