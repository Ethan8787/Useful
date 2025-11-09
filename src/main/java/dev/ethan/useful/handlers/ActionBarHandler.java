package dev.ethan.useful.handlers;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static dev.ethan.useful.utils.MessageUtils.sendActionBar;
import static dev.ethan.useful.utils.PlayerUtils.isNicked;
import static dev.ethan.useful.utils.PlayerUtils.isVanished;
import static org.bukkit.Bukkit.getOnlinePlayers;

public class ActionBarHandler extends BukkitRunnable {
    @Override
    public void run() {
        for (Player player : getOnlinePlayers()) {
            boolean nicked = isNicked(player);
            boolean vanished = isVanished(player);

            String message = null;

            if (nicked && vanished) {
                message = "§fYou are currently §cVANISHED§f, §cNICKED";
            } else if (vanished) {
                message = "§fYou are currently §cVANISHED";
            } else if (nicked) {
                message = "§fYou are currently §cNICKED";
            }

            if (message != null) {
                sendActionBar(player, message);
            }
        }
    }
}

