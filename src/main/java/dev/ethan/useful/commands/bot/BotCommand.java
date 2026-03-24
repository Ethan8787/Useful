package dev.ethan.useful.commands.bot;

import dev.ethan.useful.Main;
import dev.ethan.useful.constants.Messages;
import dev.ethan.useful.utils.BotUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

@CommandInfo(name = "bot", permission = "useful.bot.spawn", description = "Spawn a bot", override = true)
public class BotCommand implements NontageCommand {
    private final BotUtil botUtil = Main.getInstance().getBotUtil();
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player p)) return;
        if (args.length < 1) {
            p.sendMessage(Messages.PREFIX + "§c用法: /bot <name>");
            return;
        }
        String botName = args[0];
        p.sendMessage(Messages.PREFIX + "§a已生成機器人：" + botName);
        botUtil.spawnBot(p.getLocation(), botName);
    }
}
