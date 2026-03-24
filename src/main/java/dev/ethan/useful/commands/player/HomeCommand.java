package dev.ethan.useful.commands.player;

import dev.ethan.useful.Main;
import dev.ethan.useful.utils.HomeUtil;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@CommandInfo(name = "home", permission = "useful.player.home", description = "Teleport to home", override = true)
public class HomeCommand implements NontageCommand {

    private final HomeUtil homeUtil = Main.getInstance().getHomeUtil();

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player player)) return;
        homeUtil.handleHome(player, args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args, Location location) {
        List<String> completions = new ArrayList<>();
        if (sender instanceof Player player && args.length == 1) {
            List<String> homeNames = new ArrayList<>(homeUtil.getHomeNames(player.getUniqueId()));
            StringUtil.copyPartialMatches(args[0], homeNames, completions);
            Collections.sort(completions);
        }
        return completions;
    }
}