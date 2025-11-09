package dev.ethan.useful.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import static dev.ethan.useful.Main.Plugin_Prefix;
import static net.kyori.adventure.text.Component.text;

public class HomeUtil {
    private static final File homesFile = new File("plugins/Useful", "homes.yml");
    public static FileConfiguration homesConfig = YamlConfiguration.loadConfiguration(homesFile);

    public static void saveConfig() {
        try {
            homesConfig.save(homesFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void handleSetHome(Player p, String[] args) {
        if (args.length != 1) {
            p.sendMessage(Plugin_Prefix + "§c用法: /sethome <名稱>");
            return;
        }
        String homeName = args[0].toLowerCase();
        UUID uuid = p.getUniqueId();
        Set<String> homeNames = homesConfig.contains(uuid.toString())
                && homesConfig.getConfigurationSection(uuid.toString()) != null
                ? homesConfig.getConfigurationSection(uuid.toString()).getKeys(false)
                : null;
        int maxHomes = p.hasPermission("useful.vip") ? 20 : 5;
        if (homeNames != null && homeNames.size() >= maxHomes && !homesConfig.contains(uuid + "." + homeName)) {
            p.sendMessage(Plugin_Prefix + "§c你已達到最大家數上限 (" + maxHomes + " 個)");
            return;
        }
        Location loc = p.getLocation();
        String path = uuid + "." + homeName;
        homesConfig.set(path + ".world", loc.getWorld().getName());
        homesConfig.set(path + ".x", loc.getX());
        homesConfig.set(path + ".y", loc.getY());
        homesConfig.set(path + ".z", loc.getZ());
        saveConfig();
        p.sendMessage(Plugin_Prefix + "§a成功設置家: §f" + homeName + " §7(目前共 §e" + (homeNames == null ? 1 : homeNames.contains(homeName) ? homeNames.size() : homeNames.size() + 1) + "§7/" + maxHomes + " 個)");
    }

    public static void handleHomes(Player p) {
        UUID uuid = p.getUniqueId();
        if (!homesConfig.contains(uuid.toString()) || homesConfig.getConfigurationSection(uuid.toString()) == null) {
            p.sendMessage(Plugin_Prefix + "§f你還沒有設置任何家");
            return;
        }
        Set<String> homeNames = homesConfig.getConfigurationSection(uuid.toString()).getKeys(false);
        if (homeNames.isEmpty()) {
            p.sendMessage(Plugin_Prefix + "§f你還沒有設置任何家");
            return;
        }
        Component msg = text(Plugin_Prefix + "§f你的家:\n");
        int i = 0;
        int total = homeNames.size();
        for (String home : homeNames) {
            String path = uuid + "." + home;
            Location loc = new Location(
                    Bukkit.getWorld(homesConfig.getString(path + ".world")),
                    homesConfig.getDouble(path + ".x"),
                    homesConfig.getDouble(path + ".y"),
                    homesConfig.getDouble(path + ".z")
            );
            Component homeName = text(home, NamedTextColor.AQUA);
            Component coords = text(" (X: " + loc.getBlockX() + " Y: " + loc.getBlockY() + " Z: " + loc.getBlockZ() + ")", NamedTextColor.YELLOW);
            Component teleportBtn = text(" [傳送]", NamedTextColor.GREEN)
                    .hoverEvent(HoverEvent.showText(text("點擊傳送到 " + home, NamedTextColor.GREEN)))
                    .clickEvent(ClickEvent.runCommand("/home " + home));
            Component copyBtn = text(" [複製座標]", NamedTextColor.GOLD)
                    .hoverEvent(HoverEvent.showText(text("點擊複製座標", NamedTextColor.YELLOW)))
                    .clickEvent(ClickEvent.copyToClipboard(loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ()));
            msg = msg.append(homeName)
                    .append(coords)
                    .append(teleportBtn)
                    .append(copyBtn);
            i++;
            if (i < total) msg = msg.append(text("\n"));
        }
        p.sendMessage(msg);
    }

    public static void handleHome(Player p, String[] args) {
        if (args.length != 1) {
            p.sendMessage(Plugin_Prefix + "§c用法: /home <名稱>");
            return;
        }
        String homeName = args[0].toLowerCase();
        UUID uuid = p.getUniqueId();
        String path = uuid + "." + homeName;
        if (!homesConfig.contains(path)) {
            p.sendMessage(Plugin_Prefix + "§c家不存在: " + homeName);
            return;
        }
        Location loc = new Location(
                Bukkit.getWorld(homesConfig.getString(path + ".world")),
                homesConfig.getDouble(path + ".x"),
                homesConfig.getDouble(path + ".y"),
                homesConfig.getDouble(path + ".z")
        );
        p.teleport(loc);
        p.sendMessage(Plugin_Prefix + "§a已傳送到家: §f" + homeName);
    }

    public static void handleDelHome(Player p, String[] args) {
        if (args.length != 1) {
            p.sendMessage(Plugin_Prefix + "§c用法: /delhome <名稱>");
            return;
        }
        String homeName = args[0].toLowerCase();
        UUID uuid = p.getUniqueId();
        String path = uuid + "." + homeName;
        if (!homesConfig.contains(path)) {
            p.sendMessage(Plugin_Prefix + "§c家不存在: " + homeName);
            return;
        }
        homesConfig.set(path, null);
        saveConfig();
        p.sendMessage(Plugin_Prefix + "§a已刪除家: §f" + homeName);
    }
}
