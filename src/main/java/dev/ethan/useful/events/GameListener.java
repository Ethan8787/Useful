package dev.ethan.useful.events;

import dev.ethan.useful.Main;
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
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

import static dev.ethan.useful.Main.*;
import static dev.ethan.useful.utils.IPTrackerUtils.saveIpsFile;
import static dev.ethan.useful.utils.LuckPermsUtils.getPlayerPrefix;
import static dev.ethan.useful.utils.LuckPermsUtils.getPlayerSuffix;
import static dev.ethan.useful.utils.MessageUtils.config;
import static dev.ethan.useful.utils.PlayerUtils.getUUID;
import static dev.ethan.useful.utils.PlayerUtils.shoot;
import static dev.ethan.useful.utils.TranslationUtil.getDeathMessageByCause;
import static dev.ethan.useful.utils.ValorantAceUtils.playKillSound;

public class GameListener implements Listener {
    private static final Map<UUID, Long> shootCooldown = new HashMap<>();
    private static final long SHOOT_COOLDOWN_MS = 100;
    public static final Map<UUID, Location> deathLocation = new HashMap<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
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
        if (p.hasPermission("useful.admin")) {
            p.playSound(p.getLocation(), Sound.ENTITY_GUARDIAN_DEATH,1.0f, 1.0f);
        } else {
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        }
        ipsConfig.set(playerName, ipList);
        saveIpsFile();
        if (nick != null) {
            UUID skinUUID;
            try {
                skinUUID = getUUID(nick);
            } catch (Exception e) {
                skinUUID = null;
            }

            Disguise disguise = Disguise.builder()
                    .setName(nick)
                    .setSkin(SkinAPI.MOJANG, Objects.requireNonNullElseGet(skinUUID, p::getUniqueId))
                    .setEntityType(EntityType.PLAYER)
                    .build();

            DisguiseManager.getProvider().disguise(p, disguise);
            p.setDisplayName(nick);
            p.setPlayerListName(nick);

            p.setMetadata("nicked", new FixedMetadataValue(Main.getInstance(), true));
            event.setJoinMessage("§8[§d+§8] " + prefix + nick + suffix);
            sendActionBar(p, "§d» §f歡迎回來 " + prefix + nick + suffix + " §d«", 7);
            return;
        }
        if (event.getPlayer().hasPlayedBefore()) {
            event.setJoinMessage(two);
        } else {
            event.setJoinMessage(one);
        }
        sendActionBar(p, three, 7);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (frozenPlayers.contains(event.getPlayer().getName())) {
            event.setTo(event.getFrom());
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null) return;

        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return;

        String itemName = item.getItemMeta().getDisplayName();
        Action action = event.getAction();

