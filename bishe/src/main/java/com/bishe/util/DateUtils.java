package com.bishe.util;

import java.time.LocalDate;

public class DateUtils {
    public static int getCurrentYear() {
        return LocalDate.now().getYear();
    }

    public static int getCurrentMonth() {
        return LocalDate.now().getMonthValue();
    }
}
