package dev.ethan.useful.commands.player.gameMode;

import dev.ethan.useful.Main;
import dev.ethan.useful.constants.Messages;
import dev.ethan.useful.utils.LuckPermsUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.nontage.nontagelib.command.NontageCommand;

public abstract class AbstractGameModeCommand implements NontageCommand {
    private final GameMode mode;
    private final LuckPermsUtil luckPermsUtil = Main.getInstance().getLuckPermsUtil();

    protected AbstractGameModeCommand(GameMode mode) {
        this.mode = mode;
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player p)) return;
        Player target = args.length == 0 ? p : Bukkit.getPlayer(args[0]);
        if (target == null) {
            p.sendMessage(Messages.PREFIX + "§c玩家不存在");
            return;
        }
        target.setGameMode(mode);
        if (p.equals(target)) {
            p.sendMessage(Messages.PREFIX + "§f您的遊戲模式已更新為 §a" + mode.name());
            return;
        }
        p.sendMessage(Messages.PREFIX + "§f已更新 " + luckPermsUtil.getPlayerPrefix(target) + target.getName() + " §f的遊戲模式為 §a" + mode.name());
        target.sendMessage(Messages.PREFIX + "§f您的遊戲模式已被更新為 §a" + mode.name());
    }
}
