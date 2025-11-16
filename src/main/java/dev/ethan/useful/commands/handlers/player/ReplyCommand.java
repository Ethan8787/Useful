package dev.ethan.useful.commands.handlers.player;

import dev.ethan.useful.Main;
import dev.ethan.useful.commands.handlers.CommandHandler;
import dev.ethan.useful.utils.MessageUtil;
import org.bukkit.entity.Player;

public class ReplyCommand implements CommandHandler {
    private final MessageUtil messageUtil = Main.getInstance().getMessageUtil();

    @Override
    public boolean handle(Player p, String label, String[] args) {
        Player target = messageUtil.getLastMessaged(p);
        if (target == null) {
            p.sendMessage("§c無法回覆，沒有找到對象");
            return true;
        }

        String msg = String.join(" ", args);
        messageUtil.sendMessage(p, target, msg);
        return true;
    }
}
