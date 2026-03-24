package dev.ethan.useful.commands.player;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

@CommandInfo(name = "kms", permission = "useful.player.kms", description = "Kill yourself", override = true)
public class KmsCommand implements NontageCommand {

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player player)) return;
        player.setHealth(0.0);
    }
}