package dev.ethan.useful.listeners;

import dev.ethan.useful.Main;
import dev.ethan.useful.managers.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import top.nontage.nontagelib.annotations.AutoListener;

import java.util.UUID;

@AutoListener
public class CrystalListener implements Listener {

    private final GameManager gameManager = Main.getInstance().getGameManager();

    @EventHandler
    public void onCrystalDamage(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof EnderCrystal crystal)) return;
        if (e.getDamager() instanceof Player p) {
            gameManager.lastCrystalShooter.put(crystal.getUniqueId(), p.getUniqueId());
            Bukkit.getLogger().info("Shot by " + p.getName() + " on crystal " + crystal.getUniqueId());
        }
        if (e.getDamager() instanceof Projectile proj) {
            if (proj.getShooter() instanceof Player p) {
                gameManager.lastCrystalShooter.put(crystal.getUniqueId(), p.getUniqueId());
                Bukkit.getLogger().info("Shot by " + p.getName() + " on crystal " + crystal.getUniqueId());
            }
        }
    }

    @EventHandler
    public void onCrystalExplode(EntityExplodeEvent e) {
        if (!(e.getEntity() instanceof EnderCrystal crystal)) return;
        UUID id = crystal.getUniqueId();
        UUID shooter = gameManager.lastCrystalShooter.remove(id);
        if (shooter != null) {
            gameManager.crystalTapper.put(id, shooter);
            Bukkit.getLogger().info("Crystal exploded " + crystal.getUniqueId());
            Bukkit.getLogger().info("Last shooter = " + shooter);

        }
    }
}
