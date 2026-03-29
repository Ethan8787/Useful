package dev.ethan.useful.commands.admin;

import dev.ethan.useful.Main;
import dev.ethan.useful.constants.Messages;
import dev.ethan.useful.utils.LuckPermsUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@CommandInfo(name = "ping", permission = "useful.admin.ping", description = "Check player latency", override = true)
public class PingCommand implements NontageCommand {

    private final LuckPermsUtil luckPermsUtil = Main.getInstance().getLuckPermsUtil();

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§c只有玩家可以使用此指令。");
            return;
        }

        if (args.length == 0) {
            int ping = player.getPing();
            player.sendMessage(Messages.PREFIX + "§a你的延遲為: §f" + ping + "ms");
            return;
        }

        if (!player.hasPermission("useful.admin.ping.others")) {
            player.sendMessage(Messages.PREFIX + "§c你沒有權限查看其他玩家的延遲。");
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(Messages.PREFIX + "§c玩家不存在");
            return;
        }

        int targetPing = target.getPing();
        player.sendMessage(Messages.PREFIX + luckPermsUtil.getPlayerPrefix(target) + target.getName() + " §a的延遲為: §f" + targetPing + "ms");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args, Location location) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1 && sender.hasPermission("useful.admin.ping.others")) {
            List<String> names = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (sender instanceof Player sp && !sp.canSee(p)) continue;
                names.add(p.getName());
            }
            StringUtil.copyPartialMatches(args[0], names, completions);
            Collections.sort(completions);
            return completions;
        }
        return completions;
    }
}