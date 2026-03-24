package dev.ethan.useful.commands.player;

import dev.ethan.useful.Main;
import dev.ethan.useful.utils.PlayerBlockingUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@CommandInfo(name = "block", permission = "useful.player.block", description = "Block teleport", override = true)
public class BlockCommand implements NontageCommand {
    private final PlayerBlockingUtil blockingUtil = Main.getInstance().getPlayerBlockingUtil();

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player p)) return;
        blockingUtil.sendBlock(p, args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args, Location location) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            List<String> allPlayerNames = new ArrayList<>();
            for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                String name = offlinePlayer.getName();
                if (name != null) {
                    allPlayerNames.add(name);
                }
            }
            StringUtil.copyPartialMatches(args[0], allPlayerNames, completions);
            Collections.sort(completions);
            return completions;
        }
        return completions;
    }
}