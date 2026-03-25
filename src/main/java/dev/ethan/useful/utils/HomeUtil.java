package dev.ethan.useful.utils;

import dev.ethan.useful.Main;
import dev.ethan.useful.constants.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import static net.kyori.adventure.text.Component.text;

public class HomeUtil {
    private final JavaPlugin plugin;
    private final TeleportUtil teleportUtil = Main.getInstance().getTeleportUtil();
    private final File homesFile;
    private final FileConfiguration homesConfig;

    public HomeUtil(JavaPlugin plugin) {
        this.plugin = plugin;
        this.homesFile = new File(plugin.getDataFolder(), "homes.yml");
        if (!homesFile.exists()) {
            try {
                homesFile.getParentFile().mkdirs();
                homesFile.createNewFile();
            } catch (IOException ignored) {
            }
        }
        this.homesConfig = YamlConfiguration.loadConfiguration(homesFile);
    }

    private void saveConfig() {
        try {
            homesConfig.save(homesFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save homes.yml!");
            e.printStackTrace();
        }
    }

    private int getMaxHomes(Player p) {
        if (p.hasPermission("useful.vipplus")) return 25;
        if (p.hasPermission("useful.vip")) return 15;
        return 5;
    }

    public Set<String> getHomeNames(UUID uuid) {
        if (!homesConfig.contains(uuid.toString())) return Set.of();
        var section = homesConfig.getConfigurationSection(uuid.toString());
        return section != null ? section.getKeys(false) : Set.of();
    }

    public void handleSetHome(Player player, String[] args) {
        if (args.length != 1) {
            player.sendMessage(Messages.PREFIX + "§c用法: /sethome <名稱>");
            return;
        }
        String homeName = args[0].toLowerCase();
        UUID uuid = player.getUniqueId();
        int max = getMaxHomes(player);
        Set<String> homes = homesConfig.contains(uuid.toString()) ? homesConfig.getConfigurationSection(uuid.toString()).getKeys(false) : Set.of();
        if (!homesConfig.contains(uuid + "." + homeName) && homes.size() >= max) {
            player.sendMessage(Messages.PREFIX + "§c你已達到最大家數上限 (" + max + ")");
            return;
        }
        Location loc = player.getLocation();
        String path = uuid + "." + homeName;
        homesConfig.set(path + ".world", loc.getWorld().getName());
        homesConfig.set(path + ".x", loc.getX());
        homesConfig.set(path + ".y", loc.getY());
        homesConfig.set(path + ".z", loc.getZ());
        saveConfig();
        player.sendMessage(Messages.PREFIX + "§a成功設置家: §f" + homeName);
    }

    public void handleHomes(Player player) {
        UUID uuid = player.getUniqueId();
        if (!homesConfig.contains(uuid.toString())) {
            player.sendMessage(Messages.PREFIX + "§f你還沒有設置任何家");
            return;
        }
        Set<String> homes = homesConfig.getConfigurationSection(uuid.toString()).getKeys(false);
        if (homes.isEmpty()) {
            player.sendMessage(Messages.PREFIX + "§f你還沒有設置任何家");
            return;
        }
        Component msg = text(Messages.PREFIX + "§f你的家:\n");
        int index = 0;
        for (String home : homes) {
            String base = uuid + "." + home;
            Location loc = new Location(Bukkit.getWorld(homesConfig.getString(base + ".world")), homesConfig.getDouble(base + ".x"), homesConfig.getDouble(base + ".y"), homesConfig.getDouble(base + ".z"));
            msg = msg
                    .append(text(home, NamedTextColor.AQUA))
                    .append(text(" (X:" + loc.getBlockX() + " Y:" + loc.getBlockY() + " Z:" + loc.getBlockZ() + ")", NamedTextColor.YELLOW))
                    .append(text(" [傳送]", NamedTextColor.GREEN)
                            .hoverEvent(HoverEvent.showText(text("點擊傳送", NamedTextColor.GREEN)))
                            .clickEvent(ClickEvent.runCommand("/home " + home)))
                    .append(text(" [複製座標]", NamedTextColor.GOLD)
                            .hoverEvent(HoverEvent.showText(text("複製座標", NamedTextColor.YELLOW)))
                            .clickEvent(ClickEvent.copyToClipboard(loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ())));

            index++;
            if (index < homes.size()) msg = msg.append(text("\n"));
        }
        player.sendMessage(msg);
    }

    public void handleHome(Player player, String[] args) {
        if (args.length != 1) {
            player.sendMessage(Messages.PREFIX + "§c用法: /home <名稱>");
            return;
        }
        String name = args[0].toLowerCase();
        String path = player.getUniqueId() + "." + name;
        if (!homesConfig.contains(path)) {
            player.sendMessage(Messages.PREFIX + "§c家不存在: " + name);
            return;
        }

        String worldName = homesConfig.getString(path + ".world");
        if (worldName == null || Bukkit.getWorld(worldName) == null) {
            player.sendMessage(Messages.PREFIX + "§c家所在的環境不存在");
            return;
        }

        Location loc = new Location(
                Bukkit.getWorld(worldName),
                homesConfig.getDouble(path + ".x"),
                homesConfig.getDouble(path + ".y"),
                homesConfig.getDouble(path + ".z")
        );

        Component cancelBtn = text(" §7取消")
                .hoverEvent(HoverEvent.showText(text("取消傳送", NamedTextColor.RED)))
                .clickEvent(ClickEvent.runCommand("/tpcancel"));

        player.sendMessage(text(Messages.PREFIX).append(text("準備傳送中...", NamedTextColor.WHITE)).append(cancelBtn));

        teleportUtil.delayedTeleport(player, loc, name);
    }

    public void handleDelHome(Player player, String[] args) {
        if (args.length != 1) {
            player.sendMessage(Messages.PREFIX + "§c用法: /delhome <名稱>");
            return;
        }
        String homeName = args[0].toLowerCase();
        String path = player.getUniqueId() + "." + homeName;
        if (!homesConfig.contains(path)) {
            player.sendMessage(Messages.PREFIX + "§c家不存在: " + homeName);
            return;
        }
        homesConfig.set(path, null);
        saveConfig();
        player.sendMessage(Messages.PREFIX + "§a已刪除家: §f" + homeName);
    }
}
