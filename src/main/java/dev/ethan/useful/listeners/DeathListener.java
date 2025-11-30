package dev.ethan.useful.listeners;

import dev.ethan.useful.Main;
import dev.ethan.useful.constants.Messages;
import dev.ethan.useful.managers.GameManager;
import dev.ethan.useful.utils.LuckPermsUtil;
import dev.ethan.useful.utils.TranslationUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

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
        Component message = buildDeathMessage(victim, damageEvent);
        Location loc = victim.getLocation().clone();
        gameManager.setDeathLocation(vUUID, loc);
        e.deathMessage(message);
    }

    private KillerInfo resolveKiller(EntityDamageEvent cause) {
        if (!(cause instanceof EntityDamageByEntityEvent ev)) return null;
        Entity damager = ev.getDamager();
        switch (damager) {
            case EnderCrystal crystal -> {
                UUID cid = crystal.getUniqueId();
                UUID shooterUUID = gameManager.lastCrystalShooter.get(cid);
                if (shooterUUID != null) {
                    Player shooter = Bukkit.getPlayer(shooterUUID);
                    return new KillerInfo(shooter != null ? shooter : crystal, null);
                }
                return new KillerInfo(crystal, null);
            }
            case TNTPrimed tnt -> {
                Entity src = tnt.getSource() instanceof Entity e ? e : null;
                return new KillerInfo(tnt, src);
            }
            case Trident trident -> {
                Entity shooter = trident.getShooter() instanceof Entity e ? e : null;
                return new KillerInfo(trident, shooter);
            }
            case Projectile projectile -> {
                Entity shooter = projectile.getShooter() instanceof Entity e ? e : null;
                return new KillerInfo(projectile, shooter);
            }
            case Tameable tameable -> {
                Entity owner = tameable.getOwner() instanceof Entity e ? e : null;
                return new KillerInfo(tameable, owner);
            }
            default -> {
                return new KillerInfo(damager, null);
            }
        }
    }

    private Component buildDeathMessage(Player victim, EntityDamageEvent event) {
        Component victimName = buildPlayerName(victim);
        EntityDamageEvent.DamageCause cause = event.getCause();
        KillerInfo info = resolveKiller(event);
        if (info != null) {
            Entity damager = info.actualKiller();
            Entity source = info.explosionSource();
            if (cause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION || cause == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) {
                String verb = cause == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION ? "炸飛了" : "炸死了";
                if (damager instanceof Player killer) return simple(victimName, buildPlayerName(killer), verb);
                if (damager instanceof TNTPrimed) {
                    if (source instanceof Player p) return simple(victimName, buildPlayerName(p), verb);
                    return simple(victimName, Component.text("TNT", NamedTextColor.GRAY), verb);
                }
                if (damager instanceof EnderCrystal) {
                    UUID cid = (damager).getUniqueId();
                    UUID tapperUUID = gameManager.lastCrystalShooter.get(cid);
                    if (tapperUUID != null) {
                        Player tap = Bukkit.getPlayer(tapperUUID);
                        if (tap != null) return simple(victimName, buildPlayerName(tap), verb);
                    }
                    return simple(victimName, Component.text("終界水晶", NamedTextColor.GRAY), verb);
                }
                if (damager instanceof Creeper) {
                    Component mob = translationUtil.getCustomTranslatedEntityName(damager);
                    return simple(victimName, mob, verb);
                }
            }
            if (cause == EntityDamageEvent.DamageCause.PROJECTILE) {
                String verb = "射殺了";
                if (damager instanceof Projectile) {
                    Entity shooter = ((Projectile) damager).getShooter() instanceof Entity e ? e : null;
                    assert shooter != null;
                    return simple(victimName, translationUtil.getCustomTranslatedEntityName(shooter), verb);
                }
            }
            if (damager instanceof Player killer) {
                if (cause == EntityDamageEvent.DamageCause.PROJECTILE)
                    return simple(victimName, buildPlayerName(killer), "射殺了");
                Component weapon = weaponNameComponent(killer.getInventory().getItemInMainHand());
                if (weapon == null)
                    return simple(victimName, buildPlayerName(killer), "殺死了");
                return withWeapon(victimName, buildPlayerName(killer), weapon);
            }
            if (damager instanceof Projectile && source instanceof Player p)
                return simple(victimName, buildPlayerName(p), "射殺了");
            if (damager instanceof Tameable && source instanceof Player p)
                return simple(victimName, buildPlayerName(p), "的 " + damager.getName() + " 殺死了");
            Component mob = translationUtil.getCustomTranslatedEntityName(damager);
            return simple(victimName, mob, "殺死了");
        }
        return translationUtil.getDeathMessageByCause(luckPermsUtil.getPlayerPrefix(victim), victim.getName(), cause);
    }

    private Component simple(Component victim, Component killer, String verb) {
        return Component.text("死亡 ", NamedTextColor.DARK_RED)
                .append(Component.text("» ", NamedTextColor.GRAY))
                .append(victim)
                .append(Component.text(" 被 ", NamedTextColor.WHITE))
                .append(killer)
                .append(Component.text(" " + verb, NamedTextColor.WHITE));
    }

    private Component withWeapon(Component victim, Component killer, Component weapon) {
        return Component.text("死亡 ", NamedTextColor.DARK_RED)
                .append(Component.text("» ", NamedTextColor.GRAY))
                .append(victim)
                .append(Component.text(" 被 ", NamedTextColor.WHITE))
                .append(killer)
                .append(Component.text(" 用 ", NamedTextColor.WHITE))
                .append(weapon)
                .append(Component.text(" 殺死了", NamedTextColor.WHITE));
    }

    private Component buildPlayerName(Player p) {
        String prefix = luckPermsUtil.getPlayerPrefix(p);
        Component prefixComponent = net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand().deserialize(prefix);
        TextColor color = prefixComponent.color();
        if (color == null) color = TextColor.color(0xFFFFFF);
        Component nameComponent = Component.text(p.getName()).color(color);
        return prefixComponent.append(nameComponent);
    }

    private Component weaponNameComponent(ItemStack item) {
        if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return null;
        String displayName = item.getItemMeta().getDisplayName();
        if (displayName.equals(Messages.VANDAL_NAME)) return null;
        return Component.text(displayName, NamedTextColor.WHITE);
    }

    public record KillerInfo(Entity actualKiller, Entity explosionSource) {}
}
