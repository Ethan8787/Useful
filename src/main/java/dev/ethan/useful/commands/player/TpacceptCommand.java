package dev.ethan.useful.commands.player;

import dev.ethan.useful.Main;
import dev.ethan.useful.utils.TeleportUtil;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@CommandInfo(name = "tpaccept", permission = "useful.player.tpaccept", description = "Accept teleport request", override = true)
public class TpacceptCommand implements NontageCommand {

    private final TeleportUtil teleportUtil = Main.getInstance().getTeleportUtil();

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player player)) return;
        teleportUtil.handleTpacceptCommand(player, args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args, Location location) {
        List<String> completions = new ArrayList<>();
        if (sender instanceof Player player && args.length == 1) {
            List<String> requesters = teleportUtil.getPendingRequesters(player);
            StringUtil.copyPartialMatches(args[0], requesters, completions);
            Collections.sort(completions);
        }
        return completions;
    }
}