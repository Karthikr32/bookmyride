package com.BusReservation.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimalCalculationUtils {


    // totalCost
    public static BigDecimal totalCostWithOutDiscount(BigDecimal seatsBooked, BigDecimal fare) {
        return fare.multiply(seatsBooked);
    }

    // will give discountAmount
    public static BigDecimal discountedAmount(BigDecimal totalCost, Integer discountPct) {
        return (totalCost.multiply(BigDecimal.valueOf(discountPct))).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    // this will give finalCost
    public static BigDecimal finalFareCost(BigDecimal totalCost, BigDecimal discountAmount) {
        return totalCost.subtract(discountAmount);
    }
}
