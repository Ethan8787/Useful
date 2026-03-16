package dev.ethan.useful.commands.admin;

import dev.ethan.useful.Main;
import dev.ethan.useful.constants.Messages;
import dev.ethan.useful.managers.GameManager;
import dev.ethan.useful.utils.LuckPermsUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

@CommandInfo(name = "freeze", permission = "useful.admin.freeze", description = "Freeze a player", override = true)
public class FreezeCommand implements NontageCommand {
    private final GameManager gameManager = Main.getInstance().getGameManager();
    private final LuckPermsUtil luckPermsUtil = Main.getInstance().getLuckPermsUtil();

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player s)) return;
        if (args.length != 1) {
            s.sendMessage(Messages.PREFIX + "§c用法: /freeze <玩家>");
            return;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            s.sendMessage(Messages.PREFIX + "§c目標無效");
            return;
        }
        gameManager.freezePlayer(target.getName());
        s.sendMessage(Messages.PREFIX + "§c你已凍結 " + luckPermsUtil.getPlayerPrefix(target) + target.getName());
        target.sendMessage(Messages.PREFIX + "§c你已被凍結");
    }
}
