package dev.ethan.useful.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;

public class NickUtils {
    private static JavaPlugin plugin;
    private static HikariDataSource dataSource;
    private static NickUtils instance;

    private NickUtils() {} // prevent direct instantiation

    public static NickUtils init(JavaPlugin pl, String host, int port, String database, String username, String password, int poolSize) {
        plugin = pl;

        if (instance != null) {
            plugin.getLogger().warning("NickUtils already initialized.");
            return instance;
        }

        instance = new NickUtils();

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&autoReconnect=true&serverTimezone=UTC");
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(poolSize);
        config.setMinimumIdle(2);
        config.setIdleTimeout(60000);
        config.setMaxLifetime(1800000);
        config.setConnectionTimeout(10000);
        config.setLeakDetectionThreshold(30000); // detect leaks

        dataSource = new HikariDataSource(config);
        instance.createTable();

        plugin.getLogger().info("[NickUtils] Database initialized successfully.");
        return instance;
    }

    public static NickUtils getInstance() {
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

    public void setNickname(Player player, String nickname) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                         "INSERT INTO nicknames(uuid,nickname) VALUES (?,?) " +
                                 "ON DUPLICATE KEY UPDATE nickname=VALUES(nickname), last_update=NOW()")) {
                ps.setString(1, player.getUniqueId().toString());
                ps.setString(2, nickname);
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void removeNickname(Player player, Runnable callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement("DELETE FROM nicknames WHERE uuid=?")) {
                ps.setString(1, player.getUniqueId().toString());
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (callback != null) Bukkit.getScheduler().runTask(plugin, callback);
        });
    }

    public void removeNicknameSync(Player player) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM nicknames WHERE uuid=?")) {
            ps.setString(1, player.getUniqueId().toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getNickname(Player player) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT nickname FROM nicknames WHERE uuid=?")) {
            ps.setString(1, player.getUniqueId().toString());
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
            plugin.getLogger().info("[NickUtils] Database pool closed.");
        }
    }
}
