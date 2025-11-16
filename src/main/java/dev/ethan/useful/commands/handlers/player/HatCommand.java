package dev.ethan.useful.commands.handlers.player;

import dev.ethan.useful.commands.handlers.CommandHandler;
import dev.ethan.useful.constants.Messages;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HatCommand implements CommandHandler {
    @Override
    public boolean handle(Player p, String label, String[] args) {
        ItemStack hand = p.getInventory().getItemInMainHand();
        if (hand.getType() == Material.AIR) {
            p.sendMessage(Messages.PREFIX + "§c手中物品無效");
            return true;
        }
        ItemStack oldHelmet = p.getInventory().getHelmet();
        p.getInventory().setHelmet(hand);
        p.getInventory().setItemInMainHand(oldHelmet != null ? oldHelmet : new ItemStack(Material.AIR));
        p.sendMessage(Messages.PREFIX + "§a已將手中物品裝備在頭盔欄位");
        return true;
    }
}
