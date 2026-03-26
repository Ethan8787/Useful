package dev.ethan.useful.listeners;

import dev.ethan.useful.Main;
import dev.ethan.useful.managers.GameManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import top.nontage.nontagelib.annotations.AutoListener;

@AutoListener
public class MovementListener implements Listener {
    private final GameManager gameManager = Main.getInstance().getGameManager();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (gameManager.isFrozen(p.getName())) {
            e.setTo(e.getFrom());
        }
    }
}
