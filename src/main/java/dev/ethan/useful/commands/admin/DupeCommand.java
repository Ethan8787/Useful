package dev.ethan.useful.commands.admin;

import dev.ethan.useful.constants.Messages;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

@CommandInfo(name = "dupe", permission = "useful.admin.dupe", description = "Duplicate item in hand", override = true)
public class DupeCommand implements NontageCommand {

    @Override
    public void execute(CommandSender sender, String label, String[] args) {

        if (!(sender instanceof Player p)) return;

        ItemStack item = p.getInventory().getItemInMainHand();

        if (item.getType() == Material.AIR) {
            p.sendMessage(Messages.PREFIX + "§c手上沒有物品");
            return;
        }

        p.getInventory().addItem(item.clone());
    }
}
