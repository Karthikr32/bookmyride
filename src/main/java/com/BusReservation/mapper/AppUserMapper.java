package com.BusReservation.mapper;
import com.BusReservation.constants.Gender;
import com.BusReservation.constants.Role;
import com.BusReservation.dto.*;
import com.BusReservation.model.AppUser;
import com.BusReservation.model.Booking;
import com.BusReservation.model.MasterLocation;
import com.BusReservation.utils.MockDataUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.*;

public class AppUserMapper {

    public  static AppUser bookingDtoToAppUser(BookingDto bookingDto, Gender gender) {
        AppUser appUser = new AppUser();     // 10
        appUser.setName(bookingDto.getName());
        appUser.setGender(gender);
        appUser.setMobile(bookingDto.getMobile());
        appUser.setEmail(bookingDto.getEmail());
        appUser.setRole(Role.GUEST);                               // Initially GUEST
        appUser.setPassword(MockDataUtils.getDummyPassword());    // no pass, so dummy placeholder
        appUser.setIsUser(false);                                // no bcz he/she GUEST
        appUser.setIsProfileCompleted(true);                    // evenThough he/she was GUEST, I set the profile as false
        appUser.setRegisteredAt(null);
        appUser.setProfileUpdatedAt(null);
        appUser.setPasswordLastUpdatedAt(null);
        return appUser;
    }


    public static AppUser signUpDtoToAppUser(SignUpLoginDto signUpDto, BCryptPasswordEncoder passwordEncoder) {
        AppUser newAppUser = new AppUser();
        newAppUser.setName(MockDataUtils.getDummyName(signUpDto.getMobile()));
        newAppUser.setEmail(MockDataUtils.getDummyEmail(signUpDto.getMobile()));
        newAppUser.setGender(Gender.NOT_SPECIFIED);
        newAppUser.setRole(Role.USER);
        newAppUser.setMobile(signUpDto.getMobile());
        newAppUser.setPassword(passwordEncoder.encode(signUpDto.getPassword()));
        newAppUser.setIsUser(true);
        newAppUser.setIsProfileCompleted(false);           // this user is actually new to app, since he/she decided to make signup 1st and then go for bookings
        newAppUser.setRegisteredAt(LocalDateTime.now());
        newAppUser.setPasswordLastUpdatedAt(null);
        return newAppUser;
    }

    public static ManagementAppUserDataDto appUserToManagementAppUserDataDto(AppUser appUser) {
        ManagementAppUserDataDto managementAppUserDataDto = new ManagementAppUserDataDto();

        List<ManagementAppUserDataDto.BookingInfo> bookingInfos = appUser.getBookings().stream().map(booking -> {
            ManagementAppUserDataDto.BookingInfo bookingInfo = new ManagementAppUserDataDto.BookingInfo();   // 15
            bookingInfo.setBookingId(booking.getId());
            bookingInfo.setBusId(booking.getBus().getId());
            bookingInfo.setBusNumber(booking.getBus().getBusNumber());
            bookingInfo.setBusName(booking.getBus().getBusName());
            bookingInfo.setBookedAt(booking.getBookedAt());
            bookingInfo.setTravelAt(booking.getTravelAt());
            bookingInfo.setSeatsBooked(booking.getSeatsBooked());
            bookingInfo.setDepartureAt(booking.getDepartureAt());
            bookingInfo.setArrivalAt(booking.getArrivalAt());
            bookingInfo.setBookingStatus(booking.getBookingStatus().getBookingStatusName());
            bookingInfo.setPaymentStatus(booking.getPaymentStatus().getPaymentStatusName());
            bookingInfo.setPaymentMethod(booking.getPaymentMethod().getPaymentMethodName());
            bookingInfo.setBusTicket(booking.getBusTicket());
            bookingInfo.setTransactionId(booking.getTransactionId());
            bookingInfo.setFinalCost(booking.getFinalCost());
            return bookingInfo;
        }).toList();

        ManagementAppUserDataDto.PassengerInfo passengerInfo = new ManagementAppUserDataDto.PassengerInfo();   // 9
        passengerInfo.setId(appUser.getId());
        passengerInfo.setName(appUser.getName());
        passengerInfo.setMobile(appUser.getMobile());
        passengerInfo.setEmail(appUser.getEmail());
        passengerInfo.setGender(appUser.getGender().getGenderName());
        passengerInfo.setRole(appUser.getRole().getRoleName());
        passengerInfo.setProfileStatus(appUser.getRole() == Role.GUEST ? "Not Registered" : appUser.getIsProfileCompleted() ? "Completed" : "Pending");
        passengerInfo.setRegisterAt(appUser.getRole() == Role.GUEST ? null : appUser.getRegisteredAt());
        passengerInfo.setProfileLastUpdate(appUser.getRole() == Role.GUEST ? null : appUser.getProfileUpdatedAt());
        passengerInfo.setTotalBookingsCount(bookingInfos.size());

        managementAppUserDataDto.setPassenger(passengerInfo);
        managementAppUserDataDto.setBookings(bookingInfos);
        return managementAppUserDataDto;
    }

