package dev.ethan.useful.commands.player;

import dev.ethan.useful.constants.Messages;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

@CommandInfo(name = "hat", permission = "useful.player.hat", description = "Wear item as hat", override = true)
public class HatCommand implements NontageCommand {

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player player)) return;
        ItemStack handItem = player.getInventory().getItemInMainHand();
        if (handItem.getType() == Material.AIR) {
            player.sendMessage(Messages.PREFIX + "§c手中物品無效");
            return;
        }
        ItemStack oldHelmet = player.getInventory().getHelmet();
        player.getInventory().setHelmet(handItem);
        player.getInventory().setItemInMainHand(oldHelmet != null ? oldHelmet : new ItemStack(Material.AIR));
        player.sendMessage(Messages.PREFIX + "§a已將手中物品裝備在頭盔欄位");
    }
}