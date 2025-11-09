package dev.ethan.useful.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;

public class NickUtil {
    private static JavaPlugin plugin;
    private static HikariDataSource dataSource;
    private static NickUtil instance;

    private NickUtil() {
    }

    public static NickUtil init(JavaPlugin pl, String host, int port, String database, String username, String password, int poolSize) {
        plugin = pl;
        if (instance != null) {
            plugin.getLogger().warning("NickUtil already initialized.");
            return instance;
        }
        instance = new NickUtil();
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&autoReconnect=true&serverTimezone=UTC");
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(poolSize);
        config.setMinimumIdle(2);
        config.setIdleTimeout(60000);
        config.setMaxLifetime(1800000);
        config.setConnectionTimeout(10000);
        config.setLeakDetectionThreshold(30000);
        dataSource = new HikariDataSource(config);
        instance.createTable();
        plugin.getLogger().info("[NickUtil] Database initialized successfully.");
        return instance;
    }

    private void createTable() {
        try (Connection conn = dataSource.getConnection();
             Statement st = conn.createStatement()) {
            st.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS nicknames (
                            uuid CHAR(36) PRIMARY KEY,
                            nickname VARCHAR(50),
                            last_update TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                        )
                    """);
        } catch (SQLException e) {
            plugin.getLogger().severe("Could not create nicknames table!");
            e.printStackTrace();
        }
    }

    public void setNickname(Player p, String nick) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                         "INSERT INTO nicknames(uuid,nickname) VALUES (?,?) " +
                                 "ON DUPLICATE KEY UPDATE nickname=VALUES(nickname), last_update=NOW()")) {
                ps.setString(1, p.getUniqueId().toString());
                ps.setString(2, nick);
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void removeNicknameSync(Player p) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM nicknames WHERE uuid=?")) {
            ps.setString(1, p.getUniqueId().toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getNickname(Player p) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT nickname FROM nicknames WHERE uuid=?")) {
            ps.setString(1, p.getUniqueId().toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("nickname");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            plugin.getLogger().info("[NickUtil] Database pool closed.");
        }
    }
}
