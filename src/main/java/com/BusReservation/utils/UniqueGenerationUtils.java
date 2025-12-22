package com.BusReservation.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class UniqueGenerationUtils {

    public static String generateTravelTicket(Integer digits, Long bookingId) {
        StringBuilder travelTicket = new StringBuilder();
        travelTicket.append("TK");

        String id = String.format("%02d", bookingId);
        travelTicket.append(id);

        UUID uuid = UUID.randomUUID();
        String random = uuid.toString().replace("-", "").substring(0, digits - travelTicket.length());
        travelTicket.append(random);
        return travelTicket.toString();
    }


    public static String generateTransactionId(Integer digits, Long bookingId) {
        StringBuilder transactionId = new StringBuilder();
        transactionId.append("TNX");

        String id = String.format("%02d", bookingId);
        transactionId.append(id);

        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmssSSS"));
        transactionId.append(time);

        UUID uuid = UUID.randomUUID();
        String random = uuid.toString().replace("-", "").substring(0, digits - transactionId.length());
        transactionId.append(random);
        return transactionId.toString();
    }


    public static String getUsername(String fullName) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("adm_");

        String[] name = fullName.trim().split(" ");
        if(name.length == 1) stringBuilder.append(name[0].trim().toLowerCase()).append("_");
        else {
            if(name[0].length() > 2) stringBuilder.append(name[0].trim().toLowerCase()).append("_");
            else stringBuilder.append(name[1].trim().toLowerCase()).append("_");
        }

        UUID uuid = UUID.randomUUID();
        String unique = uuid.toString().substring(0, 4);
        stringBuilder.append(unique);
        return stringBuilder.toString();
    }
}
