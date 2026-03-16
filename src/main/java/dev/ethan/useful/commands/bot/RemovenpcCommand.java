package dev.ethan.useful.commands.bot;

import dev.ethan.useful.Main;
import dev.ethan.useful.constants.Messages;
import dev.ethan.useful.utils.BotUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

@CommandInfo(
        name = "removenpc",
        permission = "useful.admin.removenpc",
        description = "Remove all NPCs",
        override = true
)
public class RemovenpcCommand implements NontageCommand {

    private final BotUtil botUtil = Main.getInstance().getBotUtil();

    @Override
    public void execute(CommandSender sender, String label, String[] args) {

        if (!(sender instanceof Player p)) return;

        botUtil.removeAllNPCs();
        p.sendMessage(Messages.PREFIX + "§a已移除所有 NPC");
    }
}
