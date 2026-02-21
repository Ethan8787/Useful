package dev.ethan.useful.commands.fun;

import dev.ethan.useful.Main;
import dev.ethan.useful.constants.Messages;
import dev.ethan.useful.utils.LuckPermsUtil;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

import java.util.Objects;

@CommandInfo(name = "boom", permission = "guildwars.fun.boom", description = "Launch a player with firework", override = true)
public class BoomCommand implements NontageCommand {

    private final LuckPermsUtil luckPermsUtil = Main.getInstance().getLuckPermsUtil();

    @Override
    public void execute(CommandSender sender, String label, String[] args) {

        if (!(sender instanceof Player p)) return;

        if (args.length != 1) {
            p.sendMessage(Messages.PREFIX + "§c用法: /boom <玩家|all>");
            return;
        }

        if (args[0].equalsIgnoreCase("all")) {
            Bukkit.getOnlinePlayers().forEach(t -> launch(p, t));
            p.sendMessage(Messages.PREFIX + "§a你將所有玩家射向高空");
            return;
        }

        Player t = Bukkit.getPlayer(args[0]);
        if (t == null) {
            p.sendMessage(Messages.PREFIX + "§c玩家不存在");
            return;
        }

        launch(p, t);
    }

    private void launch(Player s, Player t) {
        Location loc = t.getLocation();
        Firework firework = (Firework) Objects.requireNonNull(loc.getWorld()).spawnEntity(loc, EntityType.FIREWORK_ROCKET);

        FireworkMeta meta = firework.getFireworkMeta();
        meta.setPower(5);

        Color[] palette = new Color[]{Color.fromRGB(12801229), Color.fromRGB(8073150), Color.fromRGB(11743532), Color.fromRGB(14188952), Color.fromRGB(14602026), Color.fromRGB(15435844), Color.fromRGB(15790320)};

        for (Color c : palette) {
            meta.addEffect(FireworkEffect.builder().with(FireworkEffect.Type.BALL).withColor(c).build());
        }

        firework.setFireworkMeta(meta);
        firework.addPassenger(t);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!firework.isValid() || firework.isDead()) {
                    cancel();
                    return;
                }
                firework.setVelocity(firework.getVelocity().setX(0).setZ(0).setY(0.5));
            }
        }.runTaskTimer(Main.getInstance(), 0L, 1L);

        t.sendMessage(Messages.PREFIX + "§fLaunched by " + luckPermsUtil.getPlayerPrefix(s) + s.getName());

        s.sendMessage(Messages.PREFIX + "§aFirework launched for " + luckPermsUtil.getPlayerPrefix(t) + t.getName());

        s.playSound(s.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
    }
}
