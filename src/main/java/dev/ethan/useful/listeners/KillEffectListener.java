package dev.ethan.useful.listeners;

import dev.ethan.useful.Main;
import dev.ethan.useful.managers.GameManager;
import dev.ethan.useful.utils.AceUtil;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import top.nontage.nontagelib.annotations.AutoListener;

import java.util.UUID;

@AutoListener
public class KillEffectListener implements Listener {
    private final GameManager gameManager = Main.getInstance().getGameManager();
    private final AceUtil aceUtil = Main.getInstance().getAceUtil();

    @EventHandler
    public void onPlayerKill(PlayerDeathEvent e) {
        Player killer = e.getEntity().getKiller();
        if (killer == null) return;
        if (!killer.hasPermission("useful.killeffect")) return;
        killer.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, e.getEntity().getLocation(), 50, 0.5, 1, 0.5, 0.1);
        UUID killerId = killer.getUniqueId();
        int kills = gameManager.increaseKillStreak(killerId);
        aceUtil.playKillSound(killer, kills);
    }
}