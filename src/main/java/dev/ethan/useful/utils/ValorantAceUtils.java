package dev.ethan.useful.utils;

import dev.ethan.useful.Main;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getServer;

public class ValorantAceUtils {
    public static void playKillSound(Player killer, int killCount) {
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
                sendActionBar(killer, ChatColor.GRAY + "» " + ChatColor.WHITE + "SINGLE KILL" + ChatColor.GRAY + " «");
                break;
            case 2:
                killer.playSound(killer.getLocation(), track01, 2.0f, 1.0f);
                killer.playSound(killer.getLocation(), track02, 2.0f, 1.0f);
                killer.playSound(killer.getLocation(), track03, 2.0f, 1.0f);
                killer.playSound(killer.getLocation(), track04, 2.0f, 1.0f);
                sendActionBar(killer, ChatColor.DARK_GREEN + "» " + ChatColor.GREEN + "DOUBLE KILL" + ChatColor.DARK_GREEN + " «");
                break;
            case 3:
                killer.playSound(killer.getLocation(), track01, 2.0f, 1.059463f);
                killer.playSound(killer.getLocation(), track02, 2.0f, 1.059463f);
                killer.playSound(killer.getLocation(), track03, 2.0f, 1.059463f);
                killer.playSound(killer.getLocation(), track04, 2.0f, 1.059463f);
                sendActionBar(killer, ChatColor.GOLD + "» " + ChatColor.YELLOW + "TRIPLE KILL" + ChatColor.GOLD + " «");
                break;
            case 4:
                killer.playSound(killer.getLocation(), track01, 2.0f, 1.189207f);
                killer.playSound(killer.getLocation(), track02, 2.0f, 1.189207f);
                killer.playSound(killer.getLocation(), track03, 2.0f, 1.189207f);
                killer.playSound(killer.getLocation(), track04, 2.0f, 1.189207f);
                sendActionBar(killer, ChatColor.YELLOW + "» " + ChatColor.GOLD + "QUARDAKILL" + ChatColor.YELLOW + " «");
                break;
            case 5:
                killer.playSound(killer.getLocation(), track01, 2.0f, 0.890899f);
                killer.playSound(killer.getLocation(), track02, 2.0f, 0.890899f);
                killer.playSound(killer.getLocation(), track03, 2.0f, 0.890899f);
                killer.playSound(killer.getLocation(), track04, 2.0f, 0.890899f);

                sendActionBar(killer, ChatColor.DARK_RED + "» " + ChatColor.RED + "PENTAAKILL" + ChatColor.DARK_RED + " «");

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
                    sendActionBar(killer, ChatColor.DARK_PURPLE + "» " + ChatColor.LIGHT_PURPLE + "OVERKILL" + ChatColor.WHITE + " × " + ChatColor.LIGHT_PURPLE + ks + ChatColor.DARK_PURPLE + " «");

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

    public static void sendActionBar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }
}
