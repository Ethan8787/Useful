package dev.ethan.useful.handlers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import top.nontage.nontagelib.utils.inventory.InventoryBuilder;
import top.nontage.nontagelib.utils.item.ItemBuilder;

public class NontageGuiHandlers {
    public static void openTestGui(Player player) {
        InventoryBuilder inv = new InventoryBuilder(27, "§bNontageLib §b§l測試介面");
        inv.setItem(
            new ItemBuilder(Material.DIAMOND)
                .setName("§b點擊我")
                .setLore("§7點擊獲得鑽石")
                .build(),
            event -> {
                Player p = event.getPlayer();
                p.getInventory().addItem(new ItemStack(Material.DIAMOND));
                p.sendMessage("§a你獲得了一個鑽石");
            },
            13
        );
        inv.setCloseEvent(event -> {
            event.getPlayer().sendMessage("§c你關閉了介面");
        });
        player.openInventory(inv.getInventory());
    }
}
