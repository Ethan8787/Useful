package dev.ethan.useful.handlers;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.citizensnpcs.api.CitizensAPI;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

import static dev.ethan.useful.utils.PlayerUtils.getStat;
import static dev.ethan.useful.utils.StarsUtils.getColoredStar;

public class PlaceHolderHandlers extends PlaceholderExpansion {

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
    public String onPlaceholderRequest(Player player, @NotNull String params) {
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
                    from = Color.decode("#55FF55"); // §a
                    to = Color.decode("#00AA00");   // §3
                } else if (tps >= 15.0) {
                    from = Color.decode("#FFFF55"); // §e
                    to = Color.decode("#FFAA00");   // §6
                } else {
                    from = Color.decode("#FF5555"); // §c
                    to = Color.decode("#AA0000");   // §4
                }

                String tpsColored = hexGradient(tpsStr, from, to);

                return tpsColored + "§7/" + hexGradient("20.00", Color.decode("#55FF55"), Color.decode("#00AA00"));
            }

            case "level" -> {
                return getColoredStar(getStat(player, "level"));
            }
            case "rank" -> {
                LuckPerms luckPerms = LuckPermsProvider.get();

                User user = luckPerms.getUserManager().getUser(player.getUniqueId());
                if (user == null) return "&7[Default]";

                String primaryGroup = user.getPrimaryGroup();

                if ("default".equalsIgnoreCase(primaryGroup)) {
                    return "&a[Default]";
                }

                Group group = luckPerms.getGroupManager().getGroup(primaryGroup);
                if (group == null) return "&7[Default]";

                String prefix = group.getCachedData().getMetaData().getPrefix();
                if (prefix == null || prefix.isEmpty()) {
                    prefix = primaryGroup;
                }
                return prefix;
            }

            case "calvin" -> {
                int npcCount = 0;
                for (net.citizensnpcs.api.npc.NPC ignored : CitizensAPI.getNPCRegistry()) {
                    npcCount++;
                }

                int playerCount = Bukkit.getOnlinePlayers().size();
                return String.valueOf(npcCount + playerCount);
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