        if (item.getType() == Material.BOW) {
            if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
                launchPlayer(player, 1.2);
            }
        }

        if (item.getType() == Material.FEATHER) {
            if (ChatColor.stripColor(itemName).equalsIgnoreCase(ChatColor.stripColor(VandalName))) {
                if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
                    if (!canShoot(player)) {
                        return;
                    }
                    shoot(player);
                    event.setCancelled(true);
                    return;
                }
            }

            if (ChatColor.stripColor(itemName).equalsIgnoreCase(ChatColor.stripColor(FeatherName))) {
                if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                    launchPlayer(player, 2.5);
                }
            }
        }

    }

    public static boolean canShoot(Player player) {
        long now = System.currentTimeMillis();
        long lastShot = shootCooldown.getOrDefault(player.getUniqueId(), 0L);

        if (now - lastShot < SHOOT_COOLDOWN_MS) {
            return false;
        }

        shootCooldown.put(player.getUniqueId(), now);
        return true;
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Snowball snowball)) return;
        if (!(snowball.getShooter() instanceof Player player)) return;
        if (!"GunBullet".equals(snowball.getCustomName())) return;

        Entity hit = event.getHitEntity();
        if (hit == null) return;

        if (hit instanceof EnderDragonPart part) {
            EnderDragon dragon = (EnderDragon) part.getParent();
            dragon.damage(40.0, player);
            return;
        }

        if (hit instanceof EnderCrystal crystal) {
            crystal.getWorld().createExplosion(crystal.getLocation(), 6F);
            crystal.remove();
            return;
        }

        if (hit instanceof LivingEntity target) {
            double hitY = snowball.getLocation().getY();
            double targetY = target.getLocation().getY();
            double targetHeight = target.getHeight();
            double relativeY = hitY - targetY;

            boolean isHeadshot = relativeY > targetHeight * 0.70;
            double damage = isHeadshot ? 40 : 10;

            target.damage(damage, player);
            target.setNoDamageTicks(0);
            target.getWorld().spawnParticle(Particle.CRIT, target.getLocation().add(0, targetHeight / 2, 0), 10, 0.1, 0.1, 0.1, 0.1);

            if (isHeadshot) {
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 2f);
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 2f);
            } else {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1f, 1.1f);
                player.playSound(player.getLocation(), Sound.BLOCK_STONE_BREAK, 1f, 1.1f);
            }

            return;
        }

        if (hit instanceof Damageable damageable) {
            damageable.damage(999.0, player);
        }
    }

    @EventHandler
    public void onSnowballDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Snowball snowball && "GunBullet".equals(snowball.getCustomName())) {
            event.setCancelled(true);
        }
    }

    private void launchPlayer(Player player, double power) {
        Location loc = player.getLocation();
        float yaw = loc.getYaw();
        float pitch = loc.getPitch();
        Vector direction = new Vector(
                -Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)),
                -Math.sin(Math.toRadians(pitch)),
                Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch))
        );
        player.setVelocity(direction.multiply(power));
        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1f, 1.2f);
        player.getWorld().playEffect(player.getLocation(), Effect.SMOKE, 0);
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
        Player player = e.getEntity();
        if (killStreaks.containsKey(player.getUniqueId())) {
            killStreaks.remove(player.getUniqueId());
        }
        EntityDamageEvent lastDamageCause = player.getLastDamageCause();
        Entity killer = (lastDamageCause instanceof EntityDamageByEntityEvent) ? ((EntityDamageByEntityEvent) lastDamageCause).getDamager() : null;

        if (killer instanceof Projectile projectile && projectile.getShooter() instanceof Entity shooter) {
            killer = shooter;
        }

        String playerPrefix = getPlayerPrefix(player);
        Component dm;

        if (killer instanceof Player playerKiller) {
            String bPrefix = getPlayerPrefix(playerKiller);
            ItemStack weapon = playerKiller.getInventory().getItemInMainHand();
            String weaponName = TranslationUtil.getCustomTranslatedItemName(weapon);
            NamedTextColor weaponColor = NamedTextColor.WHITE;
            if (weapon.hasItemMeta() && Objects.requireNonNull(weapon.getItemMeta()).hasDisplayName()) {
                weaponColor = NamedTextColor.WHITE;
            }
            dm = Component.text("§4死亡", NamedTextColor.DARK_RED)
                    .append(Component.text(" §7» ", NamedTextColor.GRAY))
                    .append(Component.text(playerPrefix + player.getDisplayName(), NamedTextColor.WHITE))
                    .append(Component.text(" §f被 ", NamedTextColor.WHITE))
                    .append(Component.text(bPrefix + playerKiller.getDisplayName(), NamedTextColor.WHITE))
                    .append(weaponName.isEmpty() ?
                        Component.text(" §f殺死了", NamedTextColor.WHITE) :
                        Component.text(" §f用 ", NamedTextColor.WHITE)
                            .append(Component.text(weaponName, weaponColor))
                            .append(Component.text(" §f殺死了", NamedTextColor.WHITE))
                    );
        } else if (killer != null) {
            dm = Component.text("§4死亡", NamedTextColor.DARK_RED)
                    .append(Component.text(" §7» ", NamedTextColor.GRAY))
                    .append(Component.text(playerPrefix + player.getDisplayName(), NamedTextColor.WHITE))
                    .append(Component.text(" §f被 ", NamedTextColor.WHITE))
                    .append(Component.text(TranslationUtil.getCustomTranslatedEntityName(killer), NamedTextColor.GRAY))
                    .append(Component.text(" §f殺死了", NamedTextColor.WHITE));
        } else {
            dm = Component.text((lastDamageCause != null) ?
                    getDeathMessageByCause(playerPrefix, player.getDisplayName(), lastDamageCause.getCause()) :
                    "§4死亡 §7» " + playerPrefix + player.getDisplayName() + " §f死亡", NamedTextColor.WHITE);
        }
        Location loc = player.getLocation().clone();
        deathLocation.put(player.getUniqueId(), loc);

        e.deathMessage(dm);
    }

    @EventHandler
    public void onPlayerKill(PlayerDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer != null && killer.hasPermission("useful.killeffect")) {
            killer.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, event.getEntity().getLocation(), 50, 0.5, 1, 0.5, 0.1);
            UUID killerId = killer.getUniqueId();
            int kills = killStreaks.getOrDefault(killerId, 0) + 1;
            killStreaks.put(killerId, kills);
            playKillSound(killer, kills);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String prefix = getPlayerPrefix(player);
        String suffix = getPlayerSuffix(player);
        String message = ChatColor.translateAlternateColorCodes('&',ChatColor.DARK_GRAY + "[" + ChatColor.RED + "-" + ChatColor.DARK_GRAY + "] " + prefix + player.getDisplayName() + suffix);
        event.setQuitMessage(message);
    }

    private void sendActionBar(final Player player, final String message, final double duration) {
        (new BukkitRunnable() {
            double count = 0;
            final double maxCount = duration * 20 / 10;

            public void run() {
                if (this.count < this.maxCount && player.isOnline()) {
                    player.sendActionBar(Component.text(message));
                    ++this.count;
                } else {
                    this.cancel();
                }
            }
        }).runTaskTimer(Main.getInstance(), 0L, 10L);
    }
}
