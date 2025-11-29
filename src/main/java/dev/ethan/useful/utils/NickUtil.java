package dev.ethan.useful.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class NickUtil {
    private final JavaPlugin plugin;
    private final File nickFile;
    private FileConfiguration config;
    private final Map<UUID, String> nicknameCache = new ConcurrentHashMap<>();
    private final Object lock = new Object();

    public NickUtil(JavaPlugin plugin) {
        this.plugin = plugin;
        this.nickFile = new File(plugin.getDataFolder(), "nicknames.yml");
        if (!nickFile.exists()) {
            try {
                nickFile.getParentFile().mkdirs();
                nickFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create nicknames.yml");
                e.printStackTrace();
            }
        }
        reload();
    }

    public void reload() {
        synchronized (lock) {
            this.config = YamlConfiguration.loadConfiguration(nickFile);
            nicknameCache.clear();
            if (config.getConfigurationSection("nicks") != null) {
                for (String uuidStr : config.getConfigurationSection("nicks").getKeys(false)) {
                    String nick = config.getString("nicks." + uuidStr + ".nickname");
                    if (nick != null) {
                        nicknameCache.put(UUID.fromString(uuidStr), nick);
                    }
                }
            }
        }
        plugin.getLogger().info("Nicknames loaded: " + nicknameCache.size());
    }

    private void saveAsync() {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            synchronized (lock) {
                try {
                    config.save(nickFile);
                } catch (IOException e) {
                    plugin.getLogger().severe("Failed to save nicknames.yml");
                    e.printStackTrace();
                }
            }
        });
    }

    public void setNickname(Player player, String nickname) {
        UUID uuid = player.getUniqueId();
        nicknameCache.put(uuid, nickname);
        synchronized (lock) {
            config.set("nicks." + uuid + ".nickname", nickname);
            config.set("nicks." + uuid + ".last_update", System.currentTimeMillis());
        }
        saveAsync();
    }

    public void removeNickname(Player player) {
        UUID uuid = player.getUniqueId();
        nicknameCache.remove(uuid);
        synchronized (lock) {
            config.set("nicks." + uuid, null);
        }
        saveAsync();
    }

    public String getNickname(Player player) {
        return nicknameCache.get(player.getUniqueId());
    }

    public String getNicknameOrDefault(Player player) {
        return getNickname(player) != null ? getNickname(player) : player.getName();
    }

    public void close() {
        synchronized (lock) {
            try {
                config.save(nickFile);
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to save nicknames.yml on shutdown");
                e.printStackTrace();
            }
        }
    }
}
