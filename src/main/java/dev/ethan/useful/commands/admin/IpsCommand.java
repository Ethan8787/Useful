package dev.ethan.useful.commands.admin;

import dev.ethan.useful.Main;
import dev.ethan.useful.constants.Messages;
import dev.ethan.useful.utils.IPTrackerUtil;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@CommandInfo(name = "ips", permission = "useful.admin.ips", description = "Check player IP list", override = true)
public class IpsCommand implements NontageCommand {
    private final IPTrackerUtil ipTrackerUtil = Main.getInstance().getIPTrackerUtil();

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player p)) return;
        if (args.length != 1) {
            p.sendMessage(Messages.PREFIX + "§c用法: /ips <玩家>");
            return;
        }
        String playerName = args[0];
        if (!ipTrackerUtil.ipsConfig.contains(playerName)) {
            p.sendMessage(Messages.PREFIX + "§c找不到玩家 §f" + playerName + " §c的紀錄");
            return;
        }
        List<String> ipList = ipTrackerUtil.ipsConfig.getStringList(playerName);
        if (ipList.isEmpty()) {
            p.sendMessage(Messages.PREFIX + "§e玩家 §f" + playerName + " §e沒有任何已記錄的 IP");
            return;
        }
        p.sendMessage(Messages.PREFIX + "§e" + playerName + " 的 IP 列表:");
        p.sendMessage("§f" + String.join("§7, §f", ipList));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args, Location location) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            Set<String> allRecordedPlayers = ipTrackerUtil.ipsConfig.getKeys(false);
            StringUtil.copyPartialMatches(args[0], allRecordedPlayers, completions);
            Collections.sort(completions);
            return completions;
        }
        return completions;
    }
}