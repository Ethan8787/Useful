package dev.ethan.useful.commands.admin.gameMode;

import dev.ethan.useful.Main;
import dev.ethan.useful.constants.Messages;
import dev.ethan.useful.utils.LuckPermsUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import top.nontage.nontagelib.command.NontageCommand;

import java.util.ArrayList;
import java.util.List;

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
        String modeName = getChineseGameModeName(mode);
        if (p.equals(target)) {
            p.sendMessage(Messages.PREFIX + "§f您的遊戲模式已更新為 §a" + modeName);
            return;
        }
        p.sendMessage(Messages.PREFIX + "§f已更新 " + luckPermsUtil.getPlayerPrefix(target) + target.getName() + " §f的遊戲模式為 §a" + modeName);
        target.sendMessage(Messages.PREFIX + "§f您的遊戲模式已被更新為 §a" + modeName);
    }

    private String getChineseGameModeName(GameMode mode) {
        return switch (mode) {
            case SURVIVAL -> "生存模式";
            case CREATIVE -> "創造模式";
            case ADVENTURE -> "冒險模式";
            case SPECTATOR -> "旁觀模式";
        };
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
            return completions;
        }
        return completions;
    }
}