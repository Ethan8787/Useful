package dev.ethan.useful.commands.handlers.player;

import dev.ethan.useful.Main;
import dev.ethan.useful.commands.handlers.CommandHandler;
import dev.ethan.useful.constants.Messages;
import dev.ethan.useful.utils.LuckPermsUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class FlyCommand implements CommandHandler {
    private final LuckPermsUtil luckPermsUtil = Main.getInstance().getLuckPermsUtil();

    @Override
    public boolean handle(Player p, String label, String[] args) {
        Player t = args.length == 0 ? p : Bukkit.getPlayer(args[0]);
        if (t == null) {
            p.sendMessage(Messages.PREFIX + "§c玩家不存在");
            return true;
        }
        t.setAllowFlight(!t.getAllowFlight());
        p.sendMessage(Messages.PREFIX + luckPermsUtil.getPlayerPrefix(t) + t.getName() + " §f的飛行狀態 " + (t.getAllowFlight() ? "§aOn" : "§cOff"));
        t.sendMessage(Messages.PREFIX + "§f飛行狀態 " + (t.getAllowFlight() ? "§aOn" : "§cOff"));
        return true;
    }
}
