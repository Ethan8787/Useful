package dev.ethan.useful.utils;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import static dev.ethan.useful.Main.luckPerms;

public class LuckPermsUtil {
    public static void init(JavaPlugin plugin) {
        RegisteredServiceProvider<LuckPerms> provider = plugin.getServer().getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            luckPerms = provider.getProvider();
        }
    }

    public static String getPlayerPrefix(Player p) {
        UserManager manager = luckPerms.getUserManager();
        User u = manager.getUser(p.getUniqueId());
        if (u != null) {
            CachedMetaData data = u.getCachedData().getMetaData();
            String prefix = data.getPrefix();
            return prefix != null ? ChatColor.translateAlternateColorCodes('&', prefix) : "";
        }
        return "";
    }

    public static String getPlayerPrefix(OfflinePlayer offlinePlayer) {
        UserManager manager = luckPerms.getUserManager();
        User u = manager.getUser(offlinePlayer.getUniqueId());
        if (u == null) {
            try {
                u = manager.loadUser(offlinePlayer.getUniqueId()).get();
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }
        if (u != null) {
            CachedMetaData data = u.getCachedData().getMetaData();
            String prefix = data.getPrefix();
            return prefix != null ? ChatColor.translateAlternateColorCodes('&', prefix) : "";
        }
        return "";
    }

    public static String getPlayerSuffix(Player p) {
        UserManager manager = luckPerms.getUserManager();
        User u = manager.getUser(p.getUniqueId());
        if (u != null) {
            CachedMetaData data = u.getCachedData().getMetaData();
            String suffix = data.getSuffix();
            return suffix != null ? ChatColor.translateAlternateColorCodes('&', suffix) : "";
        }
        return "";
    }

}
