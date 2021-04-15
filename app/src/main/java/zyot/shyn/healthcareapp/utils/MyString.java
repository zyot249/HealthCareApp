package zyot.shyn.healthcareapp.utils;

import java.util.regex.Pattern;

public class MyString {
    public static boolean isEmpty(String s) {
        if (s == null || s.trim().length() == 0) return true;
        return false;
    }

    public static boolean isNotEmpty(String s) {
        return !isEmpty(s);
    }

    public static boolean isValidEmail(String s) {
        String emailRegex = "^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(s).matches();
    }

    public static boolean isValidPassword(String s) {
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,20}$";
        Pattern pattern = Pattern.compile(passwordRegex);
        return pattern.matcher(s).matches();
    }
}
