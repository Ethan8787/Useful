package dev.ethan.useful.listeners;

import dev.ethan.useful.Main;
import dev.ethan.useful.front.ShopGUI;
import dev.ethan.useful.front.ShopHolder;
import dev.ethan.useful.managers.EconomyManager;
import dev.ethan.useful.managers.ShopManager;
import dev.ethan.useful.models.ShopItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;
import top.nontage.nontagelib.annotations.AutoListener;

@AutoListener
public class ShopListener implements Listener {
    private final ShopManager manager = Main.getInstance().getShopManager();

    @EventHandler
    public void click(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;

        // ===== 主選單 =====
        if (e.getView().getTitle().equals("§8商店")) {
            e.setCancelled(true);

            ItemStack clicked = e.getCurrentItem();
            if (clicked == null || clicked.getType().isAir()) return;

            Material mat = clicked.getType();

            String cat = switch (mat) {
                case GRASS_BLOCK -> "blocks";
                case COOKED_BEEF -> "food";
                case DIAMOND_SWORD -> "tools";
                case CHEST -> "misc";
                default -> null;
            };

            if (cat != null) {
                p.openInventory(ShopGUI.category(manager, cat, 0));
            }
            return;
        }

        // ===== Category GUI =====
        if (!(e.getView().getTopInventory().getHolder() instanceof ShopHolder holder)) return;

        e.setCancelled(true);

        // 防止奇怪操作
        if (e.getClick() == ClickType.NUMBER_KEY ||
                e.getClick() == ClickType.DOUBLE_CLICK) return;

        // 玩家自己背包 → 不處理
        if (e.getClickedInventory() == null ||
                e.getClickedInventory().equals(e.getView().getBottomInventory())) return;

        ItemStack clicked = e.getCurrentItem();
        if (clicked == null || clicked.getType().isAir()) return;

        String category = holder.getCategory();
        int page = holder.getPage();

        // ===== 控制按鈕 =====
        if (clicked.getType() == Material.ARROW) {
            if (e.getSlot() == 45)
                p.openInventory(ShopGUI.category(manager, category, page - 1));

            if (e.getSlot() == 53)
                p.openInventory(ShopGUI.category(manager, category, page + 1));
            return;
        }

        if (clicked.getType() == Material.BARRIER) {
            p.openInventory(ShopGUI.mainMenu());
            return;
        }

        // ===== 交易 =====
        ShopItem si = manager.getItem(clicked.getType());
        if (si == null) return;

        switch (e.getClick()) {
            case LEFT -> buy(p, si, 1);
            case SHIFT_LEFT -> buy(p, si, 64);
            case RIGHT -> sell(p, si, 1);
            case SHIFT_RIGHT -> sellAll(p, si);
        }
    }

    // ===== 防拖曳 =====
    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (e.getView().getTopInventory().getHolder() instanceof ShopHolder) {
            e.setCancelled(true);
        }
    }

    // ===== BUY =====
    private void buy(Player p, ShopItem si, int amount) {
        double total = si.getPrice().getBuy() * amount;

        if (EconomyManager.getEconomy().getBalance(p) < total) {
            p.sendMessage("§c餘額不足");
            return;
        }

        EconomyManager.getEconomy().withdrawPlayer(p, total);
        p.getInventory().addItem(new ItemStack(si.getMaterial(), amount));
    }

    // ===== SELL =====
    private void sell(Player p, ShopItem si, int amount) {
        if (!p.getInventory().containsAtLeast(new ItemStack(si.getMaterial()), amount)) {
            p.sendMessage("§c物品不足");
            return;
        }

        p.getInventory().removeItem(new ItemStack(si.getMaterial(), amount));
        EconomyManager.getEconomy().depositPlayer(p, si.getPrice().getSell() * amount);
    }

    // ===== SELL ALL =====
    private void sellAll(Player p, ShopItem si) {
        int count = 0;

        for (ItemStack item : p.getInventory().getContents()) {
            if (item != null && item.getType() == si.getMaterial()) {
                count += item.getAmount();
                item.setAmount(0);
            }
        }

        if (count == 0) {
            p.sendMessage("§c沒有可販售物品");
            return;
        }

        EconomyManager.getEconomy().depositPlayer(p, si.getPrice().getSell() * count);
    }
}