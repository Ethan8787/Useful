package dev.ethan.useful.events;

import dev.ethan.useful.Main;
import dev.ethan.useful.utils.IPTrackerUtil;
import dev.ethan.useful.utils.TranslationUtil;
import dev.iiahmed.disguise.Disguise;
import dev.iiahmed.disguise.DisguiseManager;
import dev.iiahmed.disguise.SkinAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.*;

import static dev.ethan.useful.Main.*;
import static dev.ethan.useful.utils.LuckPermsUtil.getPlayerPrefix;
import static dev.ethan.useful.utils.LuckPermsUtil.getPlayerSuffix;
import static dev.ethan.useful.utils.MessageUtil.config;
import static dev.ethan.useful.utils.PlayerUtil.getUUID;
import static dev.ethan.useful.utils.PlayerUtil.shoot;
import static dev.ethan.useful.utils.TranslationUtil.getDeathMessageByCause;
import static dev.ethan.useful.utils.AceUtil.playKillSound;

public class GameListener implements Listener {
    private static final Map<UUID, Long> shootCooldown = new HashMap<>();
    private static final long SHOOT_COOLDOWN_MS = 100;
    public static final Map<UUID, Location> deathLocation = new HashMap<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        String prefix = getPlayerPrefix(p);
        String suffix = getPlayerSuffix(p);
        UUID uuid = p.getUniqueId();
        String nick = nickStorage.getNickname(p);
        String one = "§8[§d+§8] " + prefix + p.getDisplayName() + suffix;
        String two = "§8[§a+§8] " + prefix + p.getDisplayName() + suffix;
        String three = "§d» §f歡迎回來 " + prefix + p.getDisplayName() + suffix + " §d«";
        String playerName = p.getName();
        String ipAddress = Objects.requireNonNull(p.getAddress()).getAddress().getHostAddress();
        List<String> ipList = ipsConfig.getStringList(playerName);
        if (config.getBoolean("listeners." + uuid, false)) {
            dmListeners.add(uuid);
            p.sendMessage(Plugin_Prefix + "§a您仍在監聽私訊。");
        }
        if (!ipList.contains(ipAddress)) {
            ipList.add(ipAddress);
        }
        if (p.isOp()) {
            p.playSound(p.getLocation(), Sound.ENTITY_GUARDIAN_DEATH, 1.0f, 1.0f);
        } else {
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        }
        ipsConfig.set(playerName, ipList);
        IPTrackerUtil.save();
        if (nick != null) {
            UUID skinUUID;
            try {
                skinUUID = getUUID(nick);
            } catch (Exception ex) {
                skinUUID = null;
            }
            Disguise d = Disguise.builder()
                    .setName(nick)
                    .setSkin(SkinAPI.MOJANG, Objects.requireNonNullElseGet(skinUUID, p::getUniqueId))
                    .setEntityType(EntityType.PLAYER)
                    .build();
            DisguiseManager.getProvider().disguise(p, d);
            p.setDisplayName(nick);
            p.setPlayerListName(nick);

            p.setMetadata("nicked", new FixedMetadataValue(Main.getInstance(), true));
            e.setJoinMessage("§8[§d+§8] " + prefix + nick + suffix);
            p.sendActionBar("§d» §f歡迎回來 " + prefix + nick + suffix + " §d«");
            return;
        }
        if (e.getPlayer().hasPlayedBefore()) {
            e.setJoinMessage(two);
        } else {
            e.setJoinMessage(one);
        }
        p.sendActionBar(three);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (frozenPlayers.contains(p.getName())) {
            e.setTo(e.getFrom());
        }
    }

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
            if (itemName.equalsIgnoreCase(VandalName)) {
                if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
                    if (!canShoot(p)) {
                        return;
                    }
                    shoot(p);
                    e.setCancelled(true);
                    return;
                }
            }
            if (itemName.equalsIgnoreCase(FeatherName)) {
                if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                    launchPlayer(p, 2.5);
                }
            }
        }
    }

    public static boolean canShoot(Player p) {
        long now = System.currentTimeMillis();
        long last = shootCooldown.getOrDefault(p.getUniqueId(), 0L);
        if (now - last < SHOOT_COOLDOWN_MS) {
            return false;
        }
        shootCooldown.put(p.getUniqueId(), now);
        return true;
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

    @EventHandler
    public void onAnvilUse(PrepareAnvilEvent e) {
        ItemStack result = e.getResult();
        if (result == null) return;
        ItemMeta meta = result.getItemMeta();
        if (meta == null) return;
        Component displayName = meta.displayName();
        if (displayName != null) {
            displayName = displayName.decoration(TextDecoration.ITALIC, false);
            meta.displayName(displayName);
            result.setItemMeta(meta);
            e.setResult(result);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        if (killStreaks.containsKey(p.getUniqueId())) {
            killStreaks.remove(p.getUniqueId());
        }
        EntityDamageEvent lastDamageCause = p.getLastDamageCause();
        Entity killer = (lastDamageCause instanceof EntityDamageByEntityEvent) ? ((EntityDamageByEntityEvent) lastDamageCause).getDamager() : null;
        if (killer instanceof Projectile projectile && projectile.getShooter() instanceof Entity shooter) {
            killer = shooter;
        }
        String playerPrefix = getPlayerPrefix(p);
        Component dm;
        if (killer instanceof Player k) {
            String bPrefix = getPlayerPrefix(k);
            ItemStack weapon = k.getInventory().getItemInMainHand();
            String name = TranslationUtil.getCustomTranslatedItemName(weapon);
            NamedTextColor weaponColor = NamedTextColor.WHITE;
            if (weapon.hasItemMeta() && Objects.requireNonNull(weapon.getItemMeta()).hasDisplayName()) {
                weaponColor = NamedTextColor.WHITE;
            }
            dm = Component.text("§4死亡", NamedTextColor.DARK_RED)
                    .append(Component.text(" §7» ", NamedTextColor.GRAY))
                    .append(Component.text(playerPrefix + p.getDisplayName(), NamedTextColor.WHITE))
                    .append(Component.text(" §f被 ", NamedTextColor.WHITE))
                    .append(Component.text(bPrefix + k.getDisplayName(), NamedTextColor.WHITE))
                    .append(name.isEmpty() ?
                            Component.text(" §f殺死了", NamedTextColor.WHITE) :
                            Component.text(" §f用 ", NamedTextColor.WHITE)
                                    .append(Component.text(name, weaponColor))
                                    .append(Component.text(" §f殺死了", NamedTextColor.WHITE))
                    );
        } else if (killer != null) {
            dm = Component.text("§4死亡", NamedTextColor.DARK_RED)
                    .append(Component.text(" §7» ", NamedTextColor.GRAY))
                    .append(Component.text(playerPrefix + p.getDisplayName(), NamedTextColor.WHITE))
                    .append(Component.text(" §f被 ", NamedTextColor.WHITE))
                    .append(Component.text(TranslationUtil.getCustomTranslatedEntityName(killer), NamedTextColor.GRAY))
                    .append(Component.text(" §f殺死了", NamedTextColor.WHITE));
        } else {
            dm = Component.text((lastDamageCause != null) ?
                    getDeathMessageByCause(playerPrefix, p.getDisplayName(), lastDamageCause.getCause()) :
                    "§4死亡 §7» " + playerPrefix + p.getDisplayName() + " §f死亡", NamedTextColor.WHITE);
        }
        Location loc = p.getLocation().clone();
        deathLocation.put(p.getUniqueId(), loc);
        e.deathMessage(dm);
    }

    @EventHandler
    public void onPlayerKill(PlayerDeathEvent e) {
        Player killer = e.getEntity().getKiller();
        if (killer != null && killer.hasPermission("useful.killeffect")) {
            killer.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, e.getEntity().getLocation(), 50, 0.5, 1, 0.5, 0.1);
            UUID killerId = killer.getUniqueId();
            int kills = killStreaks.getOrDefault(killerId, 0) + 1;
            killStreaks.put(killerId, kills);
            playKillSound(killer, kills);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        String prefix = getPlayerPrefix(p);
        String suffix = getPlayerSuffix(p);
        String name = p.getDisplayName();
        e.setQuitMessage("§8[§d+§8] " + prefix + name + suffix);
    }
}
