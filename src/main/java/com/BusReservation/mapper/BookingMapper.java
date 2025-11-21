package com.BusReservation.mapper;
import com.BusReservation.constants.BookingStatus;
import com.BusReservation.constants.PaymentMethod;
import com.BusReservation.constants.PaymentStatus;
import com.BusReservation.constants.Role;
import com.BusReservation.dto.*;
import com.BusReservation.model.AppUser;
import com.BusReservation.model.Booking;
import com.BusReservation.model.Bus;
import com.BusReservation.model.MasterLocation;
import com.BusReservation.utils.DurationFormat;
import com.BusReservation.utils.BigDecimalCalculationUtils;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class BookingMapper {

    public static Booking newBooking(AppUser appUser, Bus bus, BookingDto bookingDto, LocalDate date) {
        Booking booking = new Booking();         // 22
        booking.setAppUser(appUser);
        booking.setBus(bus);
        booking.setFromLocation(bus.getFromLocation());
        booking.setToLocation(bus.getToLocation());
        booking.setSeatsBooked(bookingDto.getSeatsBooked());
        booking.setBookedAt(LocalDateTime.now());            // current date is the booking date
        booking.setTravelAt(date);
        booking.setCanceledAt(null);
        booking.setDepartureAt(LocalDateTime.of(booking.getTravelAt(), bus.getDepartureAt()));
        booking.setArrivalAt(booking.getDepartureAt().plus(bus.getDuration()));
        booking.setUserEditedAt(null);

        // 1st decide and then implement once for all
        Integer discount;
        if(appUser.getIsUser() && appUser.getRole().equals(Role.USER)) discount = 5;
        else discount = 0;

        booking.setDiscountPct(discount);
        BigDecimal seatCount = BigDecimal.valueOf(booking.getSeatsBooked());          // converting integer to BigDecimal
        booking.setTotalCost(BigDecimalCalculationUtils.totalCostWithOutDiscount(seatCount, bus.getFare()));
        booking.setDiscountAmount(BigDecimalCalculationUtils.discountedAmount(booking.getTotalCost(), booking.getDiscountPct()));
        booking.setFinalCost(BigDecimalCalculationUtils.finalFareCost(booking.getTotalCost(), booking.getDiscountAmount()));

        booking.setBookingStatus(BookingStatus.PENDING);
        booking.setPaymentStatus(PaymentStatus.UNPAID);
        booking.setPaymentMethod(PaymentMethod.NONE);
        booking.setBusTicket(null);
        booking.setTransactionId(null);
        booking.setBookingExpiresAt(LocalDateTime.now().plusMinutes(10));    // expire time current time + 10 min
        return booking;
    }

    public static BookingPreviewDto getBookingPreview(Booking booking) {
        BookingPreviewDto bookingPreviewDto = new BookingPreviewDto();
        MasterLocation fromLocation = booking.getFromLocation();
        MasterLocation toLocation = booking.getToLocation();

        BookingPreviewDto.BookingInfo bookingInfo = new BookingPreviewDto.BookingInfo();   // 8
        bookingInfo.setBookingId(booking.getId());
        bookingInfo.setBookedAt(booking.getBookedAt());
        bookingInfo.setTravelAt(booking.getTravelAt());
        bookingInfo.setTotalCost(booking.getTotalCost());
        bookingInfo.setDiscount(booking.getDiscountPct() + "%");
        bookingInfo.setDiscountAmount(booking.getDiscountAmount());
        bookingInfo.setFinalCost(booking.getFinalCost());
        bookingInfo.setBookingExpiresAt(booking.getBookingExpiresAt());

        BookingPreviewDto.BusInfo busInfo = new BookingPreviewDto.BusInfo();    // 8
        busInfo.setBusNumber(booking.getBus().getBusNumber());
        busInfo.setBusName(booking.getBus().getBusName());
        busInfo.setBusType(booking.getBus().getBusType().getBusTypeName());
        busInfo.setFromLocation(fromLocation.getCity() + ", " + fromLocation.getState() + ", " + fromLocation.getCountry());
        busInfo.setToLocation(toLocation.getCity() + ", " + toLocation.getState() + ", " + toLocation.getCountry());
        busInfo.setDepartureAt(booking.getDepartureAt());
        busInfo.setArrivalAt(booking.getArrivalAt());
        busInfo.setBusFare(booking.getBus().getFare());

        BookingPreviewDto.PassengerInfo passengerInfo = new BookingPreviewDto.PassengerInfo();  // 4
        passengerInfo.setName(booking.getAppUser().getName());
        passengerInfo.setEmail(booking.getAppUser().getEmail());
        passengerInfo.setMobile(booking.getAppUser().getMobile());
        passengerInfo.setSeatsBooked(booking.getSeatsBooked());

        bookingPreviewDto.setBooking(bookingInfo);
        bookingPreviewDto.setBus(busInfo);
        bookingPreviewDto.setPassenger(passengerInfo);
        return bookingPreviewDto;
    }

    public static BookingSummaryDto bookingToBookingSummary(Booking booking) {     // 22
        BookingSummaryDto bookingSummary = new BookingSummaryDto();
        MasterLocation fromLocation = booking.getFromLocation();
        MasterLocation toLocation = booking.getToLocation();

        BookingSummaryDto.BookingInfo bookingInfo = new BookingSummaryDto.BookingInfo();  // 10
        bookingInfo.setBookingId(booking.getId());
        bookingInfo.setBookedAt(booking.getBookedAt());
        bookingInfo.setTravelAt(booking.getTravelAt());
        bookingInfo.setTotalCost(booking.getTotalCost());
        bookingInfo.setDiscount(booking.getDiscountPct() + "%");
        bookingInfo.setDiscountAmount(booking.getDiscountAmount());
        bookingInfo.setFinalCost(booking.getFinalCost());
        bookingInfo.setBookingStatus(booking.getBookingStatus().getBookingStatusName());
        bookingInfo.setPaymentStatus(booking.getPaymentStatus().getPaymentStatusName());
        bookingInfo.setBookingExpiresAt(booking.getBookingExpiresAt());


        BookingSummaryDto.BusInfo busInfo =  new BookingSummaryDto.BusInfo();   // 9
        busInfo.setBusNumber(booking.getBus().getBusNumber());
        busInfo.setBusName(booking.getBus().getBusName());
        busInfo.setBusType(booking.getBus().getBusType().getBusTypeName());
        busInfo.setFromLocation(fromLocation.getCity() + ", " + fromLocation.getState() + ", " + fromLocation.getCountry());
        busInfo.setToLocation(toLocation.getCity() + ", " + toLocation.getState() + ", " + toLocation.getCountry());
        busInfo.setDuration(DurationFormat.durationToStr(booking.getBus().getDuration()));
        busInfo.setDepartureAt(booking.getDepartureAt());
        busInfo.setArrivalAt(booking.getArrivalAt());
        busInfo.setBusFare(booking.getBus().getFare());


        BookingSummaryDto.PassengerInfo passengerInfo = new BookingSummaryDto.PassengerInfo();  // 5
        passengerInfo.setName(booking.getAppUser().getName());
        passengerInfo.setEmail(booking.getAppUser().getEmail());
        passengerInfo.setMobile(booking.getAppUser().getMobile());
        passengerInfo.setGender(booking.getAppUser().getGender().getGenderName());
        passengerInfo.setSeatsBooked(booking.getSeatsBooked());

        bookingSummary.setBooking(bookingInfo);
        bookingSummary.setBus(busInfo);
        bookingSummary.setPassenger(passengerInfo);
        return bookingSummary;
    }


    public static BookingFinalInfoDto finalBookingSummary(Booking booking) {   // 24
        BookingFinalInfoDto finalInfoDto = new BookingFinalInfoDto();   // 12/12/6
        MasterLocation fromLocation = booking.getFromLocation();
        MasterLocation toLocation = booking.getToLocation();

        BookingFinalInfoDto.BookingInfo bookingInfo = new BookingFinalInfoDto.BookingInfo();  // 12
        bookingInfo.setBookingId(booking.getId());
        bookingInfo.setBookedAt(booking.getBookedAt());
        bookingInfo.setTravelAt(booking.getTravelAt());
        bookingInfo.setTotalCost(booking.getTotalCost());
        bookingInfo.setDiscount(booking.getDiscountPct() + "%");
        bookingInfo.setDiscountAmount(booking.getDiscountAmount());
        bookingInfo.setFinalCost(booking.getFinalCost());
        bookingInfo.setBookingStatus(booking.getBookingStatus().getBookingStatusName());
        bookingInfo.setPaymentStatus(booking.getPaymentStatus().getPaymentStatusName());
        bookingInfo.setPaymentMethod(booking.getPaymentMethod().getPaymentMethodName());
        bookingInfo.setBusTicket(booking.getBusTicket());
        bookingInfo.setTransactionId(booking.getTransactionId());

        BookingFinalInfoDto.BusInfo busInfo =  new BookingFinalInfoDto.BusInfo();   // 11
        busInfo.setBusNumber(booking.getBus().getBusNumber());
        busInfo.setBusName(booking.getBus().getBusName());
        busInfo.setBusType(booking.getBus().getBusType().getBusTypeName());
        busInfo.setAcType(booking.getBus().getAcType().getAcTypeName());
        busInfo.setSeatType(booking.getBus().getSeatType().getSeatTypeName());
        busInfo.setFromLocation(fromLocation.getCity() + ", " + fromLocation.getState() + ", " + fromLocation.getCountry());
        busInfo.setToLocation(toLocation.getCity() + ", " + toLocation.getState() + ", " + toLocation.getCountry());
        busInfo.setDepartureAt(booking.getDepartureAt());
        busInfo.setArrivalAt(booking.getArrivalAt());
        busInfo.setBusFare(booking.getBus().getFare());
        busInfo.setDuration(DurationFormat.durationToStr(booking.getBus().getDuration()));


        BookingFinalInfoDto.PassengerInfo passengerInfo = new BookingFinalInfoDto.PassengerInfo();  // 5
        passengerInfo.setName(booking.getAppUser().getName());
        passengerInfo.setEmail(booking.getAppUser().getEmail());
        passengerInfo.setMobile(booking.getAppUser().getMobile());
        passengerInfo.setGender(booking.getAppUser().getGender().getGenderName());
        passengerInfo.setSeatsBooked(booking.getSeatsBooked());

        finalInfoDto.setBooking(bookingInfo);
        finalInfoDto.setBus(busInfo);
        finalInfoDto.setPassenger(passengerInfo);
        return finalInfoDto;
    }


    public static ManagementBookingDataDto bookingToManagementBookingDataDto(Booking booking) {    // 32
        ManagementBookingDataDto managementBookingDataDto = new ManagementBookingDataDto();
        MasterLocation fromLocation = booking.getFromLocation();
        MasterLocation toLocation = booking.getToLocation();

        ManagementBookingDataDto.BookingInfo bookingInfo = new ManagementBookingDataDto.BookingInfo();   // 15
        bookingInfo.setId(booking.getId());
        bookingInfo.setBookedAt(booking.getBookedAt());
        bookingInfo.setBookingEditedAt(booking.getUserEditedAt());
        bookingInfo.setBookingExpiresAt(booking.getBookingExpiresAt());
        bookingInfo.setBookingStatus(booking.getBookingStatus().getBookingStatusName());
        bookingInfo.setPaymentMethod(booking.getPaymentMethod().getPaymentMethodName());
        bookingInfo.setPaymentStatus(booking.getPaymentStatus().getPaymentStatusName());
        bookingInfo.setTravelAt(booking.getTravelAt());
        bookingInfo.setBusTicket(booking.getBusTicket());
        bookingInfo.setTransactionId(booking.getTransactionId());
        bookingInfo.setDiscount(booking.getDiscountPct() + "%");
        bookingInfo.setDiscountAmount(booking.getDiscountAmount());
        bookingInfo.setTotalCost(booking.getTotalCost());
        bookingInfo.setFinalCost(booking.getFinalCost());
        bookingInfo.setCanceledAt(booking.getCanceledAt());


        ManagementBookingDataDto.BusInfo busInfo = new ManagementBookingDataDto.BusInfo();   // 12
        busInfo.setId(booking.getBus().getId());
        busInfo.setBusNumber(booking.getBus().getBusNumber());
        busInfo.setBusName(booking.getBus().getBusName());
        busInfo.setBusType(booking.getBus().getBusType().getBusTypeName());
        busInfo.setFromLocation(fromLocation.getCity() + ", " + fromLocation.getState() + ", " + fromLocation.getCountry());
        busInfo.setToLocation(toLocation.getCity() + ", " + toLocation.getState() + ", " + toLocation.getCountry());
        busInfo.setDepartureAt(booking.getDepartureAt());
        busInfo.setArrivalAt(booking.getArrivalAt());
        busInfo.setBusFare(booking.getBus().getFare());
        busInfo.setDuration(DurationFormat.durationToStr(booking.getBus().getDuration()));
        busInfo.setAcType(booking.getBus().getAcType().getAcTypeName());
        busInfo.setSeatType(booking.getBus().getSeatType().getSeatTypeName());


        ManagementBookingDataDto.PassengerInfo passengerInfo = new ManagementBookingDataDto.PassengerInfo();    // 7
        passengerInfo.setId(booking.getAppUser().getId());
        passengerInfo.setName(booking.getAppUser().getName());
        passengerInfo.setEmail(booking.getAppUser().getEmail());
        passengerInfo.setMobile(booking.getAppUser().getMobile());
        passengerInfo.setGender(booking.getAppUser().getGender().getGenderName());
        passengerInfo.setRole(booking.getAppUser().getRole().getRoleName());
        passengerInfo.setSeatsBooked(booking.getSeatsBooked());


        managementBookingDataDto.setBooking(bookingInfo);
        managementBookingDataDto.setBus(busInfo);
        managementBookingDataDto.setPassenger(passengerInfo);
        return managementBookingDataDto;
    }


    public static List<ManagementBookingDataDto> bookingToManagementBookingDataDtoList(List<Booking> bookingList) {  // 32
        return bookingList.stream().map(BookingMapper::bookingToManagementBookingDataDto).toList();
    }


    public static Booking bookingExpired(Booking booking) {
        booking.setBookingStatus(BookingStatus.EXPIRED);
        booking.setPaymentStatus(PaymentStatus.FAILED);
        booking.setPaymentMethod(PaymentMethod.NONE);
        booking.setBusTicket(null);
        booking.setTransactionId(null);

        Bus bus = booking.getBus();
        bus.setAvailableSeats(bus.getAvailableSeats() + booking.getSeatsBooked());

        return booking;
    }


    public static Booking bookingExpiredBeforeContinue(Booking booking) {
        booking.setBookingStatus(BookingStatus.EXPIRED);
        booking.setPaymentStatus(PaymentStatus.UNPAID);
        booking.setPaymentMethod(PaymentMethod.NONE);
        booking.setBusTicket(null);
        booking.setTransactionId(null);

        Bus bus = booking.getBus();
        bus.setAvailableSeats(bus.getAvailableSeats() + booking.getSeatsBooked());

        return booking;
    }


    public static Booking editedBooking(Booking booking, AppUser appUser, LocalDate date, Long bookedSeats, Long modifiedCount) {
        booking.setAppUser(appUser);
        booking.setTravelAt(date);
        booking.setDepartureAt(LocalDateTime.of(booking.getTravelAt(), booking.getBus().getDepartureAt()));
        booking.setArrivalAt(booking.getDepartureAt().plus(booking.getBus().getDuration()));
        booking.setSeatsBooked(bookedSeats);
        booking.setUserEditedAt(LocalDateTime.now());

        Integer discount;

        if (appUser.getIsUser() && appUser.getRole() == Role.USER) discount = 5;
        else discount = 0;

        BigDecimal seatsBooked = BigDecimal.valueOf(bookedSeats);
        booking.setDiscountPct(discount);
        booking.setTotalCost(BigDecimalCalculationUtils.totalCostWithOutDiscount(seatsBooked, booking.getBus().getFare()));
        booking.setDiscountAmount(BigDecimalCalculationUtils.discountedAmount(booking.getTotalCost(), booking.getDiscountPct()));
        booking.setFinalCost(BigDecimalCalculationUtils.finalFareCost(booking.getTotalCost(), booking.getDiscountAmount()));

        Bus bus = booking.getBus();
        bus.setAvailableSeats(bus.getAvailableSeats() - modifiedCount);

        return booking;
    }
}
