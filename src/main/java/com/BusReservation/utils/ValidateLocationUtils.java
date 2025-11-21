package com.BusReservation.utils;

import com.BusReservation.constants.ResponseStatus;
import com.BusReservation.model.MasterLocation;
import com.BusReservation.service.MasterLocationService;

import java.util.Map;
import java.util.Optional;

public class ValidateLocationUtils {

    public static ServiceResponse<Map<String, MasterLocation>> validateLocation(String from, String to, String whom, MasterLocationService masterLocationService) {
        if(from.equalsIgnoreCase(to)) return new ServiceResponse<>(ResponseStatus.BAD_REQUEST, "Invalid input. 'from' and 'to' locations cannot be the same.");

        Optional<MasterLocation> fromLocationOptional = masterLocationService.fetchEntityByCity(from);
        Optional<MasterLocation> toLocationOptional = masterLocationService.fetchEntityByCity(to);

        if(fromLocationOptional.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, (whom.equalsIgnoreCase("user")) ? "Sorry, we couldn't find any buses from '" + from + "'. Please check the location and try again." : "Given location '" + from + "' is not found in MasterLocation table.");
        else if(toLocationOptional.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, (whom.equalsIgnoreCase("user")) ? "Sorry, we couldn't find any buses from '" + to + "'. Please check the location and try again." : "Given location '" + to + "' is not found in MasterLocation table.");

        Map<String, MasterLocation> masterLocations = Map.of("from", fromLocationOptional.get(), "to", toLocationOptional.get());
        return new ServiceResponse<>(ResponseStatus.SUCCESS, masterLocations);
    }
}
