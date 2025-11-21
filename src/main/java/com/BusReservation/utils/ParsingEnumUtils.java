package com.BusReservation.utils;

import com.BusReservation.constants.ResponseStatus;

public class ParsingEnumUtils {

    public static <K extends Enum<K>> ServiceResponse<K> getParsedEnumType(Class<K> enumType, String str, String type) {
        String normalizedStr = NormalizeStringUtils.getNormalize(str);
        try {
            K parsedEnum = Enum.valueOf(enumType, normalizedStr);
            return new ServiceResponse<>(ResponseStatus.SUCCESS, parsedEnum);
        }
        catch(IllegalArgumentException e) {
            return new ServiceResponse<>(ResponseStatus.BAD_REQUEST, "No data matched for the given " + type +". Please check and try again.");
        }
    }
}
