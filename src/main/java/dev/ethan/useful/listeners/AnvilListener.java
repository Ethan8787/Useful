package dev.ethan.useful.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import top.nontage.nontagelib.annotations.AutoListener;

@AutoListener
public class AnvilListener implements Listener {
    @EventHandler
    public void onAnvilUse(PrepareAnvilEvent e) {
        ItemStack result = e.getResult();
        if (result == null) return;
        ItemMeta meta = result.getItemMeta();
        if (meta == null) return;
        Component displayName = meta.displayName();
        if (displayName != null) {
            displayName = displayName.decoration(TextDecoration.ITALIC, false);
            meta.displayName(displayName);
            result.setItemMeta(meta);
            e.setResult(result);
        }
    }
}