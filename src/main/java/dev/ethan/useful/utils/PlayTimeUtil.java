package dev.ethan.useful.utils;

public class PlayTimeUtil {
    public static String formatPlayTime(long seconds) {
        if (seconds < 60) {
            return seconds + "s";
        }

        long minutes = seconds / 60;
        if (minutes < 60) {
            return minutes + "m";
        }

        long hours = minutes / 60;
        if (hours < 24) {
            return hours + "h";
        }

        long days = hours / 24;
        return days + "d";
    }
}