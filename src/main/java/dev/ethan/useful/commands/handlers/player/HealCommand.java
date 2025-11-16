package dev.ethan.useful.commands.handlers.player;

import dev.ethan.useful.Main;
import dev.ethan.useful.commands.handlers.CommandHandler;
import dev.ethan.useful.constants.Messages;
import dev.ethan.useful.utils.LuckPermsUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import java.util.Objects;

public class HealCommand implements CommandHandler {
    private final LuckPermsUtil luckPermsUtil = Main.getInstance().getLuckPermsUtil();

    @Override
    public boolean handle(Player p, String label, String[] args) {
        if (args.length == 0) {
            if (!p.hasPermission("useful.heal")) return true;
            p.setHealth(20);
            p.setFoodLevel(20);
            p.sendMessage(Messages.PREFIX + "§d已治癒");
            p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
            return true;
        }
        try {
            double value = Double.parseDouble(args[0]);
            if (p.hasPermission("useful.heal.amount")) {
                p.setHealth(value);
                p.sendMessage(Messages.PREFIX + "§d已設定生命值為 " + value);
                return true;
            }
        } catch (NumberFormatException ignored) {}
        if (p.hasPermission("useful.heal.others")) {
            Player t = Bukkit.getPlayer(args[0]);
            if (t == null) {
                p.sendMessage(Messages.PREFIX + "§c玩家不存在或離線");
                return true;
            }
            t.setHealth(Objects.requireNonNull(t.getAttribute(Attribute.MAX_HEALTH)).getValue());
            t.setFoodLevel(20);
            t.sendMessage(Messages.PREFIX + "§d你被 " + luckPermsUtil.getPlayerPrefix(p) + p.getName() + " §d治癒了");
            p.sendMessage(Messages.PREFIX + "§d你治癒了 " + luckPermsUtil.getPlayerPrefix(t) + t.getName());
        } else {
            p.sendMessage(Messages.PREFIX + "§c用法: /heal <玩家名稱|血量>");
        }
        return true;
    }
}
