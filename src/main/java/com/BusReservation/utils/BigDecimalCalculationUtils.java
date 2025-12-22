package com.BusReservation.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimalCalculationUtils {


    public static BigDecimal totalCostWithOutDiscount(BigDecimal seatsBooked, BigDecimal fare) {
        return fare.multiply(seatsBooked);
    }


    public static BigDecimal discountedAmount(BigDecimal totalCost, Integer discountPct) {
        return (totalCost.multiply(BigDecimal.valueOf(discountPct))).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }


    public static BigDecimal finalFareCost(BigDecimal totalCost, BigDecimal discountAmount) {
        return totalCost.subtract(discountAmount);
    }
}
