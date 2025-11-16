package dev.ethan.useful.commands.handlers.player;

import dev.ethan.useful.Main;
import dev.ethan.useful.commands.handlers.CommandHandler;
import dev.ethan.useful.constants.Messages;
import dev.ethan.useful.utils.LuckPermsUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class GodCommand implements CommandHandler {
    private final LuckPermsUtil luckPermsUtil = Main.getInstance().getLuckPermsUtil();

    @Override
    public boolean handle(Player p, String label, String[] args) {
        if (args.length == 0) {
            if (!p.hasPermission("useful.god")) return true;
            p.setInvulnerable(!p.isInvulnerable());
            p.sendMessage(Messages.PREFIX + "§f無敵狀態 " + (p.isInvulnerable() ? "§aOn" : "§cOff"));
            return true;
        }
        Player t = Bukkit.getPlayer(args[0]);
        if (t == null) {
            p.sendMessage(Messages.PREFIX + "§c玩家不存在");
            return true;
        }
        t.setInvulnerable(!t.isInvulnerable());
        p.sendMessage(Messages.PREFIX + luckPermsUtil.getPlayerPrefix(t) + t.getName() + " §f的無敵狀態 " + (t.isInvulnerable() ? "§aOn" : "§cOff"));
        t.sendMessage(Messages.PREFIX + "§f無敵狀態 " + (t.isInvulnerable() ? "§aOn" : "§cOff"));
        return true;
    }
}
