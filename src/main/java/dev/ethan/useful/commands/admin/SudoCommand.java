package dev.ethan.useful.commands.admin;

import dev.ethan.useful.constants.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@CommandInfo(name = "sudo", permission = "useful.admin.sudo", description = "Force a player to execute command", override = true)
public class SudoCommand implements NontageCommand {

    @Override
    public void execute(CommandSender sender, String label, String[] args) {

        if (!(sender instanceof Player executor)) return;

        if (args.length < 2) {
            executor.sendMessage(Messages.PREFIX + "§c用法: /sudo <玩家> <指令>");
            return;
        }

        Player targetPlayer = Bukkit.getPlayer(args[0]);
        if (targetPlayer == null) {
            executor.sendMessage(Messages.PREFIX + "§c玩家不存在");
            return;
        }

        String commandOrChat = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        if (commandOrChat.startsWith("/")) {
            Bukkit.dispatchCommand(targetPlayer, commandOrChat.substring(1));
        } else {
            targetPlayer.chat(commandOrChat);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args, Location location) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
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