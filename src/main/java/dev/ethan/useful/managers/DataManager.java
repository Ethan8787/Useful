package dev.ethan.useful.managers;

import dev.ethan.useful.models.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DataManager {

    private final JavaPlugin plugin;
    private final File file;
    private final FileConfiguration config;
    private final Map<UUID, PlayerData> cache = new HashMap<>();

    public DataManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "userdata.yml");
        if (!file.exists()) {
            plugin.getDataFolder().mkdirs();
            try { file.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
        this.config = YamlConfiguration.loadConfiguration(file);

        startAutoSave();
    }

    public PlayerData getPlayerData(UUID uuid) {
        return cache.computeIfAbsent(uuid, k -> {
            PlayerData data = new PlayerData();
            data.kills = config.getInt(uuid + ".kills", 0);
            data.deaths = config.getInt(uuid + ".deaths", 0);
            data.playTimeSeconds = config.getLong(uuid + ".playtime", 0);
            return data;
        });
    }

    private void startAutoSave() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::saveAll, 6000L, 6000L);
    }

    public synchronized void saveAll() {
        if (cache.isEmpty()) return;

        for (Map.Entry<UUID, PlayerData> entry : cache.entrySet()) {
            updateConfig(entry.getKey(), entry.getValue());
        }
        saveToFile();
    }

    public void unloadPlayer(UUID uuid) {
        PlayerData data = cache.remove(uuid);
        if (data != null) {
            updateConfig(uuid, data);
            saveToFile();
        }
    }

    private void updateConfig(UUID uuid, PlayerData data) {
        config.set(uuid + ".kills", data.kills);
        config.set(uuid + ".deaths", data.deaths);
        config.set(uuid + ".playtime", data.playTimeSeconds);
    }

    private synchronized void saveToFile() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}