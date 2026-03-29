package dev.ethan.useful.managers;

import dev.ethan.useful.Main;
import dev.ethan.useful.models.PlayerData;
import dev.ethan.useful.utils.PlayerUtil;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import static org.bukkit.Bukkit.getOnlinePlayers;

public class PlayerStatusManager extends BukkitRunnable {
    private final PlayerUtil playerUtil;
    private final DataManager dataManager;

    public PlayerStatusManager(JavaPlugin plugin) {
        this.playerUtil = Main.getInstance().getPlayerUtil();
        this.dataManager = Main.getInstance().getDataManager();
        this.runTaskTimer(plugin, 0L, 20L);
    }

    @Override
    public void run() {
        for (Player p : getOnlinePlayers()) {
            PlayerData data = dataManager.getPlayerData(p.getUniqueId());
            data.playTimeSeconds++;
            boolean nicked = playerUtil.isNicked(p);
            boolean vanished = playerUtil.isVanished(p);
            if (nicked || vanished) {
                String msg = "§fYou are currently " + (vanished ? "§cVANISHED" : "") + ((vanished && nicked) ? "§f, " : "") + (nicked ? "§cNICKED" : "");
                sendActionBar(p, msg);
            }
        }
    }

    private void sendActionBar(Player p, String msg) {
        p.sendActionBar(msg);
    }
}