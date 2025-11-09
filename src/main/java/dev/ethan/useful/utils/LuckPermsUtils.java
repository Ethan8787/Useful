package dev.ethan.useful.utils;

import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import static dev.ethan.useful.Main.luckPerms;

public class LuckPermsUtils {
    public static String getPlayerPrefix(Player player) {
        UserManager userManager = luckPerms.getUserManager();
        User user = userManager.getUser(player.getUniqueId());
        if (user != null) {
            CachedMetaData metaData = user.getCachedData().getMetaData();
            String prefix = metaData.getPrefix();
            return prefix != null ? ChatColor.translateAlternateColorCodes('&', prefix) : "";
        }
        return "";
    }

    public static String getPlayerPrefix(OfflinePlayer offlinePlayer) {
        UserManager userManager = luckPerms.getUserManager();
        User user = userManager.getUser(offlinePlayer.getUniqueId());
        if (user == null) {
            try {
                user = userManager.loadUser(offlinePlayer.getUniqueId()).get();
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }

        if (user != null) {
            CachedMetaData metaData = user.getCachedData().getMetaData();
            String prefix = metaData.getPrefix();
            return prefix != null ? ChatColor.translateAlternateColorCodes('&', prefix) : "";
        }
        return "";
    }


    public static String getPlayerSuffix(Player player) {
        UserManager userManager = luckPerms.getUserManager();
        User user = userManager.getUser(player.getUniqueId());
        if (user != null) {
            CachedMetaData metaData = user.getCachedData().getMetaData();
            String suffix = metaData.getSuffix();
            return suffix != null ? ChatColor.translateAlternateColorCodes('&', suffix) : "";
        }
        return "";
    }

}
