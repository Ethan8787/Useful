package dev.ethan.useful.listeners;

import dev.ethan.useful.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;
import top.nontage.nontagelib.annotations.AutoListener;

import java.util.concurrent.ThreadLocalRandom;

@AutoListener
public class DamageListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        Entity target = event.getEntity();
        double damage = event.getFinalDamage();
        spawnDamageHologram(target.getLocation().add(0, 1.5, 0), damage);
    }

    private void spawnDamageHologram(Location location, double damage) {
        TextDisplay textDisplay = location.getWorld().spawn(location, TextDisplay.class, display -> {
            display.setText(ChatColor.RED + String.format("%.1f", damage));
            display.setBillboard(Display.Billboard.CENTER);
            display.setShadowed(true);
            display.setSeeThrough(false);
            display.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
        });

        double randomX = ThreadLocalRandom.current().nextDouble(-0.1, 0.1);
        double randomZ = ThreadLocalRandom.current().nextDouble(-0.1, 0.1);
        Vector velocity = new Vector(randomX, 0.2, randomZ);

        textDisplay.setVelocity(velocity);

        Bukkit.getScheduler().runTaskLater(Main.getInstance(), textDisplay::remove, 20L);
    }
}