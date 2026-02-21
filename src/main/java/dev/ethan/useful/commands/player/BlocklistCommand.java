package dev.ethan.useful.commands.player;

import dev.ethan.useful.Main;
import dev.ethan.useful.utils.PlayerBlockingUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

@CommandInfo(name = "blocklist", permission = "guildwars.player.blocklist", description = "Show blocked players", override = true)
public class BlocklistCommand implements NontageCommand {

    private final PlayerBlockingUtil blockingUtil = Main.getInstance().getPlayerBlockingUtil();

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player p)) return;
        blockingUtil.sendBlockList(p);
    }
}