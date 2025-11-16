package dev.ethan.useful.commands.handlers.player;

import dev.ethan.useful.Main;
import dev.ethan.useful.commands.handlers.CommandHandler;
import dev.ethan.useful.utils.HomeUtil;
import org.bukkit.entity.Player;

public class SethomeCommand implements CommandHandler {
    private final HomeUtil homeUtil = Main.getInstance().getHomeUtil();

    @Override
    public boolean handle(Player p, String label, String[] args) {
        homeUtil.handleSetHome(p, args);
        return true;
    }
}
