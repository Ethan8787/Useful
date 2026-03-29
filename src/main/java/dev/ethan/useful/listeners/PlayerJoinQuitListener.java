package dev.ethan.useful.listeners;

import dev.ethan.useful.Main;
import dev.ethan.useful.constants.Messages;
import dev.ethan.useful.utils.IPTrackerUtil;
import dev.ethan.useful.utils.LuckPermsUtil;
import dev.ethan.useful.utils.MessageUtil;
import dev.ethan.useful.utils.PlayerUtil;
import dev.iiahmed.disguise.Disguise;
import dev.iiahmed.disguise.DisguiseManager;
import dev.iiahmed.disguise.SkinAPI;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import top.nontage.nontagelib.annotations.AutoListener;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@AutoListener
public class PlayerJoinQuitListener implements Listener {
    private final LuckPermsUtil luckPermsUtil = Main.getInstance().getLuckPermsUtil();
    private final IPTrackerUtil ipTrackerUtil = Main.getInstance().getIPTrackerUtil();
    private final PlayerUtil playerUtil = Main.getInstance().getPlayerUtil();
    private final MessageUtil messageUtil = Main.getInstance().getMessageUtil();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        String prefix = luckPermsUtil.getPlayerPrefix(p);
        String suffix = luckPermsUtil.getPlayerSuffix(p);
        String nick = Main.nick().getNickname(p);

        String playerName = p.getName();
        String ipAddress = Objects.requireNonNull(p.getAddress()).getAddress().getHostAddress();
        List<String> ipList = ipTrackerUtil.ipsConfig.getStringList(playerName);

        if (messageUtil.getConfig().getBoolean("listeners." + uuid, false)) {
            messageUtil.addDmListener(p);
            p.sendMessage(Messages.PREFIX + "§a您仍在監聽私訊。");
        }

        if (!ipList.contains(ipAddress)) {
            ipList.add(ipAddress);
        }

        if (p.isOp()) {
            p.playSound(p.getLocation(), Sound.ENTITY_GUARDIAN_DEATH, 1.0f, 1.0f);
        } else {
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        }

        ipTrackerUtil.ipsConfig.set(playerName, ipList);
        ipTrackerUtil.save();

        if (nick != null) {
            e.setJoinMessage("§8[§d+§8] " + prefix + nick + suffix);
            p.sendActionBar("§d» §f歡迎回來 " + prefix + nick + suffix + " §d«");

            Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
                UUID skinUUID;
                try {
                    skinUUID = playerUtil.getUUID(nick);
                } catch (Exception ex) {
                    skinUUID = null;
                }

                final UUID finalSkinUUID = (skinUUID != null) ? skinUUID : p.getUniqueId();

                Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                    if (!p.isOnline()) return;

                    Disguise d = Disguise.builder()
                            .setName(nick)
                            .setSkin(SkinAPI.MOJANG, finalSkinUUID)
                            .build();

                    DisguiseManager.getProvider().disguise(p, d);
                    p.setMetadata("nicked", new FixedMetadataValue(Main.getInstance(), true));
                });
            });
            return;
        }

        if (p.hasPlayedBefore()) {
            e.setJoinMessage("§8[§a+§8] " + prefix + p.getDisplayName() + suffix);
        } else {
            e.setJoinMessage("§8[§d+§8] " + prefix + p.getDisplayName() + suffix);
        }
        p.sendActionBar("§d» §f歡迎回來 " + prefix + p.getDisplayName() + suffix + " §d«");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        String prefix = luckPermsUtil.getPlayerPrefix(p);
        String suffix = luckPermsUtil.getPlayerSuffix(p);
        String name = p.getDisplayName();
        e.setQuitMessage("§8[§c-§8] " + prefix + name + suffix);
    }
}