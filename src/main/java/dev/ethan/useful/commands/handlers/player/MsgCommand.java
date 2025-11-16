package dev.ethan.useful.commands.handlers.player;

import dev.ethan.useful.Main;
import dev.ethan.useful.commands.handlers.CommandHandler;
import dev.ethan.useful.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class MsgCommand implements CommandHandler {
    private final MessageUtil messageUtil = Main.getInstance().getMessageUtil();

    @Override
    public boolean handle(Player p, String label, String[] args) {
        if (args.length < 2) {
            p.sendMessage("§c用法: /" + label + " <玩家> <訊息>");
            return true;
        }

        Player t = Bukkit.getPlayer(args[0]);
        if (t == null) {
            p.sendMessage("§c玩家不存在");
            return true;
        }

        String msg = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        messageUtil.sendMessage(p, t, msg);
        return true;
    }
}
