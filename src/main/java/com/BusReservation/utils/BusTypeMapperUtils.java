package com.BusReservation.utils;

import com.BusReservation.constants.AcType;
import com.BusReservation.constants.BusType;
import com.BusReservation.constants.SeatType;
import com.BusReservation.model.Bus;

import java.util.ArrayList;
import java.util.List;

public class BusTypeMapperUtils {

    public static List<Object> getAcTypeAndSeatType(Bus bus) {
        List<Object> list = new ArrayList<>();
        AcType acType;
        SeatType seatType;

        if(bus.getBusType() == BusType.AC_SLEEPER) {
            acType = AcType.AC;
            seatType = SeatType.SLEEPER;
        }
        else if(bus.getBusType() == BusType.NON_AC_SLEEPER) {
            acType = AcType.NON_AC;
            seatType = SeatType.SLEEPER;
        }
        else if(bus.getBusType() == BusType.AC_SEATER_SLEEPER) {
            acType = AcType.AC;
            seatType = SeatType.SEATER_SLEEPER;
        }
        else if(bus.getBusType() == BusType.SEMI_SLEEPER) {
            acType = AcType.NON_AC;
            seatType = SeatType.SEATER;
        }
        else if(bus.getBusType() == BusType.AC_SEMI_SLEEPER) {
            acType = AcType.AC;
            seatType = SeatType.SEATER;
        }
        else if(bus.getBusType() == BusType.DELUXE_AC) {
            acType = AcType.AC;
            seatType = SeatType.SEATER;
        }
        else if(bus.getBusType() == BusType.DELUXE_NON_AC) {
            acType = AcType.NON_AC;
            seatType = SeatType.SEATER;
        }
        else if(bus.getBusType() == BusType.ULTRA_DELUXE) {
            acType = AcType.AC;
            seatType = SeatType.SEATER;
        }
        else if(bus.getBusType() == BusType.VOLVO_AC) {
            acType = AcType.AC;
            seatType = SeatType.SEATER_SLEEPER;
        }
        else if(bus.getBusType() == BusType.SCANIA_AC) {
            acType = AcType.AC;
            seatType = SeatType.SEATER_SLEEPER;
        }
        else if(bus.getBusType() == BusType.MERCEDES_BENZ) {
            acType = AcType.AC;
            seatType = SeatType.SEATER_SLEEPER;
        }
        else if(bus.getBusType() == BusType.BHARAT_BENZ_AC) {
            acType = AcType.AC;
            seatType = SeatType.SLEEPER;
        }
        else if(bus.getBusType() == BusType.ELECTRIC_AC) {
            acType = AcType.AC;
            seatType = SeatType.SEATER;
        }
        else if(bus.getBusType() == BusType.ELECTRIC_NON_AC) {
            acType = AcType.NON_AC;
            seatType = SeatType.SEATER;
        }
        else if(bus.getBusType() == BusType.MULTI_AXLE) {
            acType = AcType.AC;
            seatType = SeatType.SEATER_SLEEPER;
        }
        else {
            acType = AcType.NOT_SPECIFIED;
            seatType = SeatType.NOT_SPECIFIED;
        }

        list.add(acType);
        list.add(seatType);
        return list;
    }
}
