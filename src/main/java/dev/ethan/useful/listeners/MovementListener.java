package dev.ethan.useful.listeners;

import dev.ethan.useful.Main;
import dev.ethan.useful.managers.RuntimeManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MovementListener implements Listener {
    private final RuntimeManager runtimeManager = Main.getInstance().getRuntimeManager();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (runtimeManager.isFrozen(p.getName())) {
            e.setTo(e.getFrom());
        }
    }

    public void freezePlayer(String name) {
        runtimeManager.freezePlayer(name);
    }

    public void unfreezePlayer(String name) {
        runtimeManager.unfreezePlayer(name);
    }
}