    public static UserProfileDto appUserToUserProfileDto(AppUser user) {
        UserProfileDto userProfileDto = new UserProfileDto();    // 8
        userProfileDto.setId(user.getId());
        userProfileDto.setName(user.getName());
        userProfileDto.setMobile(user.getMobile());
        userProfileDto.setEmail(user.getEmail());
        userProfileDto.setRole(user.getRole().getRoleName());
        userProfileDto.setGender(user.getGender().getGenderName());
        userProfileDto.setProfileStatus(user.getIsProfileCompleted() ? "Completed" : "Pending");
        userProfileDto.setPasswordLastUpdatedAt(user.getPasswordLastUpdatedAt());
        userProfileDto.setTotalBookingsCount(user.getBookings().size());
        return userProfileDto;
    }


    public static BookingListDto bookingListDto(Booking booking) {
        BookingListDto bookingListDto = new BookingListDto();
        MasterLocation fromLocation = booking.getFromLocation();
        MasterLocation toLocation = booking.getToLocation();

        BookingListDto.BookingInfo bookingInfo = new BookingListDto.BookingInfo();   // 16
        bookingInfo.setId(booking.getId());
        bookingInfo.setBookedAt(booking.getBookedAt());
        bookingInfo.setTravelAt(booking.getTravelAt());
        bookingInfo.setSeatsBooked(booking.getSeatsBooked());
        bookingInfo.setDepartureAt(booking.getDepartureAt());
        bookingInfo.setArrivalAt(booking.getArrivalAt());
        bookingInfo.setBookingStatus(booking.getBookingStatus().getBookingStatusName());
        bookingInfo.setPaymentStatus(booking.getPaymentStatus().getPaymentStatusName());
        bookingInfo.setPaymentMethod(booking.getPaymentMethod().getPaymentMethodName());
        bookingInfo.setBusTicket(booking.getBusTicket());
        bookingInfo.setTransactionId(booking.getTransactionId());
        bookingInfo.setTotalCost(booking.getTotalCost());
        bookingInfo.setDiscount(booking.getDiscountPct() + "%");
        bookingInfo.setDiscountAmount(booking.getDiscountAmount());
        bookingInfo.setFinalCost(booking.getFinalCost());
        bookingInfo.setCancelledAt(booking.getCanceledAt());

        BookingListDto.BusInfo busInfo = new BookingListDto.BusInfo();   // 6
        busInfo.setBusNumber(booking.getBus().getBusNumber());
        busInfo.setBusName(booking.getBus().getBusName());
        busInfo.setBusType(booking.getBus().getBusType().getBusTypeName());
        busInfo.setFromLocation(fromLocation.getCity() + ", " + fromLocation.getState() + ", " + fromLocation.getCountry());
        busInfo.setToLocation(toLocation.getCity() + ", " + toLocation.getState() + ", " + toLocation.getCountry());
        busInfo.setBusFare(booking.getBus().getFare());

        bookingListDto.setBus(busInfo);
        bookingListDto.setBooking(bookingInfo);
        return bookingListDto;
    }
}
