package zyot.shyn.healthcareapp.utils;

import java.util.Calendar;

public class MyNumberUtils {
    public static int randomIntegerBetween(int min, int max) {
        if (max < min)
            return min;
        return (min == max) ? min : (min + (int) (Math.random() * ((max - min) + 1)));
    }

    public static boolean isValidBirthYear(int year) {
        int curYear = MyDateTimeUtils.getCurrentCalendar().get(Calendar.YEAR);
        return (year >= curYear - 150 && year <= curYear);
    }
}
