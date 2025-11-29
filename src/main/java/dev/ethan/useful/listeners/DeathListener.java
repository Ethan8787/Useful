package dev.ethan.useful.listeners;

import dev.ethan.useful.Main;
import dev.ethan.useful.managers.GameManager;
import dev.ethan.useful.utils.LuckPermsUtil;
import dev.ethan.useful.utils.TranslationUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

import java.util.UUID;

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
        Entity killer = resolveKiller(damageEvent);
        Component message = buildDeathMessage(victim, killer, damageEvent);
        Location loc = victim.getLocation().clone();
        gameManager.setDeathLocation(vUUID, loc);
        e.deathMessage(message);
    }

    private Entity resolveKiller(EntityDamageEvent cause) {
        if (!(cause instanceof EntityDamageByEntityEvent ev)) return null;
        Entity damager = ev.getDamager();
        return switch (damager) {
            case Trident trident -> {
                if (trident.getShooter() instanceof Entity shooterEntity) {
                    yield shooterEntity;
                }
                yield trident;
            }
            case Projectile projectile -> {
                ProjectileSource shooter = projectile.getShooter();
                if (shooter instanceof Entity shooterEntity) {
                    yield shooterEntity;
                }
                yield projectile;
            }
            case Tameable tameable -> {
                if (tameable.getOwner() instanceof Entity owner) {
                    yield owner;
                }
                yield tameable;
            }
            default -> damager;
        };
    }

    private Component buildDeathMessage(Player victim, Entity killer, EntityDamageEvent cause) {
        Component victimName = buildPlayerName(victim);
        if (killer instanceof Player killerP) {
            Component killerName = buildPlayerName(killerP);
            Component weaponComponent = weaponNameComponent(killerP.getInventory().getItemInMainHand());
            if (weaponComponent == null) {
                return Component.text("死亡 ", NamedTextColor.DARK_RED)
                        .append(Component.text("» ", NamedTextColor.GRAY))
                        .append(victimName)
                        .append(Component.text(" 被 ", NamedTextColor.WHITE))
                        .append(killerName)
                        .append(Component.text(" 殺死了", NamedTextColor.WHITE));
            } else {
                return Component.text("死亡 ", NamedTextColor.DARK_RED)
                        .append(Component.text("» ", NamedTextColor.GRAY))
                        .append(victimName)
                        .append(Component.text(" 被 ", NamedTextColor.WHITE))
                        .append(killerName)
                        .append(Component.text(" 用 ", NamedTextColor.WHITE))
                        .append(weaponComponent)
                        .append(Component.text(" 殺死了", NamedTextColor.WHITE));
            }
        }
        if (killer != null) {
            Component mobName = Component.text(
                    translationUtil.getCustomTranslatedEntityName(killer),
                    NamedTextColor.GRAY
            );
            String verb = "殺死了";
            if (killer instanceof Creeper) {
                verb = "炸死了";
            }
            if (killer instanceof Skeleton ||
                    killer instanceof Stray ||
                    killer instanceof WitherSkeleton ||
                    killer instanceof Pillager ||
                    killer instanceof Illusioner) {
                verb = "射死了";
            }
            if (cause instanceof EntityDamageByEntityEvent ev &&
                    ev.getDamager() instanceof Projectile &&
                    !(killer instanceof Creeper)) {
                verb = "射死了";
            }
            return Component.text("死亡 ", NamedTextColor.DARK_RED)
                    .append(Component.text("» ", NamedTextColor.GRAY))
                    .append(victimName)
                    .append(Component.text(" 被 ", NamedTextColor.WHITE))
                    .append(mobName)
                    .append(Component.text(" " + verb, NamedTextColor.WHITE));
        }
        return Component.text(
                translationUtil.getDeathMessageByCause(
                        luckPermsUtil.getPlayerPrefix(victim),
                        victim.getName(),
                        cause.getCause()
                )
        );
    }

    private Component buildPlayerName(Player p) {
        String prefix = luckPermsUtil.getPlayerPrefix(p);
        return Component.text(prefix, NamedTextColor.WHITE)
                .append(Component.text(p.getName(), NamedTextColor.WHITE));
    }

    private Component weaponNameComponent(ItemStack item) {
        if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return null;
        NamedTextColor color = NamedTextColor.WHITE;
        String displayName = item.getItemMeta().getDisplayName();

        String translated = translationUtil.getCustomTranslatedItemName(item);
        if (translated == null || translated.isEmpty()) translated = displayName;
        return Component.text(translated, color);
    }
}