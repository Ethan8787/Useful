package dev.ethan.useful.commands.handlers.player;

import dev.ethan.useful.Main;
import dev.ethan.useful.commands.handlers.CommandHandler;
import dev.ethan.useful.constants.Messages;
import dev.ethan.useful.utils.LuckPermsUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class GameModeCommand implements CommandHandler {
    private final GameMode mode;
    private final LuckPermsUtil luckPermsUtil = Main.getInstance().getLuckPermsUtil();

    public GameModeCommand(GameMode mode) {
        this.mode = mode;
    }

    @Override
    public boolean handle(Player p, String label, String[] args) {
        if (args.length == 0) {
            p.setGameMode(mode);
            p.sendMessage(Messages.PREFIX + "§f您的遊戲模式已更新");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            p.sendMessage(Messages.PREFIX + "§c玩家不存在");
            return true;
        }

        target.setGameMode(mode);
        p.sendMessage(Messages.PREFIX + "§f已更新 " + luckPermsUtil.getPlayerPrefix(target) + target.getName() + " §f的遊戲模式");
        return true;
    }
}
