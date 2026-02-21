package dev.ethan.useful.commands.admin;

import dev.ethan.useful.constants.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

@CommandInfo(name = "world", permission = "useful.admin.world", description = "Teleport to a world spawn", override = true)
public class WorldCommand implements NontageCommand {
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player p)) return;
        if (args.length < 1) {
            p.sendMessage(Messages.PREFIX + "§c用法: /world <世界>");
            return;
        }
        World w = Bukkit.getWorld(args[0]);
        if (w == null) {
            p.sendMessage(Messages.PREFIX + "§c無效的世界名稱");
            return;
        }
        Location loc = w.getSpawnLocation();
        p.teleport(loc);
    }
}
