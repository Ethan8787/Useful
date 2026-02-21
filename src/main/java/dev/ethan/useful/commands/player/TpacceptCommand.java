package dev.ethan.useful.commands.player;

import dev.ethan.useful.Main;
import dev.ethan.useful.utils.TeleportUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

@CommandInfo(name = "tpaccept", permission = "guildwars.player.tpaccept", description = "Accept teleport request", override = true)
public class TpacceptCommand implements NontageCommand {

    private final TeleportUtil teleportUtil = Main.getInstance().getTeleportUtil();

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player player)) return;
        teleportUtil.handleTpacceptCommand(player, args);
    }
}