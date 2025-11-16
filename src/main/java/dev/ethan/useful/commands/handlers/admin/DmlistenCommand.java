package dev.ethan.useful.commands.handlers.admin;

import dev.ethan.useful.Main;
import dev.ethan.useful.commands.handlers.CommandHandler;
import dev.ethan.useful.utils.MessageUtil;
import org.bukkit.entity.Player;

public class DmlistenCommand implements CommandHandler {
    private final MessageUtil messageUtil = Main.getInstance().getMessageUtil();

    @Override
    public boolean handle(Player p, String label, String[] args) {
        if (messageUtil.isDmListenerActive(p)) {
            messageUtil.removeDmListener(p);
        } else {
            messageUtil.addDmListener(p);
        }
        return true;
    }
}
