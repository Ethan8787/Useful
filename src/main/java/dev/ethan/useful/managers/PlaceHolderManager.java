package dev.ethan.useful.managers;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class PlaceHolderManager extends PlaceholderExpansion {
    private final JavaPlugin plugin;

    public PlaceHolderManager(JavaPlugin plugin) {
        this.plugin = plugin;
        try {
            this.register();
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to register PlaceholderAPI expansion:");
            e.printStackTrace();
        }
    }

    @Override
    public @NotNull String getIdentifier() {
        return "useful";
    }

    @Override
    public @NotNull String getAuthor() {
        return "ethan";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player p, @NotNull String params) {
        switch (params.toLowerCase()) {

            case "ram" -> {
                Runtime runtime = Runtime.getRuntime();
                long used = runtime.totalMemory() - runtime.freeMemory();
                double usedGB = used / 1024.0 / 1024 / 1024.0;
                double maxGB = runtime.maxMemory() / 1024.0 / 1024 / 1024.0;

                return formatGradient(String.format("%.1fG", usedGB), "#55FF55", "#00AA00")
                        + "§7/§r"
                        + formatGradient(String.format("%.1fG", maxGB), "#55FF55", "#00AA00");
            }

            case "real_health" -> {
                double currentHealth = p.getHealth();
                double absorption = p.getAbsorptionAmount();
                double total = currentHealth + absorption;
                return String.format("%.1f", total);
            }

            case "tps" -> {
                double tps = Bukkit.getServer().getTPS()[0];
                Color from, to;

                if (tps >= 19.5) {
                    from = Color.decode("#55FF55");
                    to = Color.decode("#00AA00");
                } else if (tps >= 15.0) {
                    from = Color.decode("#FFFF55");
                    to = Color.decode("#FFAA00");
                } else {
                    from = Color.decode("#FF5555");
                    to = Color.decode("#AA0000");
                }

                return formatGradient(String.format("%.2f", tps), from, to) +
                        "§7/" +
                        formatGradient("20.00", "#55FF55", "#00AA00");
            }

            default -> {
                return null;
            }
        }
    }

    private String formatGradient(String text, String startHex, String endHex) {
        return formatGradient(text, Color.decode(startHex), Color.decode(endHex));
    }

    private String formatGradient(String text, Color start, Color end) {
        StringBuilder sb = new StringBuilder();
        int length = text.length();

        for (int i = 0; i < length; i++) {
            float ratio = (float) i / (length - 1);
            int r = (int) (start.getRed() + ratio * (end.getRed() - start.getRed()));
            int g = (int) (start.getGreen() + ratio * (end.getGreen() - start.getGreen()));
            int b = (int) (start.getBlue() + ratio * (end.getBlue() - start.getBlue()));

            sb.append(String.format("&#%02X%02X%02X", r, g, b)).append(text.charAt(i));
        }
        return sb.toString();
    }
}
