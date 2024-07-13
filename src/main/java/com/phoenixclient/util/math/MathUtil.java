package com.phoenixclient.util.math;

public class MathUtil {

    public static Number getBoundValue(Number value, Number low, Number high) {
        if (value.doubleValue() < low.doubleValue()) value = low;
        if (value.doubleValue() > high.doubleValue()) value = high;
        return value;
    }

    public static double roundDouble(double number, int sigFigs) {
        String num = String.format("%." + sigFigs + "f", number);
        return Double.parseDouble(num);
    }

    public static boolean isNumberInteger(Number number) {
        try {
            return number.doubleValue() == (int) number;
        } catch (Exception exception) {
            return false;
        }
    }

    public static boolean isNumberLong(Number number) {
        try {
            return number.doubleValue() == (long) number;
        } catch (Exception exception) {
            return false;
        }
    }

    public static boolean isNumberShort(Number number) {
        try {
            return number.doubleValue() == (short) number;
        } catch (Exception exception) {
            return false;
        }
    }

    public static boolean isNumberFloat(Number number) {
        try {
            return number.doubleValue() == (float) number;
        } catch (Exception exception) {
            return false;
        }
    }

    public static boolean isNumberDouble(Number number) {
        try {
            return number.doubleValue() == (double) number;
        } catch (Exception exception) {
            return false;
        }
    }


}
