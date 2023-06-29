package com.mogakko.be_final.util;

public class TimeUtil {
    public static String changeSecToTime(Long totalTime) {
        long hour, min;

        min = totalTime / 60 % 60;
        hour = totalTime / 3600;

        return String.format("%02dH%02dM", hour, min);
    }

    public static Double changeSecToMin(Long timeSec) {
        return (double) Math.round((timeSec / 60.0) * 0.1);
    }

    public static String formatSeconds(Long remainingTime) {
        long hours = remainingTime / 3600;
        long minutes = (remainingTime % 3600) / 60;
        long seconds = remainingTime % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}