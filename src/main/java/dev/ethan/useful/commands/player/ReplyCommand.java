package dev.ethan.useful.commands.player;

import dev.ethan.useful.Main;
import dev.ethan.useful.utils.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

@CommandInfo(name = "reply", permission = "guildwars.player.reply", description = "Reply to last message", override = true)
public class ReplyCommand implements NontageCommand {

    private final MessageUtil messageUtil = Main.getInstance().getMessageUtil();

    @Override
    public void execute(CommandSender sender, String label, String[] args) {

        if (!(sender instanceof Player player)) return;

        Player targetPlayer = messageUtil.getLastMessaged(player);

        if (targetPlayer == null) {
            player.sendMessage("§c無法回覆");
            return;
        }

        String message = String.join(" ", args);
        messageUtil.sendMessage(player, targetPlayer, message);
    }
}