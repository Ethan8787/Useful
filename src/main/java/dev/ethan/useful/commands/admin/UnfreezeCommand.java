package dev.ethan.useful.commands.admin;

import dev.ethan.useful.Main;
import dev.ethan.useful.constants.Messages;
import dev.ethan.useful.managers.GameManager;
import dev.ethan.useful.utils.LuckPermsUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@CommandInfo(name = "unfreeze", permission = "useful.admin.unfreeze", description = "Unfreeze a player", override = true)
public class UnfreezeCommand implements NontageCommand {
    private final GameManager gameManager = Main.getInstance().getGameManager();
    private final LuckPermsUtil luckPermsUtil = Main.getInstance().getLuckPermsUtil();

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player s)) return;
        if (args.length != 1) {
            s.sendMessage(Messages.PREFIX + "§c用法: /unfreeze <玩家>");
            return;
        }
        Player t = Bukkit.getPlayer(args[0]);
        if (t == null) {
            s.sendMessage(Messages.PREFIX + "§c目標無效");
            return;
        }
        gameManager.unfreezePlayer(t.getName());
        s.sendMessage(Messages.PREFIX + "§c你已解凍 " + luckPermsUtil.getPlayerPrefix(t) + t.getName());
        t.sendMessage(Messages.PREFIX + "§a你已被解凍");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args, Location location) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            List<String> names = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (sender instanceof Player sp && !sp.canSee(p)) continue;
                names.add(p.getName());
            }
            StringUtil.copyPartialMatches(args[0], names, completions);
            Collections.sort(completions);
            return completions;
        }
        return completions;
    }
}