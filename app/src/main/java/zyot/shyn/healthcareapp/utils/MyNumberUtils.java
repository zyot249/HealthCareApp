package zyot.shyn.healthcareapp.utils;

public class MyNumberUtils {
    public static  int randomIntegerBetween(int min, int max) {
        if (max < min)
            return min;
        return (min == max) ? min : (min + (int) (Math.random() * ((max - min) + 1)));
    }
}
