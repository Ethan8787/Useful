package dev.ethan.useful.utils;

import dev.ethan.useful.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.UUID;

import static dev.ethan.useful.Main.Plugin_Prefix;
import static dev.ethan.useful.Main.dmListeners;
import static dev.ethan.useful.utils.LuckPermsUtil.getPlayerPrefix;
import static dev.ethan.useful.utils.TeleportUtil.isBlocked;

public class MessageUtil {
    private static File file;
    public static FileConfiguration config;

    public static void init(JavaPlugin plugin) {
        file = new File(plugin.getDataFolder(), "dmListeners.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public static void addDmListener(Player p) {
        UUID uuid = p.getUniqueId();
        dmListeners.add(uuid);
        config.set("listeners." + uuid, true);
        saveConfig();
        p.sendMessage(Plugin_Prefix + "§a您現在正在監聽私訊。");
    }

    public static void removeDmListener(Player p) {
        UUID uuid = p.getUniqueId();
        dmListeners.remove(uuid);
        config.set("listeners." + uuid, null);
        saveConfig();
        p.sendMessage(Plugin_Prefix + "§c您已停止監聽私訊。");
    }

    public static void sendActionBar(Player p, String msg) {
        p.sendActionBar(Component.text(msg));
    }

    private static void saveConfig() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean isValidUrl(String url) {
        try {
            URI uri = new URI(url);
            return uri.getScheme() != null && uri.getHost() != null;
        } catch (Exception e) {
            return false;
        }
    }

    public static void sendMessage(Player s, Player r, String msg) {
        if (isBlocked(r.getUniqueId(), s.getUniqueId())) {
            s.sendMessage(Plugin_Prefix + "§c你無法發送私訊給 " + getPlayerPrefix(r) + r.getDisplayName() + "，對方已封鎖你");
            return;
        }
        String senderPrefix = getPlayerPrefix(s);
        String receiverPrefix = getPlayerPrefix(r);
        Component fromPrefix = Component.text("From: ", NamedTextColor.LIGHT_PURPLE)
                .clickEvent(ClickEvent.suggestCommand("/r "))
                .hoverEvent(HoverEvent.showText(Component.text("點擊此處回覆", NamedTextColor.LIGHT_PURPLE)));
        Component fromName = Component.text(formatName(senderPrefix, s.getDisplayName()), NamedTextColor.GRAY);
        Component fromMessage = Component.text(": ", NamedTextColor.WHITE);
        Component clickableMessage = Component.text(msg, NamedTextColor.WHITE);
        if (isValidUrl(msg)) {
            clickableMessage = clickableMessage
                    .clickEvent(ClickEvent.openUrl(msg))
                    .hoverEvent(HoverEvent.showText(Component.text("點擊此處打開連結", NamedTextColor.LIGHT_PURPLE)));
        }
        Component completeFromMessage = fromPrefix.append(fromName).append(fromMessage).append(clickableMessage);
        Component toPrefix = Component.text("To: ", NamedTextColor.LIGHT_PURPLE)
                .clickEvent(ClickEvent.suggestCommand("/r "))
                .hoverEvent(HoverEvent.showText(Component.text("點擊此處回覆", NamedTextColor.LIGHT_PURPLE)));
        Component toName = Component.text(formatName(receiverPrefix, r.getDisplayName()), NamedTextColor.GRAY);
        Component toMessage = Component.text(": ", NamedTextColor.WHITE);
        Component clickableToMessage = Component.text(msg, NamedTextColor.WHITE);
        if (isValidUrl(msg)) {
            clickableToMessage = clickableToMessage
                    .clickEvent(ClickEvent.openUrl(msg))
                    .hoverEvent(HoverEvent.showText(Component.text("點擊此處打開連結", NamedTextColor.LIGHT_PURPLE)));
        }
        Component completeToMessage = toPrefix.append(toName).append(toMessage).append(clickableToMessage);
        r.sendMessage(completeFromMessage);
        s.sendMessage(completeToMessage);
        for (UUID uuid : dmListeners) {
            Player l = Bukkit.getPlayer(uuid);
            if (l != null && l.isOnline()) {
                l.sendMessage(
                        getPlayerPrefix(s) + s.getDisplayName()
                                + " §f→ " + getPlayerPrefix(r) + r.getDisplayName()
                                + " §8» §r" + msg
                );
            }
        }
        Main.lastMessaged.put(s.getUniqueId(), r.getUniqueId());
        Main.lastMessaged.put(r.getUniqueId(), s.getUniqueId());
    }

    public static String formatName(String prefix, String name) {
        return prefix.isEmpty() ? name : prefix + name;
    }

    public static Player getLastMessaged(Player p) {
        UUID targetUUID = Main.lastMessaged.get(p.getUniqueId());
        return targetUUID != null ? Bukkit.getPlayer(targetUUID) : null;
    }

    public static boolean isDmListenerActive(Player p) {
        return dmListeners.contains(p.getUniqueId());
    }
}
