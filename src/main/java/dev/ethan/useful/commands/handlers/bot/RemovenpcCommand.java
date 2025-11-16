package dev.ethan.useful.commands.handlers.bot;

import dev.ethan.useful.commands.handlers.CommandHandler;
import dev.ethan.useful.constants.Messages;
import dev.ethan.useful.Main;
import dev.ethan.useful.utils.BotUtil;
import org.bukkit.entity.Player;

public class RemovenpcCommand implements CommandHandler {
    private final BotUtil botUtil = Main.getInstance().getBotUtil();

    @Override
    public boolean handle(Player p, String label, String[] args) {
        botUtil.removeAllNPCs();
        p.sendMessage(Messages.PREFIX + "§a已移除所有 NPC");
        return true;
    }
}
