package dev.ethan.useful.commands.admin;

import dev.ethan.useful.constants.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

@CommandInfo(name = "ping", permission = "useful.admin.ping", description = "Check player latency", override = true)
public class PingCommand implements NontageCommand {

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can check their ping.");
            return;
        }

        int ping = player.getPing();
        player.sendMessage(Messages.PREFIX + "§a你的延遲為: §f" + ping + "ms");
    }
}