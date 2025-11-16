package dev.ethan.useful.commands.handlers.admin;

import dev.ethan.useful.commands.handlers.CommandHandler;
import dev.ethan.useful.constants.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class SudoCommand implements CommandHandler {
    @Override
    public boolean handle(Player s, String label, String[] args) {
        if (args.length < 2) {
            s.sendMessage(Messages.PREFIX + "§c用法: /sudo <玩家> <指令>");
            return true;
        }
        Player t = Bukkit.getPlayer(args[0]);
        if (t == null) {
            s.sendMessage(Messages.PREFIX + "§c玩家不存在");
            return true;
        }
        String msg = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        if (msg.startsWith("/")) {
            Bukkit.dispatchCommand(t, msg.substring(1));
        } else {
            t.chat(msg);
        }
        return true;
    }
}
