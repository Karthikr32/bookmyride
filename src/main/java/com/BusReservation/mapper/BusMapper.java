package com.BusReservation.mapper;
import com.BusReservation.constants.*;
import com.BusReservation.dto.*;
import com.BusReservation.model.Management;
import com.BusReservation.model.MasterLocation;
import com.BusReservation.utils.BusTypeMapperUtils;
import com.BusReservation.utils.DurationFormatUtils;
import com.BusReservation.model.Bus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class BusMapper {

    public static Bus toEntity(BusDto dto, BusType busType, State state, PermitStatus permitStatus, MasterLocation fromLocation, MasterLocation toLocation, LocalTime departureAt, Management management) {
        Bus bus = new Bus();
        bus.setBusNumber(dto.getBusNumber());
        bus.setBusName(dto.getBusName());
        bus.setBusType(busType);

        List<Object> result = BusTypeMapperUtils.getAcTypeAndSeatType(bus);
        bus.setAcType((AcType) result.get(0));
        bus.setSeatType((SeatType) result.get(1));

        bus.setStateOfRegistration(state);
        bus.setInterStatePermitStatus(permitStatus);
        bus.setCapacity(dto.getCapacity());
        bus.setAvailableSeats(dto.getCapacity());
        bus.setFromLocation(fromLocation);
        bus.setToLocation(toLocation);
        bus.setDuration(DurationFormatUtils.convertTo(dto.getHours(), dto.getMinutes()));
        bus.setDepartureAt(departureAt);
        bus.setArrivalAt(bus.getDepartureAt().plus(bus.getDuration()));
        bus.setFare(dto.getFare());
        bus.setCreatedBy(management);
        bus.setCreatedAt(LocalDateTime.now());
        bus.setUpdatedBy(null);
        bus.setUpdatedAt(null);
        return bus;
    }


    public static Bus updateExistingByDto(Bus bus, BusDto busDto, BusType busType, State state, PermitStatus permitStatus, MasterLocation fromLocation, MasterLocation toLocation, LocalTime departureAt, Management management) {
        bus.setBusNumber(busDto.getBusNumber());
        bus.setBusName(busDto.getBusName());
        bus.setBusType(busType);
        List<Object> result = BusTypeMapperUtils.getAcTypeAndSeatType(bus);
        bus.setAcType((AcType) result.get(0));
        bus.setSeatType((SeatType) result.get(1));
        bus.setStateOfRegistration(state);
        bus.setInterStatePermitStatus(permitStatus);
        bus.setCapacity(busDto.getCapacity());
        bus.setAvailableSeats(busDto.getCapacity());
        bus.setFromLocation(fromLocation);
        bus.setToLocation(toLocation);
        bus.setDepartureAt(departureAt);
        bus.setArrivalAt(bus.getDepartureAt().plus(bus.getDuration()));
        bus.setDuration(DurationFormatUtils.convertTo(busDto.getHours(), busDto.getMinutes()));
        bus.setFare(busDto.getFare());
        bus.setUpdatedBy(management);
        bus.setUpdatedAt(LocalDateTime.now());
        return bus;
    }


    public static List<BusUserResponseDto> butToBusUserDto(List<Bus> busList, LocalDate travelDate) {
        return busList.stream().map(bus -> {
            BusUserResponseDto busUserResponseDto = new BusUserResponseDto();

            busUserResponseDto.setTravelAt(travelDate);
            busUserResponseDto.setBusNumber(bus.getBusNumber());
            busUserResponseDto.setBusName(bus.getBusName());
            busUserResponseDto.setBusType(bus.getBusType().getBusTypeName());
            busUserResponseDto.setCapacity(bus.getCapacity());
            busUserResponseDto.setFromLocation(bus.getFromLocation().getCity() + ", " + bus.getFromLocation().getState() + ", " + bus.getFromLocation().getCountry());
            busUserResponseDto.setToLocation(bus.getToLocation().getCity() + ", " + bus.getToLocation().getState() + ", " + bus.getToLocation().getCountry());
            busUserResponseDto.setDuration(DurationFormatUtils.durationToStr(bus.getDuration()));
            busUserResponseDto.setDepartureAt(LocalDateTime.of(travelDate, bus.getDepartureAt()));
            busUserResponseDto.setArrivalAt(busUserResponseDto.getDepartureAt().plus(bus.getDuration()));
            busUserResponseDto.setAvailableSeats(bus.getAvailableSeats());
            busUserResponseDto.setFare(bus.getFare());
            return busUserResponseDto;
        }).toList();
    }


    public static ManagementBusDataDto busToManagementBusDataDto(Bus bus) {
        ManagementBusDataDto managementBusDataDto = new ManagementBusDataDto();

        ManagementBusDataDto.BusInfo busInfo = new ManagementBusDataDto.BusInfo();
        busInfo.setId(bus.getId());
        busInfo.setBusNumber(bus.getBusNumber());
        busInfo.setBusName(bus.getBusName());
        busInfo.setBusType(bus.getBusType().getBusTypeName());
        busInfo.setDuration(DurationFormatUtils.durationToStr(bus.getDuration()));
        busInfo.setCapacity(bus.getCapacity());
        busInfo.setAvailableSeats(bus.getAvailableSeats());
        busInfo.setAcType(bus.getAcType().name());
        busInfo.setFromLocation(bus.getFromLocation().getCity() + ", " + bus.getFromLocation().getState() + ", " + bus.getFromLocation().getCountry());
        busInfo.setToLocation(bus.getToLocation().getCity() + ", " + bus.getToLocation().getState() + ", " + bus.getToLocation().getCountry());
        busInfo.setSeatType(bus.getSeatType().name());
        busInfo.setStateOfRegistration(bus.getStateOfRegistration().getStateName());
        busInfo.setInterStatePermitStatus(bus.getInterStatePermitStatus().getPermitStatusName());
        busInfo.setBusFare(bus.getFare());


        ManagementBusDataDto.ManagementInfo createdBy = new ManagementBusDataDto.ManagementInfo();
        createdBy.setId(bus.getCreatedBy().getId());
        createdBy.setUsername(bus.getCreatedBy().getUsername());
        createdBy.setMobile(bus.getCreatedBy().getMobile());
        createdBy.setRole(bus.getCreatedBy().getRole().getRoleName());
        createdBy.setActionAt(bus.getCreatedAt());

        busInfo.setCreatedBy(createdBy);

        ManagementBusDataDto.ManagementInfo updatedBy = new ManagementBusDataDto.ManagementInfo();
        if(bus.getUpdatedBy() != null) {
            updatedBy.setId(bus.getUpdatedBy().getId());
            updatedBy.setUsername(bus.getUpdatedBy().getUsername());
            updatedBy.setMobile(bus.getUpdatedBy().getMobile());
            updatedBy.setRole(bus.getUpdatedBy().getRole().getRoleName());
            updatedBy.setActionAt(bus.getUpdatedAt());

            busInfo.setUpdatedBy(updatedBy);
        }
        else busInfo.setUpdatedBy(null);

        List<ManagementBusDataDto.BookingInfo> bookings = bus.getBookings().stream().map(booking -> {
            ManagementBusDataDto.BookingInfo bookingInfo = new ManagementBusDataDto.BookingInfo();
            bookingInfo.setBookingId(booking.getId());
            bookingInfo.setPassengerId(booking.getAppUser().getId());
            bookingInfo.setPassengerName(booking.getAppUser().getName());
            bookingInfo.setPassengerMobile(booking.getAppUser().getMobile());
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

        managementBusDataDto.setBus(busInfo);
        managementBusDataDto.setBookings(bookings);
        return managementBusDataDto;
    }
}
