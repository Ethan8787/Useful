package dev.ethan.useful.models;

import org.bukkit.Material;

public class ShopItem {
    private final Material material;
    private final String category;
    private final PriceData price;

    public ShopItem(Material material, String category, PriceData price) {
        this.material = material;
        this.category = category;
        this.price = price;
    }

    public Material getMaterial() { return material; }
    public String getCategory() { return category; }
    public PriceData getPrice() { return price; }
}