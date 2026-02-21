package dev.ethan.useful.utils;

import dev.ethan.useful.constants.Messages;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PlayerBlockingUtil {

    private final JavaPlugin plugin;
    private final LuckPermsUtil luckPermsUtil;
    private final Map<UUID, Set<UUID>> blocks = new HashMap<>();
    private final File file;
    private YamlConfiguration config;

    public PlayerBlockingUtil(JavaPlugin plugin, LuckPermsUtil luckPermsUtil) {
        this.plugin = plugin;
        this.luckPermsUtil = luckPermsUtil;
        this.file = new File(plugin.getDataFolder(), "blocks.yml");
        ensureFile();
        load();
    }

    public boolean isBlocked(UUID receiver, UUID sender) {
        Set<UUID> set = blocks.get(receiver);
        return set != null && set.contains(sender);
    }

    public boolean block(UUID blocker, UUID target) {
        if (blocker.equals(target)) return false;
        Set<UUID> set = blocks.computeIfAbsent(blocker, k -> new HashSet<>());
        boolean added = set.add(target);
        if (added) save();
        return added;
    }

    public boolean unblock(UUID blocker, UUID target) {
        Set<UUID> set = blocks.get(blocker);
        if (set == null) return false;

        boolean removed = set.remove(target);
        if (removed) {
            if (set.isEmpty()) blocks.remove(blocker);
            save();
        }
        return removed;
    }

    public Set<UUID> getBlocked(UUID blocker) {
        Set<UUID> set = blocks.get(blocker);
        if (set == null) return Collections.emptySet();
        return Collections.unmodifiableSet(set);
    }

    public void sendBlock(Player blocker, String[] args) {
        if (args.length < 1) {
            blocker.sendMessage(Messages.PREFIX + "§c用法: /block <玩家>");
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

        if (target.getUniqueId().equals(blocker.getUniqueId())) {
            blocker.sendMessage(Messages.PREFIX + "§f不能封鎖自己");
            return;
        }

        boolean added = block(blocker.getUniqueId(), target.getUniqueId());

        if (!added) {
            blocker.sendMessage(Messages.PREFIX + "§f你已封鎖過 " + prefixName(target));
            return;
        }

        blocker.sendMessage(Messages.PREFIX + "§f已封鎖 " + prefixName(target) + " §f的傳送與私訊請求");
    }

    public void sendUnblock(Player blocker, String[] args) {
        if (args.length < 1) {
            blocker.sendMessage(Messages.PREFIX + "§c用法: /unblock <玩家>");
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

        boolean removed = unblock(blocker.getUniqueId(), target.getUniqueId());

        if (!removed) {
            blocker.sendMessage(Messages.PREFIX + "§f你並未封鎖 " + prefixName(target));
            return;
        }

        blocker.sendMessage(Messages.PREFIX + "§f已解除封鎖 " + prefixName(target));
    }

    public void sendBlockList(Player viewer) {
        Set<UUID> set = blocks.getOrDefault(viewer.getUniqueId(), Collections.emptySet());

        if (set.isEmpty()) {
            viewer.sendMessage(Messages.PREFIX + "§f你沒有封鎖任何人");
            return;
        }

        StringBuilder sb = new StringBuilder();

        for (UUID u : set) {
            OfflinePlayer p = Bukkit.getOfflinePlayer(u);
            String name = p.getName() != null ? prefixName(p) : "§7(未知玩家)";
            if (!sb.isEmpty()) sb.append("§7, §f");
            sb.append(name);
        }

        viewer.sendMessage(Messages.PREFIX + "§f已封鎖: " + sb);
    }

    public void reload() {
        load();
    }

    public void save() {
        try {
            if (config == null) config = YamlConfiguration.loadConfiguration(file);

            for (String key : config.getKeys(false)) {
                config.set(key, null);
            }

            for (Map.Entry<UUID, Set<UUID>> entry : blocks.entrySet()) {
                List<String> list = new ArrayList<>();
                for (UUID u : entry.getValue()) list.add(u.toString());
                config.set(entry.getKey().toString(), list);
            }

            config.save(file);
        } catch (Exception ex) {
            plugin.getLogger().warning("Failed to save block data: " + ex.getMessage());
        }
    }

    private void load() {
        blocks.clear();
        config = YamlConfiguration.loadConfiguration(file);

        for (String blockerStr : config.getKeys(false)) {
            try {
                UUID blocker = UUID.fromString(blockerStr);
                List<String> blockedList = config.getStringList(blockerStr);

                Set<UUID> blocked = new HashSet<>();
                for (String s : blockedList) {
                    try {
                        blocked.add(UUID.fromString(s));
                    } catch (IllegalArgumentException ignored) {}
                }

                if (!blocked.isEmpty()) blocks.put(blocker, blocked);

            } catch (IllegalArgumentException ignored) {}
        }
    }

    private void ensureFile() {
        if (!file.exists()) {
            try {
                if (plugin.getDataFolder() != null) plugin.getDataFolder().mkdirs();
                file.createNewFile();
            } catch (IOException ignored) {}
        }
    }

    private String prefixName(OfflinePlayer player) {
        String name = player.getName() == null ? "§7(未知玩家)" : player.getName();
        return luckPermsUtil.getPlayerPrefix(player) + name;
    }
}