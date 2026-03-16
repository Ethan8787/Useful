/*
目的：
1) /heal 完全治癒時同時解除燃燒 (fire ticks = 0)
2) 支援：/heal、/heal *、/heal <數值>、/heal <玩家>
*/
package dev.ethan.useful.commands.admin;

import dev.ethan.useful.Main;
import dev.ethan.useful.constants.Messages;
import dev.ethan.useful.utils.LuckPermsUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

import java.util.Objects;

@CommandInfo(name = "heal", permission = "useful.admin.heal", description = "Heal yourself or others", override = true)
public class HealCommand implements NontageCommand {

    private final LuckPermsUtil luckPermsUtil = Main.getInstance().getLuckPermsUtil();

    @Override
    public void execute(CommandSender sender, String label, String[] args) {

        if (!(sender instanceof Player player)) return;

        if (args.length == 0) {
            healFull(player);
            player.sendMessage(Messages.PREFIX + "§d已治癒");
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
            return;
        }

        if (args[0].equals("*")) {
            for (Player online : Bukkit.getOnlinePlayers()) {
                healFull(online);
                online.sendMessage(Messages.PREFIX + "§d你被 " + luckPermsUtil.getPlayerPrefix(player) + player.getName() + " §d治癒了");
            }
            player.sendMessage(Messages.PREFIX + "§d已治癒所有線上玩家");
            return;
        }

        try {
            double value = Double.parseDouble(args[0]);
            double maxHealth = Objects.requireNonNull(player.getAttribute(Attribute.MAX_HEALTH)).getValue();

            if (value < 0 || value > maxHealth) {
                player.sendMessage(Messages.PREFIX + "§c血量必須在 0 到 " + maxHealth + " 之間");
                return;
            }

            player.setHealth(value);
            player.setFireTicks(0);
            player.sendMessage(Messages.PREFIX + "§d已設定生命值為 " + value);
            return;

        } catch (NumberFormatException ignored) {
        }

        Player targetPlayer = Bukkit.getPlayer(args[0]);

        if (targetPlayer == null) {
            player.sendMessage(Messages.PREFIX + "§c玩家不存在或離線");
            return;
        }

        healFull(targetPlayer);
        targetPlayer.sendMessage(Messages.PREFIX + "§d你被 " + luckPermsUtil.getPlayerPrefix(player) + player.getName() + " §d治癒了");
        player.sendMessage(Messages.PREFIX + "§d你治癒了 " + luckPermsUtil.getPlayerPrefix(targetPlayer) + targetPlayer.getName());
    }

    private void healFull(Player target) {
        target.setHealth(Objects.requireNonNull(target.getAttribute(Attribute.MAX_HEALTH)).getValue());
        target.setFoodLevel(20);
        target.setFireTicks(0);
    }
}