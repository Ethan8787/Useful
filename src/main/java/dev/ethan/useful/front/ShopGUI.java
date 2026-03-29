package dev.ethan.useful.front;

import dev.ethan.useful.managers.ShopManager;
import dev.ethan.useful.models.PriceData;
import dev.ethan.useful.models.ShopItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class ShopGUI {

    public static Inventory mainMenu() {
        Inventory inv = Bukkit.createInventory(null, 27, "§8商店");

        inv.setItem(10, btn(Material.GRASS_BLOCK, "§aBlocks"));
        inv.setItem(12, btn(Material.COOKED_BEEF, "§eFood"));
        inv.setItem(14, btn(Material.DIAMOND_SWORD, "§bTools"));
        inv.setItem(16, btn(Material.CHEST, "§fMisc"));

        return inv;
    }

    public static Inventory category(ShopManager manager, String category, int page) {
        Inventory inv = Bukkit.createInventory(
                new ShopHolder(category, page),
                54,
                "Shop"
        );

        List<ShopItem> items = manager.getCategory(category);

        int start = page * 45;
        int end = Math.min(start + 45, items.size());

        int slot = 0;
        for (int i = start; i < end; i++) {
            inv.setItem(slot++, shopItem(items.get(i)));
        }

        if (page > 0) inv.setItem(45, btn(Material.ARROW, "§7Prev"));
        if (end < items.size()) inv.setItem(53, btn(Material.ARROW, "§7Next"));
        inv.setItem(49, btn(Material.BARRIER, "§cBack"));

        return inv;
    }

    private static ItemStack shopItem(ShopItem si) {
        ItemStack item = new ItemStack(si.getMaterial());
        ItemMeta meta = item.getItemMeta();

        PriceData p = si.getPrice();

        meta.setLore(Arrays.asList(
                "§aBuy: $" + p.getBuy(),
                "§cSell: $" + p.getSell(),
                "",
                "§eL: Buy 1",
                "§6Shift+L: Buy 64",
                "§cR: Sell 1",
                "§4Shift+R: Sell All"
        ));

        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack btn(Material mat, String name) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }
}