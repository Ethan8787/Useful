package dev.ethan.useful.front;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class ShopHolder implements InventoryHolder {

    private final String category;
    private final int page;

    public ShopHolder(String category, int page) {
        this.category = category;
        this.page = page;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }

    public String getCategory() { return category; }
    public int getPage() { return page; }
}