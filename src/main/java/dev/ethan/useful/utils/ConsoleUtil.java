package dev.ethan.useful.utils;

public final class ConsoleUtil {

    private ConsoleUtil() {}

    private static final java.util.Map<Character, String> ANSI = java.util.Map.ofEntries(
            java.util.Map.entry('0', "\u001B[30m"),
            java.util.Map.entry('1', "\u001B[34m"),
            java.util.Map.entry('2', "\u001B[32m"),
            java.util.Map.entry('3', "\u001B[36m"),
            java.util.Map.entry('4', "\u001B[31m"),
            java.util.Map.entry('5', "\u001B[35m"),
            java.util.Map.entry('6', "\u001B[33m"),
            java.util.Map.entry('7', "\u001B[37m"),
            java.util.Map.entry('8', "\u001B[90m"),
            java.util.Map.entry('9', "\u001B[94m"),
            java.util.Map.entry('a', "\u001B[92m"),
            java.util.Map.entry('b', "\u001B[96m"),
            java.util.Map.entry('c', "\u001B[91m"),
            java.util.Map.entry('d', "\u001B[95m"),
            java.util.Map.entry('e', "\u001B[93m"),
            java.util.Map.entry('f', "\u001B[97m"),
            java.util.Map.entry('r', "\u001B[0m")
    );

    public static String colorize(String msg) {
        StringBuilder out = new StringBuilder();
        char[] chars = msg.toCharArray();

        for (int i = 0; i < chars.length - 1; i++) {
            if (chars[i] == '&' && ANSI.containsKey(chars[i + 1])) {
                out.append(ANSI.get(chars[i + 1]));
                i++;
            } else {
                out.append(chars[i]);
            }
        }
        return out + "\u001B[0m";
    }
}
