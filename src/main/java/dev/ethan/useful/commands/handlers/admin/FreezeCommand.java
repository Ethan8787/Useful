package dev.ethan.useful.commands.handlers.admin;

import dev.ethan.useful.Main;
import dev.ethan.useful.commands.handlers.CommandHandler;
import dev.ethan.useful.constants.Messages;
import dev.ethan.useful.listeners.GameListener;
import dev.ethan.useful.utils.LuckPermsUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class FreezeCommand implements CommandHandler {
    private final GameListener gameListener = Main.getInstance().getGameListener();
    private final LuckPermsUtil luckPermsUtil = Main.getInstance().getLuckPermsUtil();

    @Override
    public boolean handle(Player s, String label, String[] args) {
        if (args.length != 1) {
            s.sendMessage(Messages.PREFIX + "§c用法: /freeze <玩家>");
            return true;
        }
        Player t = Bukkit.getPlayer(args[0]);
        if (t == null) {
            s.sendMessage(Messages.PREFIX + "§c目標無效");
            return true;
        }
        gameListener.freezePlayer(t.getName());
        s.sendMessage(Messages.PREFIX + "§c你已凍結 " + luckPermsUtil.getPlayerPrefix(t) + t.getName());
        t.sendMessage(Messages.PREFIX + "§c你已被凍結");
        return true;
    }
}
