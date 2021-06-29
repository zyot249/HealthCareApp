package zyot.shyn.healthcareapp.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class MyDateTimeUtils {
    public static final int MILLISECONDS_PER_DAY = 86400000;

    public static final String DATE_PATTERN_MEDIUM = "MMM dd, yyyy";
    public static final String DATE_PATTERN_WITHOUT_DAY = "MMM, yyyy";
    public static final String TIME_PATTERN_DEFAULT = "HH:mm";

    public static final SimpleDateFormat SDF_DATE_MEDIUM = new SimpleDateFormat(DATE_PATTERN_MEDIUM, Locale.getDefault());
    public static final SimpleDateFormat SDF_DATE_WITHOUT_DAY = new SimpleDateFormat(DATE_PATTERN_WITHOUT_DAY, Locale.getDefault());
    public static final SimpleDateFormat SDF_TIME_DEFAULT = new SimpleDateFormat(TIME_PATTERN_DEFAULT, Locale.getDefault());

    static {
        SDF_DATE_MEDIUM.setTimeZone(TimeZone.getDefault());
        SDF_DATE_WITHOUT_DAY.setTimeZone(TimeZone.getDefault());
        SDF_TIME_DEFAULT.setTimeZone(TimeZone.getDefault());
    }

    public static String getTimeStringDefault(Date date) {
        return SDF_TIME_DEFAULT.format(date);
    }

    public static String getTimeStringDefault(int hour, int minute) {
        Calendar calendar = getCalendarFromTime(hour, minute);
        return getTimeStringDefault(new Date(calendar.getTimeInMillis()));
    }

    public static Calendar getCalendarFromTime(int hour, int minute) {
        Calendar calendar = getCurrentCalendar();

        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        return calendar;
    }

    public static Calendar getDateFromTimeStringDefault(String time) {
        try {
            Date date = SDF_TIME_DEFAULT.parse(time);
            return getCalendarFromTime(date.getHours(), date.getMinutes());
        } catch (ParseException e) {
            return null;
        }
    }

    public static String getDateStringMedium(Date date) {
        return SDF_DATE_MEDIUM.format(date);
    }

    public static String getDateStringMedium(long timestamp) {
        return getDateStringMedium(new Date(timestamp));
    }

    public static String getDateStringMediumCurrentDay() {
        return getDateStringMedium(getCurrentTimestamp());
    }

    public static long getTimeFromDateStringMedium(String date) {
        try {
            return SDF_DATE_MEDIUM.parse(date).getTime();
        } catch (ParseException e) {
            return 0;
        }
    }

    public static String getDateStringWithoutDay(Date date) {
        return SDF_DATE_WITHOUT_DAY.format(date);
    }

    public static String getDateStringWithoutDay(long timestamp) {
        return getDateStringWithoutDay(new Date(timestamp));
    }

    public static String getDateStringWithoutDayCurrentDay() {
        return getDateStringWithoutDay(getCurrentTimestamp());
    }

    public static String getDateStringWithoutDay(int year, int month, int day) {
        Calendar calendar = getCalendarOfTime(year, month, day);
        return getDateStringWithoutDay(calendar.getTimeInMillis());
    }

    public static long getTimeFromDateStringWithoutDay(String date) {
        try {
            return SDF_DATE_WITHOUT_DAY.parse(date).getTime();
        } catch (ParseException e) {
            return 0;
        }
    }

    public static int getDiffDays(long timestamp1, long timestamp2) {
        long diffTime = timestamp1 - timestamp2;
        if (diffTime < 0)
            diffTime *= -1;
        return (int) TimeUnit.DAYS.convert(diffTime, TimeUnit.MILLISECONDS);
    }

    public static Calendar getCalendarOfTime(int year, int month, int day) {
        Calendar calendar = getCurrentCalendar();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DATE, day);
        return calendar;
    }

    public static long getStartTimeOfDate(int year, int month, int day) {
        Calendar calendar = getCalendarOfTime(year, month, day);
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

    public static int getDayOfMonth(long timestamp) {
        return getCalendarOfTimestamp(timestamp).get(Calendar.DAY_OF_MONTH);
    }

    public static long getDuration(int hour, int minute) {
        return (hour * 60 + minute) * 60 * 1000;
    }

    public static String getTimeStringDuration(long duration) {
        int hour = (int) (duration / (60 * 60 * 1000));
        int minute = (int) (duration / (60 * 1000) - hour * 60);
        String timeString = "";
        timeString += hour < 10 ? "0" + hour : "" + hour;
        timeString += ":";
        timeString += minute < 10 ? "0" + minute : "" + minute;
        return timeString;
    }
}
