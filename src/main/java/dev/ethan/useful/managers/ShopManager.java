package dev.ethan.useful.managers;

import dev.ethan.useful.models.PriceData;
import dev.ethan.useful.models.ShopItem;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class ShopManager {

    private final Map<Material, ShopItem> itemMap;
    private final Map<String, List<ShopItem>> categoryMap;

    public ShopManager(JavaPlugin plugin) {
        this.itemMap = new HashMap<>();
        this.categoryMap = new HashMap<>();

        // 初始化 category
        categoryMap.put("blocks", new ArrayList<>());
        categoryMap.put("food", new ArrayList<>());
        categoryMap.put("tools", new ArrayList<>());
        categoryMap.put("misc", new ArrayList<>());

        for (Material mat : Material.values()) {
            if (mat.isLegacy() || !mat.isItem() || mat.isAir()) continue;

            String category = classify(mat);

            PriceData price = new PriceData(100, 10); // 之後接 config

            ShopItem item = new ShopItem(mat, category, price);

            itemMap.put(mat, item);
            categoryMap.get(category).add(item);
        }

        // 排序（確保 deterministic）
        categoryMap.values().forEach(list ->
                list.sort(Comparator.comparing(i -> i.getMaterial().name()))
        );
    }

    private String classify(Material mat) {
        if (mat.isBlock() && mat.isSolid()) return "blocks";
        if (mat.isEdible()) return "food";

        String name = mat.name();
        if (name.contains("SWORD") || name.contains("PICKAXE") ||
                name.contains("AXE") || name.contains("SHOVEL") ||
                name.contains("HOE") || name.contains("HELMET")) {
            return "tools";
        }

        return "misc";
    }

    public List<ShopItem> getCategory(String category) {
        return categoryMap.getOrDefault(category, Collections.emptyList());
    }

    public ShopItem getItem(Material mat) {
        return itemMap.get(mat);
    }
}