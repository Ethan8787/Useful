package dev.ethan.useful.commands.handlers.player;

import dev.ethan.useful.commands.handlers.CommandHandler;
import dev.ethan.useful.constants.Messages;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class FixCommand implements CommandHandler {
    @Override
    public boolean handle(Player p, String label, String[] args) {
        ItemStack item = p.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            p.sendMessage(Messages.PREFIX + "§c你沒有拿著任何物品");
            return true;
        }
        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof Damageable dmg)) {
            p.sendMessage(Messages.PREFIX + "§c這個物品無法被修復");
            return true;
        }
        if (dmg.getDamage() <= 0) {
            p.sendMessage(Messages.PREFIX + "§c這個物品不需要被修復");
            return true;
        }
        dmg.setDamage(0);
        item.setItemMeta(meta);
        p.sendMessage(Messages.PREFIX + "§a已成功修復手中的物品");
        return true;
    }
}
