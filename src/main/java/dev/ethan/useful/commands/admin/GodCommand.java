package dev.ethan.useful.commands.admin;

import dev.ethan.useful.Main;
import dev.ethan.useful.constants.Messages;
import dev.ethan.useful.utils.LuckPermsUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

@CommandInfo(name = "god", permission = "useful.admin.god", description = "Toggle invulnerable", override = true)
public class GodCommand implements NontageCommand {

    private final LuckPermsUtil luckPermsUtil = Main.getInstance().getLuckPermsUtil();

    @Override
    public void execute(CommandSender sender, String label, String[] args) {

        if (!(sender instanceof Player player)) return;

        // 自己
        if (args.length == 0) {
            toggleGod(player);
            player.sendMessage(Messages.PREFIX + "§f無敵狀態 "
                    + (player.isInvulnerable() ? "§aOn" : "§cOff"));
            return;
        }

        // 全服 *
        if (args[0].equals("*")) {
            for (Player online : Bukkit.getOnlinePlayers()) {
                toggleGod(online);
                online.sendMessage(Messages.PREFIX + "§f無敵狀態 "
                        + (online.isInvulnerable() ? "§aOn" : "§cOff"));
            }
            player.sendMessage(Messages.PREFIX + "§d已切換所有線上玩家無敵狀態");
            return;
        }

        // 指定玩家
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

        targetPlayer.sendMessage(Messages.PREFIX + "§f無敵狀態 "
                + (targetPlayer.isInvulnerable() ? "§aOn" : "§cOff"));
    }

    private void toggleGod(Player target) {
        target.setInvulnerable(!target.isInvulnerable());
    }
}