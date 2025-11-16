package dev.ethan.useful.utils;

import dev.ethan.useful.Main;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getServer;

public class AceUtil {
    public void playKillSound(Player killer, int killCount) {
        Sound track01 = Sound.ENTITY_EXPERIENCE_ORB_PICKUP;
        Sound track02 = Sound.BLOCK_NOTE_BLOCK_CHIME;
        Sound track03 = Sound.BLOCK_NOTE_BLOCK_BELL;
        Sound track04 = Sound.BLOCK_NOTE_BLOCK_BASS;
        switch (killCount) {
            case 1:
                killer.playSound(killer.getLocation(), track01, 2.0f, 0.890899f);
                killer.playSound(killer.getLocation(), track02, 2.0f, 0.890899f);
                killer.playSound(killer.getLocation(), track03, 2.0f, 0.890899f);
                killer.playSound(killer.getLocation(), track04, 2.0f, 0.890899f);
                sendActionBar(killer, "§8» " + "§fSINGLE KILL" + " §8«");
                break;
            case 2:
                killer.playSound(killer.getLocation(), track01, 2.0f, 1.0f);
                killer.playSound(killer.getLocation(), track02, 2.0f, 1.0f);
                killer.playSound(killer.getLocation(), track03, 2.0f, 1.0f);
                killer.playSound(killer.getLocation(), track04, 2.0f, 1.0f);
                sendActionBar(killer, "§2» " + "§aDOUBLE KILL" + " §2«");
                break;
            case 3:
                killer.playSound(killer.getLocation(), track01, 2.0f, 1.059463f);
                killer.playSound(killer.getLocation(), track02, 2.0f, 1.059463f);
                killer.playSound(killer.getLocation(), track03, 2.0f, 1.059463f);
                killer.playSound(killer.getLocation(), track04, 2.0f, 1.059463f);
                sendActionBar(killer, "§6» " + "§eTRIPLE KILL" + " §6«");
                break;
            case 4:
                killer.playSound(killer.getLocation(), track01, 2.0f, 1.189207f);
                killer.playSound(killer.getLocation(), track02, 2.0f, 1.189207f);
                killer.playSound(killer.getLocation(), track03, 2.0f, 1.189207f);
                killer.playSound(killer.getLocation(), track04, 2.0f, 1.189207f);
                sendActionBar(killer, "§e» " + "§6QUADRAKILL" + " §e«");
                break;
            case 5:
                killer.playSound(killer.getLocation(), track01, 2.0f, 0.890899f);
                killer.playSound(killer.getLocation(), track02, 2.0f, 0.890899f);
                killer.playSound(killer.getLocation(), track03, 2.0f, 0.890899f);
                killer.playSound(killer.getLocation(), track04, 2.0f, 0.890899f);
                sendActionBar(killer, "§4» " + "§cPENTAKILL" + " §4«");
                getServer().getScheduler().runTaskLater(Main.getInstance(), () -> {
                    killer.playSound(killer.getLocation(), track01, 2.0f, 0.890899f);
                    killer.playSound(killer.getLocation(), track02, 2.0f, 0.890899f);
                    killer.playSound(killer.getLocation(), track03, 2.0f, 0.890899f);
                    killer.playSound(killer.getLocation(), track04, 2.0f, 0.890899f);
                }, 6L);
                getServer().getScheduler().runTaskLater(Main.getInstance(), () -> {
                    killer.playSound(killer.getLocation(), track01, 2.0f, 0.890899f);
                    killer.playSound(killer.getLocation(), track02, 2.0f, 0.890899f);
                    killer.playSound(killer.getLocation(), track03, 2.0f, 0.890899f);
                    killer.playSound(killer.getLocation(), track04, 2.0f, 0.890899f);
                }, 14L);
                getServer().getScheduler().runTaskLater(Main.getInstance(), () -> {

                    killer.playSound(killer.getLocation(), track01, 2.0f, 1.059463f);
                    killer.playSound(killer.getLocation(), track02, 2.0f, 1.059463f);
                    killer.playSound(killer.getLocation(), track03, 2.0f, 1.059463f);
                    killer.playSound(killer.getLocation(), track04, 2.0f, 1.059463f);
                }, 18L);
                break;
            default:
                if (killCount > 5) {
                    int ks = killCount - 5;
                    killer.playSound(killer.getLocation(), track01, 2.0f, 0.890899f);
                    killer.playSound(killer.getLocation(), track02, 2.0f, 0.890899f);
                    killer.playSound(killer.getLocation(), track03, 2.0f, 0.890899f);
                    killer.playSound(killer.getLocation(), track04, 2.0f, 0.890899f);
                    sendActionBar(killer, "§5» " + "§dOVERKILL" + " §f× §d" + ks + " §5«");
                    getServer().getScheduler().runTaskLater(Main.getInstance(), () -> {
                        killer.playSound(killer.getLocation(), track01, 2.0f, 0.890899f);
                        killer.playSound(killer.getLocation(), track02, 2.0f, 0.890899f);
                        killer.playSound(killer.getLocation(), track03, 2.0f, 0.890899f);
                        killer.playSound(killer.getLocation(), track04, 2.0f, 0.890899f);
                    }, 6L);
                    getServer().getScheduler().runTaskLater(Main.getInstance(), () -> {
                        killer.playSound(killer.getLocation(), track01, 2.0f, 0.890899f);
                        killer.playSound(killer.getLocation(), track02, 2.0f, 0.890899f);
                        killer.playSound(killer.getLocation(), track03, 2.0f, 0.890899f);
                        killer.playSound(killer.getLocation(), track04, 2.0f, 0.890899f);
                    }, 14L);
                    getServer().getScheduler().runTaskLater(Main.getInstance(), () -> {
                        killer.playSound(killer.getLocation(), track01, 2.0f, 1.059463f);
                        killer.playSound(killer.getLocation(), track02, 2.0f, 1.059463f);
                        killer.playSound(killer.getLocation(), track03, 2.0f, 1.059463f);
                        killer.playSound(killer.getLocation(), track04, 2.0f, 1.059463f);
                    }, 18L);
                }
                break;
        }
    }

    public void sendActionBar(Player p, String msg) {
        p.sendActionBar(msg);
    }
}
