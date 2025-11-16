package dev.ethan.useful.commands.handlers.player;

import dev.ethan.useful.Main;
import dev.ethan.useful.commands.handlers.CommandHandler;
import dev.ethan.useful.utils.TeleportUtil;
import org.bukkit.entity.Player;

public class TpacceptCommand implements CommandHandler {
    private final TeleportUtil teleportUtil = Main.getInstance().getTeleportUtil();

    @Override
    public boolean handle(Player p, String label, String[] args) {
        teleportUtil.handleTpacceptCommand(p, args);
        return true;
    }
}
