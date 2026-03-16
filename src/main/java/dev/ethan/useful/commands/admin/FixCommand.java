package dev.ethan.useful.commands.admin;

import dev.ethan.useful.constants.Messages;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

@CommandInfo(name = "fix", permission = "useful.admin.fix", description = "Repair item in hand", override = true)
public class FixCommand implements NontageCommand {

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player p)) return;
        ItemStack item = p.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            p.sendMessage(Messages.PREFIX + "§c你沒有拿著任何物品");
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof Damageable dmg)) {
            p.sendMessage(Messages.PREFIX + "§c這個物品無法被修復");
            return;
        }
        if (dmg.getDamage() <= 0) {
            p.sendMessage(Messages.PREFIX + "§c這個物品不需要被修復");
            return;
        }
        dmg.setDamage(0);
        item.setItemMeta(meta);
        p.sendMessage(Messages.PREFIX + "§a已成功修復手中的物品");
    }
}
