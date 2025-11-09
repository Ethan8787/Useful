package dev.ethan.useful.utils;

import dev.ethan.useful.Main;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

import static dev.ethan.useful.Main.ipsConfig;
import static dev.ethan.useful.Main.ipsFile;

public class IPTrackerUtils {

    public static File getPluginDataFolder() {
        return Main.getInstance().getDataFolder();
    }

    public static void savePluginResource(String resourcePath, boolean replace) {
        Main.getInstance().saveResource(resourcePath, replace);
    }

    public static void createIpsFile() {
        ipsFile = new File(getPluginDataFolder(), "ips.yml");
        if (!ipsFile.exists()) {
            ipsFile.getParentFile().mkdirs();
            savePluginResource("ips.yml", false);
        }

        ipsConfig = YamlConfiguration.loadConfiguration(ipsFile);
    }

    public static void saveIpsFile() {
        try {
            ipsConfig.save(ipsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
