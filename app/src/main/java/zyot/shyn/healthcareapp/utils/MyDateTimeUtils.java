package zyot.shyn.healthcareapp.utils;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class MyDateTimeUtils {
    public static final int MILLISECONDS_PER_DAY = 86400000;

    public static int getDiffDays(long timestamp1, long timestamp2) {
        long diffTime = timestamp1 - timestamp2;
        if (diffTime < 0)
            diffTime *= -1;
        return (int) TimeUnit.DAYS.convert(diffTime, TimeUnit.MILLISECONDS);
    }

    public static long getStartTimeOfDate(int year, int month, int date) {
        Calendar calendar = getCurrentCalendar();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DATE, date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static long getStartTimeOfDate(long timestamp) {
        Calendar calendar = getCalendarOfTimestamp(timestamp);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static long getStartTimeOfCurrentDate() {
        Calendar calendar = getCurrentCalendar();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static long getCurrentTimestamp() {
        Calendar calendar = getCurrentCalendar();
        return calendar.getTimeInMillis();
    }

    public static Calendar getCurrentCalendar() {
        return Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
    }

    public static Calendar getCalendarOfTimestamp(long timestamp) {
        Calendar calendar = getCurrentCalendar();
        calendar.setTimeInMillis(timestamp);
        return calendar;
    }
}
