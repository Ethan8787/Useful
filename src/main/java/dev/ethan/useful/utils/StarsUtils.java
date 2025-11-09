package dev.ethan.useful.utils;

public class StarsUtils {

    public static String getColoredStar(int level) {
        String symbol;
        String color;
        if (level >= 2000) {
            return getRainbowStars("[" + level + "✪] ");
        } else if (level >= 1900) {
            symbol = "✪";
            color = "§8";
            return "§7[" + "§5" + level + color + symbol + "§7] ";
        } else if (level >= 1800) {
            symbol = "✪";
            color = "§1";
            return "§7[" + "§9" + level + color + symbol + "§7] ";
        } else if (level >= 1700) {
            symbol = "✪";
            color = "§5";
            return "§7[" + "§d" + level + color + symbol + "§7] ";
        } else if (level >= 1600) {
            symbol = "✪";
            color = "§4";
            return "§7[" + "§c" + level + color + symbol + "§7] ";
        } else if (level >= 1500) {
            symbol = "✪";
            color = "§9";
            return "§7[" + "§3" + level + color + symbol + "§7] ";
        } else if (level >= 1400) {
            symbol = "✪";
            color = "§2";
            return "§7[" + "§a" + level + color + symbol + "§7] ";
        } else if (level >= 1300) {
            symbol = "✪";
            color = "§3";
            return "§7[" + "§b" + level + color + symbol + "§7] ";
        } else if (level >= 1200) {
            symbol = "✪";
            color = "§6";
            return "§7[" + "§e" + level + color + symbol + "§7] ";
        } else if (level >= 1100) {
            symbol = "✪";
            color = "§7";
            return color + "[" + "§f" + level + color + symbol + "] ";
        } else if (level >= 1000) {
            return getRainbowStars("[" + level + "✫] ");
        } else if (level >= 900) {
            symbol = "✫";
            color = "§5";
        } else if (level >= 800) {
            symbol = "✫";
            color = "§9";
        } else if (level >= 700) {
            symbol = "✫";
            color = "§d";
        } else if (level >= 600) {
            symbol = "✫";
            color = "§4";
        } else if (level >= 500) {
            symbol = "✫";
            color = "§3";
        } else if (level >= 400) {
            symbol = "✫";
            color = "§2";
        } else if (level >= 300) {
            symbol = "✫";
            color = "§b";
        } else if (level >= 200) {
            symbol = "✫";
            color = "§6";
        } else if (level >= 100) {
            symbol = "✫";
            color = "§f";
        } else {
            symbol = "✫";
            color = "§7";
        }

        return color + "[" + level + symbol + "] ";
    }

    public static String getRainbowStars(String text) {
        String[] colors = {
                "§c",
                "§6",
                "§e",
                "§a",
                "§b",
                "§9",
                "§d"
        };

        StringBuilder sb = new StringBuilder();
        char[] chars = text.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            sb.append(colors[i % colors.length]).append(chars[i]);
        }

        return sb.toString();
    }
}
