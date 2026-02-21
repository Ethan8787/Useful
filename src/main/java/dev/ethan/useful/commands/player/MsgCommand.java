package dev.ethan.useful.commands.player;

import dev.ethan.useful.Main;
import dev.ethan.useful.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

import java.util.Arrays;

@CommandInfo(name = "msg", permission = "guildwars.player.msg", description = "Private message", override = true)
public class MsgCommand implements NontageCommand {

    private final MessageUtil messageUtil = Main.getInstance().getMessageUtil();

    @Override
    public void execute(CommandSender sender, String label, String[] args) {

        if (!(sender instanceof Player player)) return;

        if (args.length < 2) {
            player.sendMessage("§c用法: /" + label + " <玩家> <訊息>");
            return;
        }

        Player targetPlayer = Bukkit.getPlayer(args[0]);
        if (targetPlayer == null) {
            player.sendMessage("§c玩家不存在");
            return;
        }

        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        messageUtil.sendMessage(player, targetPlayer, message);
    }
}