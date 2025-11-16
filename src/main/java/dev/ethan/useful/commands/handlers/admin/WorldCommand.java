package dev.ethan.useful.commands.handlers.admin;

import dev.ethan.useful.commands.handlers.CommandHandler;
import dev.ethan.useful.constants.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class WorldCommand implements CommandHandler {

    @Override
    public boolean handle(Player p, String label, String[] args) {

        if (args.length < 1) {
            p.sendMessage(Messages.PREFIX + "§c用法: /world <世界>");
            return true;
        }

        World w = Bukkit.getWorld(args[0]);
        if (w == null) {
            p.sendMessage(Messages.PREFIX + "§c無效的世界名稱");
            return true;
        }

        Location loc = w.getSpawnLocation();
        p.teleport(loc);

        return true;
    }
}
