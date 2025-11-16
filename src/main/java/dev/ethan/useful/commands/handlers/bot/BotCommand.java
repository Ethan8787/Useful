package dev.ethan.useful.commands.handlers.bot;

import dev.ethan.useful.commands.handlers.CommandHandler;
import dev.ethan.useful.constants.Messages;
import dev.ethan.useful.Main;
import dev.ethan.useful.utils.BotUtil;
import org.bukkit.entity.Player;

public class BotCommand implements CommandHandler {
    private final BotUtil botUtil = Main.getInstance().getBotUtil();

    @Override
    public boolean handle(Player p, String label, String[] args) {
        if (args.length < 1) {
            p.sendMessage(Messages.PREFIX + "§c用法: /bot <name>");
            return true;
        }
        String botName = args[0];
        p.sendMessage(Messages.PREFIX + "§a已生成機器人：" + botName);
        botUtil.spawnBot(p.getLocation(), botName);
        return true;
    }
}
