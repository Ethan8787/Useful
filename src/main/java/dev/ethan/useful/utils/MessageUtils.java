package dev.ethan.useful.utils;

import dev.ethan.useful.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
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
import static dev.ethan.useful.utils.LuckPermsUtils.getPlayerPrefix;
import static dev.ethan.useful.utils.TeleportUtils.isBlocked;

public class MessageUtils {
    private static File file;
    public static FileConfiguration config;

    public static void addDmListener(Player player) {
        UUID uuid = player.getUniqueId();
        dmListeners.add(uuid);
        config.set("listeners." + uuid, true);
        saveConfig();
        player.sendMessage(Plugin_Prefix + "§a您現在正在監聽私訊。");
    }

    public static void removeDmListener(Player player) {
        UUID uuid = player.getUniqueId();
        dmListeners.remove(uuid);
        config.set("listeners." + uuid, null);
        saveConfig();
        player.sendMessage(Plugin_Prefix + "§c您已停止監聽私訊。");
    }

    public static void sendActionBar(Player player, String message) {
        player.sendActionBar(Component.text(message));
    }

    private static void saveConfig() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setupDmListenerFile(JavaPlugin plugin) {
        file = new File(plugin.getDataFolder(), "dmListeners.yml");
        if (!file.exists()) {
            try { file.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    private static boolean isValidUrl(String url) {
        try {
            URI uri = new URI(url);
            return uri.getScheme() != null && uri.getHost() != null;
        } catch (Exception e) {
            return false;
        }
    }

    public static void sendMessage(Player sender, Player receiver, String message) {
        // ====== 檢查封鎖 ======
        if (isBlocked(receiver.getUniqueId(), sender.getUniqueId())) {
            sender.sendMessage(Plugin_Prefix + "§c你無法發送私訊給 " + getPlayerPrefix(receiver) + receiver.getDisplayName() + "，對方已封鎖你");
            return;
        }

        String senderPrefix = getPlayerPrefix(sender);
        String receiverPrefix = getPlayerPrefix(receiver);

        // ====== 給接收者看的私訊 ======
        Component fromPrefix = Component.text("From: ", NamedTextColor.LIGHT_PURPLE)
                .clickEvent(ClickEvent.suggestCommand("/r "))
                .hoverEvent(HoverEvent.showText(Component.text("點擊此處回覆", NamedTextColor.LIGHT_PURPLE)));
        Component fromName = Component.text(formatName(senderPrefix, sender.getDisplayName()), NamedTextColor.GRAY);
        Component fromMessage = Component.text(": ", NamedTextColor.WHITE);

        Component clickableMessage = Component.text(message, NamedTextColor.WHITE);
        if (isValidUrl(message)) {
            clickableMessage = clickableMessage
                    .clickEvent(ClickEvent.openUrl(message))
                    .hoverEvent(HoverEvent.showText(Component.text("點擊此處打開連結", NamedTextColor.LIGHT_PURPLE)));
        }

        Component completeFromMessage = fromPrefix.append(fromName).append(fromMessage).append(clickableMessage);

        // ====== 給發送者看的私訊 ======
        Component toPrefix = Component.text("To: ", NamedTextColor.LIGHT_PURPLE)
                .clickEvent(ClickEvent.suggestCommand("/r "))
                .hoverEvent(HoverEvent.showText(Component.text("點擊此處回覆", NamedTextColor.LIGHT_PURPLE)));
        Component toName = Component.text(formatName(receiverPrefix, receiver.getDisplayName()), NamedTextColor.GRAY);
        Component toMessage = Component.text(": ", NamedTextColor.WHITE);

        Component clickableToMessage = Component.text(message, NamedTextColor.WHITE);
        if (isValidUrl(message)) {
            clickableToMessage = clickableToMessage
                    .clickEvent(ClickEvent.openUrl(message))
                    .hoverEvent(HoverEvent.showText(Component.text("點擊此處打開連結", NamedTextColor.LIGHT_PURPLE)));
        }

        Component completeToMessage = toPrefix.append(toName).append(toMessage).append(clickableToMessage);

        receiver.sendMessage(completeFromMessage);
        sender.sendMessage(completeToMessage);

        // ====== 發送給監聽者 ======
        for (UUID listenerUUID : dmListeners) {
            Player listener = Bukkit.getPlayer(listenerUUID);
            if (listener != null && listener.isOnline()) {
                listener.sendMessage(
                        getPlayerPrefix(sender) + sender.getDisplayName()
                                + " §f→ " + getPlayerPrefix(receiver) + receiver.getDisplayName()
                                + " §8» §r" + message
                );
            }
        }

        Main.lastMessaged.put(sender.getUniqueId(), receiver.getUniqueId());
        Main.lastMessaged.put(receiver.getUniqueId(), sender.getUniqueId());
    }

    public static String formatName(String prefix, String displayName) {
        return prefix.isEmpty() ? displayName : prefix + displayName;
    }

    public static Player getLastMessaged(Player player) {
        UUID targetUUID = Main.lastMessaged.get(player.getUniqueId());
        return targetUUID != null ? Bukkit.getPlayer(targetUUID) : null;
    }

    public static boolean isDmListenerActive(Player player) {
        return dmListeners.contains(player.getUniqueId());
    }
}
