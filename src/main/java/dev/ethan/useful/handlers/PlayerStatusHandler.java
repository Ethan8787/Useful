package dev.ethan.useful.handlers;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import static dev.ethan.useful.utils.MessageUtil.sendActionBar;
import static dev.ethan.useful.utils.PlayerUtil.isNicked;
import static dev.ethan.useful.utils.PlayerUtil.isVanished;
import static org.bukkit.Bukkit.getOnlinePlayers;

public class PlayerStatusHandler extends BukkitRunnable {
    public static void init(JavaPlugin plugin) {
        new PlayerStatusHandler().runTaskTimer(plugin, 0L, 20L);
    }

    @Override
    public void run() {
        for (Player p : getOnlinePlayers()) {
            boolean nicked = isNicked(p);
            boolean vanished = isVanished(p);
            String message = null;
            if (nicked && vanished) {
                message = "§fYou are currently §cVANISHED§f, §cNICKED";
            } else if (vanished) {
                message = "§fYou are currently §cVANISHED";
            } else if (nicked) {
                message = "§fYou are currently §cNICKED";
            }
            if (message != null) {
                sendActionBar(p, message);
            }
        }
    }
}

