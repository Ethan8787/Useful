package dev.ethan.useful.utils;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCDamageEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.LookClose;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class BotUtil {
    public static void spawnFakePlayer(JavaPlugin plugin, Location loc, String name, Player p) {
        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, name);
        npc.spawn(loc);
        npc.setName(name);
        npc.setProtected(false);
        if (npc.getEntity() instanceof Player npcEntity) {
            npcEntity.setCanPickupItems(true);
            npcEntity.setCustomNameVisible(true);
        }
        SkinTrait skinTrait = npc.getOrAddTrait(SkinTrait.class);
        skinTrait.setSkinName(name);
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onNpcDamage(NPCDamageEvent event) {
                if (event.getNPC() == npc) {
                    event.setDamage(0);
                }
            }
        }, plugin);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!npc.isSpawned() || !p.isOnline()) {
                    npc.getNavigator().cancelNavigation();
                    cancel();
                    return;
                }
                Location npcLoc = npc.getEntity().getLocation();
                Location playerLoc = p.getLocation();
                double distance = npcLoc.distance(playerLoc);
                if (distance > 4.5) {
                    npc.getNavigator().setTarget(playerLoc);
                } else if (distance < 3.5) {
                    npc.getNavigator().cancelNavigation();
                }
                LookClose lookClose = npc.getOrAddTrait(LookClose.class);
                lookClose.setRange(15);
                lookClose.setRealisticLooking(true);
                lookClose.toggle();
            }
        }.runTaskTimer(plugin, 0L, 10L);
    }

    public static void spawnBot(Location loc, String name) {
        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, name);
        npc.spawn(loc);
        npc.setName(name);
        LookClose lookClose = npc.getOrAddTrait(LookClose.class);
        lookClose.setRange(15);
        lookClose.setRealisticLooking(true);
        lookClose.toggle();
        npc.setProtected(false);
        SkinTrait skinTrait = npc.getOrAddTrait(SkinTrait.class);
        skinTrait.setSkinName(name);

    }

    public static void removeAllNPCs() {
        for (NPC npc : CitizensAPI.getNPCRegistry()) {
            if (npc.isSpawned()) {
                npc.despawn();
            }
            CitizensAPI.getNPCRegistry().deregister(npc);
        }
    }
}
