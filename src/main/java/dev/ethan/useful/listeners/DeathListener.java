package dev.ethan.useful.listeners;

import dev.ethan.useful.Main;
import dev.ethan.useful.managers.GameManager;
import dev.ethan.useful.utils.LuckPermsUtil;
import dev.ethan.useful.utils.TranslationUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import top.nontage.nontagelib.annotations.AutoListener;

import java.util.UUID;

@AutoListener
public class DeathListener implements Listener {

    private final LuckPermsUtil luckPermsUtil = Main.getInstance().getLuckPermsUtil();
    private final TranslationUtil translationUtil = Main.getInstance().getTranslationUtil();
    private final GameManager gameManager = Main.getInstance().getGameManager();

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player victim = e.getEntity();
        UUID vUUID = victim.getUniqueId();

        gameManager.resetKillStreak(vUUID);

        EntityDamageEvent damageEvent = victim.getLastDamageCause();

        Component message = translationUtil.buildDeathMessage(
                luckPermsUtil,
                gameManager,
                victim,
                damageEvent
        );

        Location loc = victim.getLocation().clone();
        gameManager.setDeathLocation(vUUID, loc);

        e.deathMessage(message);
    }
}