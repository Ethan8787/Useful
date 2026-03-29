package dev.ethan.useful.managers;

import dev.ethan.useful.Main;
import dev.ethan.useful.models.PlayerData;
import dev.ethan.useful.utils.PlayTimeUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.text.DecimalFormat;

public class PlaceHolderManager extends PlaceholderExpansion {
    private final JavaPlugin plugin;
    private final DecimalFormat df = new DecimalFormat("#.##");

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
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) return "";

        switch (params.toLowerCase()) {

            case "kills" -> {
                PlayerData data = Main.getInstance().getDataManager().getPlayerData(player.getUniqueId());
                return String.valueOf(data.kills);
            }

            case "deaths" -> {
                PlayerData data = Main.getInstance().getDataManager().getPlayerData(player.getUniqueId());
                return String.valueOf(data.deaths);
            }

            case "balance" -> {
                Economy econ = EconomyManager.getEconomy();
                if (econ == null) return "0";
                double bal = econ.getBalance(player);
                return formatShortValue(bal);
            }

            case "playtime" -> {
                PlayerData data = Main.getInstance().getDataManager().getPlayerData(player.getUniqueId());
                return PlayTimeUtil.formatPlayTime(data.playTimeSeconds);
            }

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
                Player p = player.getPlayer();
                if (p == null) return "0.0";
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
                return formatGradient(String.format("%.1f", tps), from, to);
            }

            case "ping" -> {
                Player p = player.getPlayer();
                if (p == null) return "0";
                int ping = p.getPing();
                Color from, to;

                if (ping <= 50) {
                    from = Color.decode("#55FF55");
                    to = Color.decode("#00AA00");
                } else if (ping <= 150) {
                    from = Color.decode("#FFFF55");
                    to = Color.decode("#FFAA00");
                } else {
                    from = Color.decode("#FF5555");
                    to = Color.decode("#AA0000");
                }
                return formatGradient(String.valueOf(ping), from, to);
            }
        }
        return null;
    }

    private String formatShortValue(double amount) {
        if (amount < 1000) return String.valueOf((int) amount);
        if (amount < 1000000) return df.format(amount / 1000.0) + "K";
        if (amount < 1000000000) return df.format(amount / 1000000.0) + "M";
        if (amount < 1000000000000L) return df.format(amount / 1000000000.0) + "B";
        return df.format(amount / 1000000000000.0) + "T";
    }

    private String formatGradient(String text, String startHex, String endHex) {
        return formatGradient(text, Color.decode(startHex), Color.decode(endHex));
    }

    private String formatGradient(String text, Color start, Color end) {
        StringBuilder sb = new StringBuilder();
        int length = text.length();
        if (length <= 1) return "&#" + String.format("%02X%02X%02X", start.getRed(), start.getGreen(), start.getBlue()) + text;

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