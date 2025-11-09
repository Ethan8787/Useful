package dev.ethan.useful.handlers;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.awt.*;


public class PlaceHolderHandler extends PlaceholderExpansion {
    public static void init(JavaPlugin plugin) {
        try {
            new PlaceHolderHandler().register();
            Bukkit.getLogger().info("[Useful] PlaceholderAPI hooked successfully.");
        } catch (Exception e) {
            Bukkit.getLogger().severe("[Useful] Failed to register PlaceholderAPI expansion:");
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
        return "5.8.2";
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
                long max = runtime.maxMemory();
                long total = runtime.totalMemory();
                long free = runtime.freeMemory();
                long used = total - free;
                double usedGB = used / 1024.0 / 1024 / 1024.0;
                double maxGB = max / 1024.0 / 1024 / 1024.0;
                String usedStr = String.format("%.1fG", usedGB);
                String maxStr = String.format("%.1fG", maxGB);
                String usedColored = hexGradient(usedStr, Color.decode("#55FF55"), Color.decode("#00AA00"));
                String maxColored = hexGradient(maxStr, Color.decode("#55FF55"), Color.decode("#00AA00"));

                return usedColored + "§7/§r" + maxColored;
            }

            case "tps" -> {
                double tps = Bukkit.getServer().getTPS()[0];
                String tpsStr = String.format("%.2f", tps);
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
                String tpsColored = hexGradient(tpsStr, from, to);
                return tpsColored + "§7/" + hexGradient("20.00", Color.decode("#55FF55"), Color.decode("#00AA00"));
            }

            case "rank" -> {
                LuckPerms luckPerms = LuckPermsProvider.get();
                User u = luckPerms.getUserManager().getUser(p.getUniqueId());
                if (u == null) return "&7[Default]";
                String primaryGroup = u.getPrimaryGroup();
                if ("default".equalsIgnoreCase(primaryGroup)) {
                    return "&a[Default]";
                }
                Group g = luckPerms.getGroupManager().getGroup(primaryGroup);
                if (g == null) return "&7[Default]";
                String prefix = g.getCachedData().getMetaData().getPrefix();
                if (prefix == null || prefix.isEmpty()) {
                    prefix = primaryGroup;
                }
                return prefix;
            }

            default -> {
                return null;
            }
        }
    }

    private String hexGradient(String text, Color start, Color end) {
        StringBuilder sb = new StringBuilder();
        int length = text.length();
        for (int i = 0; i < length; i++) {
            float ratio = (float) i / (length - 1);
            int red = (int) (start.getRed() + ratio * (end.getRed() - start.getRed()));
            int green = (int) (start.getGreen() + ratio * (end.getGreen() - start.getGreen()));
            int blue = (int) (start.getBlue() + ratio * (end.getBlue() - start.getBlue()));

            sb.append(String.format("&#%02X%02X%02X", red, green, blue))
                    .append(text.charAt(i));
        }
        return sb.toString();
    }
}

