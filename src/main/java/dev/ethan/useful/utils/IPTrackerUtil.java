package dev.ethan.useful.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class IPTrackerUtil {
    private final JavaPlugin plugin;
    public final File ipsFile;
    public final FileConfiguration ipsConfig;

    public IPTrackerUtil(JavaPlugin plugin) {
        this.plugin = plugin;
        this.ipsFile = new File(plugin.getDataFolder(), "ips.yml");
        if (!ipsFile.exists()) {
            try {
                ipsFile.getParentFile().mkdirs();
                ipsFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to create ips.yml!");
                e.printStackTrace();
            }
        }
        this.ipsConfig = YamlConfiguration.loadConfiguration(ipsFile);
    }

    public void save() {
        try {
            ipsConfig.save(ipsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save ips.yml!");
            e.printStackTrace();
        }
    }
}
