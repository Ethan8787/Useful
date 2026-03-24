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

@CommandInfo(name = "fly", permission = "useful.admin.fly", description = "Toggle flight", override = true)
public class FlyCommand implements NontageCommand {

    private final LuckPermsUtil luckPermsUtil = Main.getInstance().getLuckPermsUtil();

    @Override
    public void execute(CommandSender sender, String label, String[] args) {

        if (!(sender instanceof Player player)) return;

        if (args.length == 0) {
            toggleFly(player);
            player.sendMessage(Messages.PREFIX + "§f飛行狀態 "
                    + (player.getAllowFlight() ? "§aOn" : "§cOff"));
            return;
        }

        if (args[0].equals("*")) {
            for (Player online : Bukkit.getOnlinePlayers()) {
                toggleFly(online);
            }
            player.sendMessage(Messages.PREFIX + "§d已切換所有線上玩家飛行狀態");
            return;
        }

        Player targetPlayer = Bukkit.getPlayer(args[0]);
        if (targetPlayer == null) {
            player.sendMessage(Messages.PREFIX + "§c玩家不存在");
            return;
        }

        toggleFly(targetPlayer);

        if (player.equals(targetPlayer)) {
            player.sendMessage(Messages.PREFIX + "§f飛行狀態 "
                    + (targetPlayer.getAllowFlight() ? "§aOn" : "§cOff"));
            return;
        }

        player.sendMessage(Messages.PREFIX
                + luckPermsUtil.getPlayerPrefix(targetPlayer)
                + targetPlayer.getName()
                + " §f的飛行狀態 "
                + (targetPlayer.getAllowFlight() ? "§aOn" : "§cOff"));

        targetPlayer.sendMessage(Messages.PREFIX + "§f您的飛行狀態已被更新為 "
                + (targetPlayer.getAllowFlight() ? "§aOn" : "§cOff"));
    }

    private void toggleFly(Player target) {
        target.setAllowFlight(!target.getAllowFlight());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args, Location location) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            List<String> targets = new ArrayList<>();
            targets.add("*");
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (sender instanceof Player sp && !sp.canSee(p)) continue;
                targets.add(p.getName());
            }
            StringUtil.copyPartialMatches(args[0], targets, completions);
            Collections.sort(completions);
            return completions;
        }
        return completions;
    }
}