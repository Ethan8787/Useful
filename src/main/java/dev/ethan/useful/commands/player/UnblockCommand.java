package dev.ethan.useful.commands.player;

import dev.ethan.useful.Main;
import dev.ethan.useful.utils.PlayerBlockingUtil;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@CommandInfo(name = "unblock", permission = "useful.player.unblock", description = "Unblock teleport", override = true)
public class UnblockCommand implements NontageCommand {
    private final PlayerBlockingUtil blockingUtil = Main.getInstance().getPlayerBlockingUtil();

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player player)) return;
        blockingUtil.sendUnblock(player, args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args, Location location) {
        List<String> completions = new ArrayList<>();
        if (!(sender instanceof Player player)) return completions;
        if (args.length == 1) {
            List<String> blockedNames = blockingUtil.getBlockedNames(player.getUniqueId());
            StringUtil.copyPartialMatches(args[0], blockedNames, completions);
            Collections.sort(completions);
            return completions;
        }
        return completions;
    }
}