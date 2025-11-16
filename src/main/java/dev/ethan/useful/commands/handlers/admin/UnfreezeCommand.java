package dev.ethan.useful.commands.handlers.admin;

import dev.ethan.useful.Main;
import dev.ethan.useful.commands.handlers.CommandHandler;
import dev.ethan.useful.constants.Messages;
import dev.ethan.useful.managers.GameManager;
import dev.ethan.useful.utils.LuckPermsUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class UnfreezeCommand implements CommandHandler {
    private final GameManager gameManager = Main.getInstance().getGameManager();
    private final LuckPermsUtil luckPermsUtil = Main.getInstance().getLuckPermsUtil();

    @Override
    public boolean handle(Player s, String label, String[] args) {
        if (args.length != 1) {
            s.sendMessage(Messages.PREFIX + "§c用法: /unfreeze <玩家>");
            return true;
        }
        Player t = Bukkit.getPlayer(args[0]);
        if (t == null) {
            s.sendMessage(Messages.PREFIX + "§c目標無效");
            return true;
        }
        gameManager.unfreezePlayer(t.getName());
        s.sendMessage(Messages.PREFIX + "§c你已解凍 " + luckPermsUtil.getPlayerPrefix(t) + t.getName());
        t.sendMessage(Messages.PREFIX + "§a你已被解凍");
        return true;
    }
}
