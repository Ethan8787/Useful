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

public class LuckPermsUtil {
    private final LuckPerms luckPerms;

    public LuckPermsUtil(JavaPlugin plugin) {
        RegisteredServiceProvider<LuckPerms> provider = plugin.getServer().getServicesManager().getRegistration(LuckPerms.class);
        if (provider == null) {
            plugin.getLogger().severe("LuckPerms not found! Prefix/suffix will not work.");
            this.luckPerms = null;
        } else {
            this.luckPerms = provider.getProvider();
            plugin.getLogger().info("Successfully hooked into LuckPerms.");
        }
    }

    public String getPlayerPrefix(Player p) {
        if (luckPerms == null) return "";
        User u = luckPerms.getUserManager().getUser(p.getUniqueId());
        if (u == null) return "";
        CachedMetaData meta = u.getCachedData().getMetaData();
        String prefix = meta.getPrefix();
        return prefix != null ? ChatColor.translateAlternateColorCodes('&', prefix) : "";
    }

    public String getPlayerPrefix(OfflinePlayer offline) {
        if (luckPerms == null) return "";
        UserManager manager = luckPerms.getUserManager();
        User u = manager.getUser(offline.getUniqueId());
        try {
            if (u == null) {
                u = manager.loadUser(offline.getUniqueId()).get();
            }
        } catch (Exception e) {
            return "";
        }
        if (u == null) return "";
        CachedMetaData meta = u.getCachedData().getMetaData();
        String prefix = meta.getPrefix();
        return prefix != null ? ChatColor.translateAlternateColorCodes('&', prefix) : "";
    }

    public String getPlayerSuffix(Player p) {
        if (luckPerms == null) return "";
        User u = luckPerms.getUserManager().getUser(p.getUniqueId());
        if (u == null) return "";
        CachedMetaData meta = u.getCachedData().getMetaData();
        String suffix = meta.getSuffix();
        return suffix != null ? ChatColor.translateAlternateColorCodes('&', suffix) : "";
    }
}
