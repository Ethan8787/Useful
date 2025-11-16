package dev.ethan.useful.listeners;

import dev.ethan.useful.Main;
import dev.ethan.useful.constants.Messages;
import dev.ethan.useful.utils.PlayerUtil;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class WeaponListener implements Listener {
    private final PlayerUtil playerUtil = Main.getInstance().getPlayerUtil();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        ItemStack i = e.getItem();
        if (i == null) return;
        if (!i.hasItemMeta() || !i.getItemMeta().hasDisplayName()) return;
        String itemName = i.getItemMeta().getDisplayName();
        Action action = e.getAction();
        if (i.getType() == Material.BOW) {
            if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
                launchPlayer(p, 1.2);
            }
        }
        if (i.getType() == Material.FEATHER) {
            if (itemName.equalsIgnoreCase(Messages.VANDAL_NAME)) {
                if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
                    playerUtil.shoot(p);
                    e.setCancelled(true);
                    return;
                }
            }
            if (itemName.equalsIgnoreCase(Messages.FEATHER_NAME)) {
                if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                    launchPlayer(p, 2.5);
                }
            }
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent e) {
        if (!(e.getEntity() instanceof Snowball s)) return;
        if (!(s.getShooter() instanceof Player p)) return;
        if (!"GunBullet".equals(s.getCustomName())) return;
        Entity hit = e.getHitEntity();
        if (hit == null) return;
        if (hit instanceof EnderDragonPart part) {
            EnderDragon dragon = part.getParent();
            dragon.damage(40.0, p);
            return;
        }
        if (hit instanceof EnderCrystal crystal) {
            crystal.getWorld().createExplosion(crystal.getLocation(), 6F);
            crystal.remove();
            return;
        }
        if (hit instanceof LivingEntity target) {
            double hitY = s.getLocation().getY();
            double targetY = target.getLocation().getY();
            double targetHeight = target.getHeight();
            double relativeY = hitY - targetY;
            boolean isHeadshot = relativeY > targetHeight * 0.70;
            double damage = isHeadshot ? 40 : 10;
            target.damage(damage, p);
            target.setNoDamageTicks(0);
            target.getWorld().spawnParticle(Particle.CRIT, target.getLocation().add(0, targetHeight / 2, 0), 10, 0.1, 0.1, 0.1, 0.1);
            if (isHeadshot) {
                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 2f);
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 2f);
            } else {
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1f, 1.1f);
                p.playSound(p.getLocation(), Sound.BLOCK_STONE_BREAK, 1f, 1.1f);
            }
            return;
        }
        if (hit instanceof Damageable damageable) {
            damageable.damage(999.0, p);
        }
    }

    @EventHandler
    public void onSnowballDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Snowball snowball && "GunBullet".equals(snowball.getCustomName())) {
            e.setCancelled(true);
        }
    }

    private void launchPlayer(Player p, double power) {
        Location loc = p.getLocation();
        float yaw = loc.getYaw();
        float pitch = loc.getPitch();

        Vector direction = new Vector(
                -Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)),
                -Math.sin(Math.toRadians(pitch)),
                Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch))
        );

        p.setVelocity(direction.multiply(power));
        p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1f, 1.2f);
        p.getWorld().playEffect(p.getLocation(), Effect.SMOKE, 0);
    }
}