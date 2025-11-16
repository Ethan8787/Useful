package dev.ethan.useful.listeners;

import dev.ethan.useful.Main;
import dev.ethan.useful.managers.RuntimeManager;
import dev.ethan.useful.utils.LuckPermsUtil;
import dev.ethan.useful.utils.TranslationUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListener implements Listener {

    private final LuckPermsUtil luckPermsUtil = Main.getInstance().getLuckPermsUtil();
    private final TranslationUtil translationUtil = Main.getInstance().getTranslationUtil();
    private final RuntimeManager runtimeManager = Main.getInstance().getRuntimeManager();

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        if (runtimeManager.getKillStreak(p.getUniqueId()) > 0) {
            runtimeManager.resetKillStreak(p.getUniqueId());
        }
        EntityDamageEvent lastDamageCause = p.getLastDamageCause();
        Entity killer = (lastDamageCause instanceof EntityDamageByEntityEvent)
                ? ((EntityDamageByEntityEvent) lastDamageCause).getDamager()
                : null;
        if (killer instanceof Projectile projectile && projectile.getShooter() instanceof Entity shooter) {
            killer = shooter;
        }
        String playerPrefix = luckPermsUtil.getPlayerPrefix(p);
        Component dm;
        if (killer instanceof Player k) {
            String bPrefix = luckPermsUtil.getPlayerPrefix(k);
            var weapon = k.getInventory().getItemInMainHand();
            String name = translationUtil.getCustomTranslatedItemName(weapon);
            NamedTextColor weaponColor = NamedTextColor.WHITE;
            if (weapon.hasItemMeta() && weapon.getItemMeta() != null && weapon.getItemMeta().hasDisplayName()) {
                weaponColor = NamedTextColor.WHITE;
            }
            dm = Component.text("§4死亡", NamedTextColor.DARK_RED)
                    .append(Component.text(" §7» ", NamedTextColor.GRAY))
                    .append(Component.text(playerPrefix + p.getDisplayName(), NamedTextColor.WHITE))
                    .append(Component.text(" §f被 ", NamedTextColor.WHITE))
                    .append(Component.text(bPrefix + k.getDisplayName(), NamedTextColor.WHITE))
                    .append(name.isEmpty()
                            ? Component.text(" §f殺死了", NamedTextColor.WHITE)
                            : Component.text(" §f用 ", NamedTextColor.WHITE)
                            .append(Component.text(name, weaponColor))
                            .append(Component.text(" §f殺死了", NamedTextColor.WHITE))
                    );
        } else if (killer != null) {
            dm = Component.text("§4死亡", NamedTextColor.DARK_RED)
                    .append(Component.text(" §7» ", NamedTextColor.GRAY))
                    .append(Component.text(playerPrefix + p.getDisplayName(), NamedTextColor.WHITE))
                    .append(Component.text(" §f被 ", NamedTextColor.WHITE))
                    .append(Component.text(translationUtil.getCustomTranslatedEntityName(killer), NamedTextColor.GRAY))
                    .append(Component.text(" §f殺死了", NamedTextColor.WHITE));
        } else {
            dm = Component.text(
                    (lastDamageCause != null)
                            ? translationUtil.getDeathMessageByCause(
                            playerPrefix,
                            p.getDisplayName(),
                            lastDamageCause.getCause()
                    )
                            : "§4死亡 §7» " + playerPrefix + p.getDisplayName() + " §f死亡",
                    NamedTextColor.WHITE
            );
        }
        Location loc = p.getLocation().clone();
        runtimeManager.setDeathLocation(p.getUniqueId(), loc);
        e.deathMessage(dm);
    }
}