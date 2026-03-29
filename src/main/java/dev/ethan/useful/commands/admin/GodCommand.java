package dev.ethan.useful.commands.admin;

import dev.ethan.useful.Main;
import dev.ethan.useful.constants.Messages;
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

@CommandInfo(name = "god", permission = "useful.admin.god", description = "Toggle invulnerable", override = true)
public class GodCommand implements NontageCommand {

    private final LuckPermsUtil luckPermsUtil = Main.getInstance().getLuckPermsUtil();

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player player)) return;
        if (args.length == 0) {
            toggleGod(player);
            player.sendMessage(Messages.PREFIX + "§f無敵狀態 "
                    + (player.isInvulnerable() ? "§aOn" : "§cOff"));
            return;
        }
        if (args[0].equals("*")) {
            for (Player online : Bukkit.getOnlinePlayers()) {
                toggleGod(online);
                online.sendMessage(Messages.PREFIX + "§f無敵狀態 "
                        + (online.isInvulnerable() ? "§aOn" : "§cOff"));
            }
            player.sendMessage(Messages.PREFIX + "§d已切換所有線上玩家無敵狀態");
            return;
        }
        Player targetPlayer = Bukkit.getPlayer(args[0]);
        if (targetPlayer == null) {
            player.sendMessage(Messages.PREFIX + "§c玩家不存在");
            return;
        }
        toggleGod(targetPlayer);
        if (player.equals(targetPlayer)) {
            player.sendMessage(Messages.PREFIX + "§f無敵狀態 "
                    + (targetPlayer.isInvulnerable() ? "§aOn" : "§cOff"));
            return;
        }
        player.sendMessage(Messages.PREFIX
                + luckPermsUtil.getPlayerPrefix(targetPlayer)
                + targetPlayer.getName()
                + " §f的無敵狀態 "
                + (targetPlayer.isInvulnerable() ? "§aOn" : "§cOff"));
        targetPlayer.sendMessage(Messages.PREFIX + "§f您的無敵狀態已被更新為 "
                + (targetPlayer.isInvulnerable() ? "§aOn" : "§cOff"));
    }

    private void toggleGod(Player target) {
        target.setInvulnerable(!target.isInvulnerable());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args, Location location) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            List<String> targets = new ArrayList<>();
            targets.add("*");
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (sender instanceof Player sp && !sp.canSee(p)) continue;
                targets.add(p.getName());
            }
            StringUtil.copyPartialMatches(args[0], targets, completions);
            Collections.sort(completions);
            return completions;
        }
        return completions;
    }
}