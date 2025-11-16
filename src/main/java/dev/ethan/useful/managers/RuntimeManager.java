package dev.ethan.useful.managers;

import org.bukkit.Location;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RuntimeManager {
    private final Set<String> frozenPlayers = ConcurrentHashMap.newKeySet();
    private final Map<UUID, Location> deathLocations = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> killStreaks = new ConcurrentHashMap<>();
    private final Map<UUID, Long> shootCooldown = new ConcurrentHashMap<>();
    public static final long SHOOT_COOLDOWN_MS = 100L;

    public boolean isFrozen(String name) {
        return frozenPlayers.contains(name);
    }

    public void freezePlayer(String name) {
        frozenPlayers.add(name);
    }

    public void unfreezePlayer(String name) {
        frozenPlayers.remove(name);
    }

    public void setDeathLocation(UUID uuid, Location location) {
        if (location == null) return;
        deathLocations.put(uuid, location.clone());
    }

    public Location getDeathLocation(UUID uuid) {
        return deathLocations.get(uuid);
    }

    public void resetKillStreak(UUID uuid) {
        killStreaks.remove(uuid);
    }

    public int increaseKillStreak(UUID uuid) {
        return killStreaks.merge(uuid, 1, Integer::sum);
    }

    public int getKillStreak(UUID uuid) {
        return killStreaks.getOrDefault(uuid, 0);
    }

    public void setLastShootTime(UUID uuid, long time) {
        shootCooldown.put(uuid, time);
    }

    public long getLastShootTime(UUID uuid) {
        return shootCooldown.getOrDefault(uuid, 0L);
    }
}
