package dev.ethan.useful.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import top.nontage.nontagelib.annotations.AutoListener;

@AutoListener
public class HandSwapListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onHandSwap(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();

        if (player.isSneaking()) {
            event.setCancelled(true);
            openCustomMenu(player);
        }
    }

    private void openCustomMenu(Player player) {
        player.performCommand("shop");
    }
}