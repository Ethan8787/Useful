package dev.ethan.useful.utils;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

import static dev.ethan.useful.Main.ipsConfig;
import static dev.ethan.useful.Main.ipsFile;

public class IPTrackerUtil {
    public static void init(JavaPlugin plugin) {
        ipsFile = new File(plugin.getDataFolder(), "ips.yml");
        if (!ipsFile.exists()) {
            ipsFile.getParentFile().mkdirs();
            plugin.saveResource("ips.yml", false);
        }
        ipsConfig = YamlConfiguration.loadConfiguration(ipsFile);
    }

    public static void save() {
        try {
            ipsConfig.save(ipsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
