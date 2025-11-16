package dev.ethan.useful.commands.handlers.admin;

import dev.ethan.useful.Main;
import dev.ethan.useful.commands.handlers.CommandHandler;
import dev.ethan.useful.constants.Messages;
import dev.ethan.useful.utils.IPTrackerUtil;
import org.bukkit.entity.Player;

import java.util.List;

public class IpsCommand implements CommandHandler {
    private final IPTrackerUtil ipTrackerUtil = Main.getInstance().getIPTrackerUtil();

    @Override
    public boolean handle(Player p, String label, String[] args) {
        if (args.length != 1) {
            p.sendMessage(Messages.PREFIX + "§c用法: /ips <玩家>");
            return true;
        }
        String playerName = args[0];
        if (!ipTrackerUtil.ipsConfig.contains(playerName)) {
            p.sendMessage(Messages.PREFIX + "§c找不到玩家 §f" + playerName + " §c的紀錄");
            return true;
        }
        List<String> ipList = ipTrackerUtil.ipsConfig.getStringList(playerName);
        if (ipList.isEmpty()) {
            p.sendMessage(Messages.PREFIX + "§e玩家 §f" + playerName + " §e沒有任何已記錄的 IP");
            return true;
        }
        p.sendMessage(Messages.PREFIX + "§e" + playerName + " 的 IP 列表:");
        p.sendMessage("§f" + String.join("§7, §f", ipList));
        return true;
    }
}
