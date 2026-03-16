package dev.ethan.useful.commands.player;

import dev.ethan.useful.Main;
import dev.ethan.useful.utils.PlayerBlockingUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

@CommandInfo(name = "unblock", permission = "useful.player.unblock", description = "Unblock teleport", override = true)
public class UnblockCommand implements NontageCommand {

    private final PlayerBlockingUtil blockingUtil = Main.getInstance().getPlayerBlockingUtil();

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player player)) return;
        blockingUtil.sendUnblock(player, args);
    }
}