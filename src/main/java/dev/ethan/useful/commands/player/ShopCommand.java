package dev.ethan.useful.commands.player;

import dev.ethan.useful.front.ShopGUI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

@CommandInfo(name = "shop", permission = "useful.player.shop", description = "Open shop menu", override = true)
public class ShopCommand implements NontageCommand {

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return;
        }
        player.openInventory(ShopGUI.mainMenu());
    }
}