package dev.ethan.useful.commands.admin;

import dev.ethan.useful.Main;
import dev.ethan.useful.constants.Messages;
import dev.ethan.useful.utils.LuckPermsUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

@CommandInfo(name = "fly", permission = "useful.admin.fly", description = "Toggle flight", override = true)
public class FlyCommand implements NontageCommand {

    private final LuckPermsUtil luckPermsUtil = Main.getInstance().getLuckPermsUtil();

    @Override
    public void execute(CommandSender sender, String label, String[] args) {

        if (!(sender instanceof Player player)) return;

        // 自己
        if (args.length == 0) {
            toggleFly(player);
            player.sendMessage(Messages.PREFIX + "§f飛行狀態 "
                    + (player.getAllowFlight() ? "§aOn" : "§cOff"));
            return;
        }

        // 全服 *
        if (args[0].equals("*")) {
            for (Player online : Bukkit.getOnlinePlayers()) {
                toggleFly(online);
                online.sendMessage(Messages.PREFIX + "§f飛行狀態 "
                        + (online.getAllowFlight() ? "§aOn" : "§cOff"));
            }
            player.sendMessage(Messages.PREFIX + "§d已切換所有線上玩家飛行狀態");
            return;
        }

        // 指定玩家
        Player targetPlayer = Bukkit.getPlayer(args[0]);
        if (targetPlayer == null) {
            player.sendMessage(Messages.PREFIX + "§c玩家不存在");
            return;
        }

        toggleFly(targetPlayer);

        if (player.equals(targetPlayer)) {
            player.sendMessage(Messages.PREFIX + "§f飛行狀態 "
                    + (targetPlayer.getAllowFlight() ? "§aOn" : "§cOff"));
            return;
        }

        player.sendMessage(Messages.PREFIX
                + luckPermsUtil.getPlayerPrefix(targetPlayer)
                + targetPlayer.getName()
                + " §f的飛行狀態 "
                + (targetPlayer.getAllowFlight() ? "§aOn" : "§cOff"));

        targetPlayer.sendMessage(Messages.PREFIX + "§f飛行狀態 "
                + (targetPlayer.getAllowFlight() ? "§aOn" : "§cOff"));
    }

    private void toggleFly(Player target) {
        target.setAllowFlight(!target.getAllowFlight());
    }
}