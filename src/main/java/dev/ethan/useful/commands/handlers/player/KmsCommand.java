package dev.ethan.useful.commands.handlers.player;

import dev.ethan.useful.commands.handlers.CommandHandler;
import org.bukkit.entity.Player;

public class KmsCommand implements CommandHandler {
    @Override
    public boolean handle(Player p, String label, String[] args) {
        p.setHealth(0.0F);
        return true;
    }
}