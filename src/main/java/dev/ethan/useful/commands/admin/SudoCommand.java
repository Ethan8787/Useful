package dev.ethan.useful.commands.admin;

import dev.ethan.useful.constants.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

import java.util.Arrays;

@CommandInfo(name = "sudo", permission = "guildwars.admin.sudo", description = "Force a player to execute command", override = true)
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
}