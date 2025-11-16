package dev.ethan.useful.utils;

import dev.ethan.useful.Main;
import dev.ethan.useful.constants.Messages;
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
import java.util.*;

public class MessageUtil {
    private final JavaPlugin plugin;
    private final LuckPermsUtil luckPerms = Main.getInstance().getLuckPermsUtil();
    private final TeleportUtil teleport = Main.getInstance().getTeleportUtil();

    private File file;
    private FileConfiguration config;

    private final Set<UUID> dmListeners = new HashSet<>();
    private final Map<UUID, UUID> lastMessaged = new HashMap<>();

    public MessageUtil(JavaPlugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    private void loadConfig() {
        file = new File(plugin.getDataFolder(), "dmListeners.yml");
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
        if (config.isConfigurationSection("listeners")) {
            for (String key : config.getConfigurationSection("listeners").getKeys(false)) {
                try {
                    dmListeners.add(UUID.fromString(key));
                } catch (Exception ignored) {}
            }
        }
    }

    private void saveConfig() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addDmListener(Player p) {
        UUID uuid = p.getUniqueId();
        dmListeners.add(uuid);

        config.set("listeners." + uuid, true);
        saveConfig();

        p.sendMessage(Messages.PREFIX + "§a您現在正在監聽私訊。");
    }

    public void removeDmListener(Player p) {
        UUID uuid = p.getUniqueId();
        dmListeners.remove(uuid);

        config.set("listeners." + uuid, null);
        saveConfig();

        p.sendMessage(Messages.PREFIX + "§c您已停止監聽私訊。");
    }

    public void sendMessage(Player sender, Player receiver, String msg) {
        if (teleport.isBlocked(receiver.getUniqueId(), sender.getUniqueId())) {
            sender.sendMessage(Messages.PREFIX + "§c你無法發送私訊給 "
                    + luckPerms.getPlayerPrefix(receiver) + receiver.getName()
                    + "，對方已封鎖你");
            return;
        }
        String senderPrefix = luckPerms.getPlayerPrefix(sender);
        String receiverPrefix = luckPerms.getPlayerPrefix(receiver);
        Component clickable = Component.text(msg, NamedTextColor.WHITE);
        if (isValidUrl(msg)) {
            clickable = clickable
                    .clickEvent(ClickEvent.openUrl(msg))
                    .hoverEvent(HoverEvent.showText(Component.text("點擊開啟連結", NamedTextColor.LIGHT_PURPLE)));
        }
        Component fromMessage = Component.text("From: ", NamedTextColor.LIGHT_PURPLE)
                .append(Component.text(senderPrefix + sender.getName(), NamedTextColor.GRAY))
                .append(Component.text(": ", NamedTextColor.WHITE))
                .append(clickable);
        Component toMessage = Component.text("To: ", NamedTextColor.LIGHT_PURPLE)
                .append(Component.text(receiverPrefix + receiver.getName(), NamedTextColor.GRAY))
                .append(Component.text(": ", NamedTextColor.WHITE))
                .append(clickable);
        receiver.sendMessage(fromMessage);
        sender.sendMessage(toMessage);
        for (UUID uuid : dmListeners) {
            Player listener = Bukkit.getPlayer(uuid);
            if (listener != null && listener.isOnline()) {
                listener.sendMessage(
                        senderPrefix + sender.getName() +
                                " §f→ " +
                                receiverPrefix + receiver.getName() +
                                " §8» §r" + msg
                );
            }
        }
        lastMessaged.put(sender.getUniqueId(), receiver.getUniqueId());
        lastMessaged.put(receiver.getUniqueId(), sender.getUniqueId());
    }

    private boolean isValidUrl(String url) {
        try {
            URI u = new URI(url);
            return u.getScheme() != null && u.getHost() != null;
        } catch (Exception e) {
            return false;
        }
    }

    public Player getLastMessaged(Player p) {
        UUID target = lastMessaged.get(p.getUniqueId());
        return target == null ? null : Bukkit.getPlayer(target);
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public boolean isDmListenerActive(Player p) {
        return dmListeners.contains(p.getUniqueId());
    }
}
