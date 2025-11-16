package dev.ethan.useful.commands.handlers;

import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public interface CommandHandler {
    boolean handle(Player p, String label, String[] args);
    default List<String> tabComplete(Player p, String alias, String[] args) {
        return Collections.emptyList();
    }
    default String getDescription() {
        return null;
    }
    default String getUsage() {
        return null;
    }
}
