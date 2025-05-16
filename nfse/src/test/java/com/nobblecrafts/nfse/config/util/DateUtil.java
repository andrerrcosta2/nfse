package com.nobblecrafts.nfse.config.util;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Random;

public class DateUtil {
    private static final Random RANDOM = new Random();

    private DateUtil() {}

    public static LocalDate randomLocalDateBetween(LocalDate startInclusive, LocalDate endExclusive) {
        long daysBetween = ChronoUnit.DAYS.between(startInclusive, endExclusive);
        long randomDays = RANDOM.nextInt((int) daysBetween + 1);
        return startInclusive.plusDays(randomDays);
    }
}
