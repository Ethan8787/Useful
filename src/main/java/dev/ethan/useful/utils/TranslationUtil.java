package dev.ethan.useful.utils;

import dev.ethan.useful.managers.GameManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class TranslationUtil {

    public Component buildDeathMessage(
            LuckPermsUtil luckPermsUtil,
            GameManager gameManager,
            Player victim,
            EntityDamageEvent event
    ) {

        Component victimName = buildPlayerName(luckPermsUtil, victim);

        if (event == null) {
            return Component.translatable("death.attack.generic", victimName);
        }

        EntityDamageEvent.DamageCause cause = event.getCause();

        if (event instanceof EntityDamageByEntityEvent byEntity) {

            KillerContext ctx = resolveKiller(byEntity, gameManager);
            Entity damager = ctx.directDamager();
            Entity attacker = ctx.attacker() != null ? ctx.attacker() : damager;

            if (cause == EntityDamageEvent.DamageCause.PROJECTILE && damager instanceof Projectile projectile) {

                Entity shooter = projectile.getShooter() instanceof Entity e ? e : null;
                Component killerName = shooter != null
                        ? entityName(luckPermsUtil, shooter)
                        : entityName(luckPermsUtil, attacker);

                String key = projectileKey(projectile);
                if (key != null) {

                    if ("death.attack.arrow".equals(key) && shooter instanceof LivingEntity le) {
                        Component namedItem = namedItem(
                                le.getEquipment() != null
                                        ? le.getEquipment().getItemInMainHand()
                                        : null
                        );

                        if (namedItem != null) {
                            return Component.translatable(
                                    "death.attack.arrow.item",
                                    victimName,
                                    killerName,
                                    namedItem
                            );
                        }
                    }

                    return Component.translatable(key, victimName, killerName);
                }
            }

            if (cause == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION
                    || cause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {

                Component killerName = attacker != null
                        ? entityName(luckPermsUtil, attacker)
                        : null;

                if (killerName != null) {
                    return Component.translatable(
                            "death.attack.explosion.player",
                            victimName,
                            killerName
                    );
                }

                return Component.translatable("death.attack.explosion", victimName);
            }

            if (attacker instanceof Player killerPlayer) {

                Component killerName = buildPlayerName(luckPermsUtil, killerPlayer);
                Component namedItem = namedItem(
                        killerPlayer.getInventory().getItemInMainHand()
                );

                if (namedItem != null) {
                    return Component.translatable(
                            "death.attack.player.item",
                            victimName,
                            killerName,
                            namedItem
                    );
                }

                return Component.translatable(
                        "death.attack.player",
                        victimName,
                        killerName
                );
            }

            if (damager instanceof Tameable tameable
                    && tameable.getOwner() instanceof Player owner) {

                Component ownerName = buildPlayerName(luckPermsUtil, owner);
                Component petName = entityName(luckPermsUtil, damager);
                Component killerName = ownerName
                        .append(Component.text(" 的 ", NamedTextColor.WHITE))
                        .append(petName);

                return Component.translatable(
                        "death.attack.mob",
                        victimName,
                        killerName
                );
            }

            if (attacker != null) {

                Component killerName = entityName(luckPermsUtil, attacker);

                if (cause == EntityDamageEvent.DamageCause.SONIC_BOOM) {
                    return Component.translatable(
                            "death.attack.sonic_boom",
                            victimName,
                            killerName
                    );
                }

                return Component.translatable(
                        "death.attack.mob",
                        victimName,
                        killerName
                );
            }
        }

        String key = nonEntityCauseKey(cause);

        if (key != null) {
            return Component.translatable(key, victimName);
        }

        return Component.translatable("death.attack.generic", victimName);
    }

    private KillerContext resolveKiller(EntityDamageByEntityEvent ev, GameManager gameManager) {

        Entity damager = ev.getDamager();

        if (damager instanceof EnderCrystal crystal) {
            UUID cid = crystal.getUniqueId();
            UUID shooterUUID = gameManager.lastCrystalShooter.get(cid);

            if (shooterUUID != null) {
                Player shooter = Bukkit.getPlayer(shooterUUID);
                if (shooter != null) return new KillerContext(crystal, shooter);
            }

            return new KillerContext(crystal, crystal);
        }

        if (damager instanceof TNTPrimed tnt) {
            Entity src = tnt.getSource() instanceof Entity e ? e : null;
            return new KillerContext(tnt, src != null ? src : tnt);
        }

        if (damager instanceof Projectile projectile) {
            Entity shooter = projectile.getShooter() instanceof Entity e ? e : null;
            return new KillerContext(projectile, shooter != null ? shooter : projectile);
        }

        if (damager instanceof Tameable tameable) {
            Entity owner = tameable.getOwner() instanceof Entity e ? e : null;
            return new KillerContext(tameable, owner != null ? owner : tameable);
        }

        return new KillerContext(damager, damager);
    }

    private Component buildPlayerName(LuckPermsUtil luckPermsUtil, Player p) {
        String prefix = luckPermsUtil.getPrefix(p);

        Component prefixComponent =
                LegacyComponentSerializer.legacyAmpersand().deserialize(prefix);

        TextColor color = prefixComponent.color();
        if (color == null) color = TextColor.color(0xFFFFFF);

        Component nameComponent =
                Component.text(p.getName()).color(color);

        return prefixComponent.append(nameComponent);
    }

    private Component entityName(LuckPermsUtil luckPermsUtil, Entity e) {
        if (e instanceof Player p) return buildPlayerName(luckPermsUtil, p);
        Component displayName = e.name();
        return displayName.colorIfAbsent(NamedTextColor.WHITE);
    }

    private Component namedItem(ItemStack item) {

        if (item == null) return null;
        if (item.getType().isAir()) return null;
        if (!item.hasItemMeta()) return null;

        Component display = item.getItemMeta().displayName();
        if (display == null) return null;

        return display.colorIfAbsent(NamedTextColor.WHITE);
    }

    private String projectileKey(Projectile projectile) {
        return switch (projectile) {
            case Arrow ignored -> "death.attack.arrow";
            case Trident ignored -> "death.attack.trident";
            case WitherSkull ignored -> "death.attack.witherSkull";
            case Fireball ignored -> "death.attack.fireball";
            default -> null;
        };
    }

    private String nonEntityCauseKey(EntityDamageEvent.DamageCause cause) {
        return switch (cause) {
            case DROWNING -> "death.attack.drown";
            case FALL -> "death.attack.fall";
            case FIRE -> "death.attack.inFire";
            case FIRE_TICK -> "death.attack.onFire";
            case LAVA -> "death.attack.lava";
            case VOID -> "death.attack.outOfWorld";
            case LIGHTNING -> "death.attack.lightningBolt";
            case SUFFOCATION -> "death.attack.inWall";
            case STARVATION -> "death.attack.starve";
            case MAGIC -> "death.attack.magic";
            case WITHER -> "death.attack.wither";
            case FLY_INTO_WALL -> "death.attack.flyIntoWall";
            case FALLING_BLOCK -> "death.attack.fallingBlock";
            case FREEZE -> "death.attack.freeze";
            case CRAMMING -> "death.attack.cramming";
            case SONIC_BOOM -> "death.attack.sonic_boom";
            case CONTACT -> "death.attack.cactus";
            case HOT_FLOOR -> "death.attack.hotFloor";
            case WORLD_BORDER -> "death.attack.worldBorder";
            case KILL, SUICIDE -> "death.attack.generic";
            default -> null;
        };
    }

    public record KillerContext(Entity directDamager, Entity attacker) {}
}