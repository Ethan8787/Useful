package dev.ethan.useful.commands.handlers.player;

import dev.ethan.useful.commands.handlers.CommandHandler;
import dev.ethan.useful.constants.Messages;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DupeCommand implements CommandHandler {
    @Override
    public boolean handle(Player p, String label, String[] args) {
        ItemStack item = p.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            p.sendMessage(Messages.PREFIX + "§c手上沒有物品");
            return true;
        }
        ItemStack clone = item.clone();
        p.getInventory().addItem(clone);
        return true;
    }
}
