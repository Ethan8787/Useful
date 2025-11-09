package dev.ethan.useful.handlers;

import dev.ethan.useful.Main;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigHandler {
    public ConfigHandler(JavaPlugin plugin) {
        plugin.saveDefaultConfig();
    }

    public String getHost() {
        return Main.getInstance().getConfig().getString("mysql.host", "localhost");
    }

    public int getPort() {
        return Main.getInstance().getConfig().getInt("mysql.port", 3306);
    }

    public String getDatabase() {
        return Main.getInstance().getConfig().getString("mysql.database", "minecraft");
    }

    public String getUsername() {
        return Main.getInstance().getConfig().getString("mysql.username", "root");
    }

    public String getPassword() {
        return Main.getInstance().getConfig().getString("mysql.password", "");
    }

    public int getPoolSize() {
        return Main.getInstance().getConfig().getInt("mysql.pool-size", 10);
    }
}

