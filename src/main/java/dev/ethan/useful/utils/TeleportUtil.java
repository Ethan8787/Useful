package dev.ethan.useful.utils;

import dev.ethan.useful.Main;
import dev.ethan.useful.constants.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class TeleportUtil {
    private final JavaPlugin plugin;
    private final LuckPermsUtil luckPermsUtil = Main.getInstance().getLuckPermsUtil();
    private final long REQUEST_EXPIRE = 5 * 60 * 1000;
    public final Map<UUID, Map<UUID, Long>> tpaRequests = new HashMap<>();
    public final Map<UUID, Map<UUID, Long>> tpahereRequests = new HashMap<>();
    public final Map<UUID, Set<UUID>> blocks = new HashMap<>();
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private File file;
    private YamlConfiguration config;

    public TeleportUtil(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "blocks.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ignored) {}
        }
        this.config = YamlConfiguration.loadConfiguration(file);
        for (String blockerStr : config.getKeys(false)) {
            try {
                UUID blocker = UUID.fromString(blockerStr);
                List<String> blockedList = config.getStringList(blockerStr);

                Set<UUID> blocked = new HashSet<>();
                for (String s : blockedList) {
                    try {
                        blocked.add(UUID.fromString(s));
                    } catch (IllegalArgumentException ignored) {}
                }
                blocks.put(blocker, blocked);

            } catch (IllegalArgumentException ignored) {}
        }
    }

    public void save() {
        try {
            config.save(file);
        } catch (Exception ex) {
            plugin.getLogger().warning("Failed to save teleport data: " + ex.getMessage());
        }
    }

    public boolean isBlocked(UUID receiver, UUID sender) {
        Set<UUID> set = blocks.get(receiver);
        return set != null && set.contains(sender);
    }

    private void addRequest(Map<UUID, Map<UUID, Long>> map, UUID receiver, UUID requester) {
        map.computeIfAbsent(receiver, k -> new HashMap<>()).put(requester, System.currentTimeMillis() + REQUEST_EXPIRE);
    }

    private boolean hasRequest(Map<UUID, Map<UUID, Long>> map, UUID receiver, UUID requester) {
        Map<UUID, Long> inner = map.get(receiver);
        if (inner == null) return false;
        Long expire = inner.get(requester);
        if (expire == null) return false;
        if (System.currentTimeMillis() > expire) {
            inner.remove(requester);
            if (inner.isEmpty()) map.remove(receiver);
            return false;
        }
        return true;
    }

    private void removeRequest(Map<UUID, Map<UUID, Long>> map, UUID receiver, UUID requester) {
        Map<UUID, Long> inner = map.get(receiver);
        if (inner != null) {
            inner.remove(requester);
            if (inner.isEmpty()) map.remove(receiver);
        }
    }

    private boolean checkCooldown(Player sender) {
        long now = System.currentTimeMillis();
        long cd = cooldowns.getOrDefault(sender.getUniqueId(), 0L);
        if (now < cd) {
            long sec = (cd - now) / 1000;
            sender.sendMessage(Messages.PREFIX + "§f冷卻中，請等待 " + sec + " 秒");
            return false;
        }
        cooldowns.put(sender.getUniqueId(), now + 3000);
        return true;
    }

    public void handleTpaCommand(Player sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(Messages.PREFIX + "§c用法: /tpa <玩家>");
            return;
        }
        if (!checkCooldown(sender)) return;

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline() || !sender.canSee(target)) {
            sender.sendMessage(Messages.PREFIX + "§f找不到該玩家或該玩家不在線上");
            return;
        }
        if (target.equals(sender)) {
            sender.sendMessage(Messages.PREFIX + "§f不能傳送到自己");
            return;
        }
        if (isBlocked(target.getUniqueId(), sender.getUniqueId())) {
            sender.sendMessage(Messages.PREFIX + "§f" + luckPermsUtil.getPlayerPrefix(target) + target.getDisplayName() + " §f已封鎖你的請求");
            return;
        }

        addRequest(tpaRequests, target.getUniqueId(), sender.getUniqueId());
        Component accept = Component.text("✔", NamedTextColor.GREEN)
                .hoverEvent(HoverEvent.showText(Component.text("同意", NamedTextColor.GREEN)))
                .clickEvent(ClickEvent.runCommand("/tpaccept " + sender.getName()));
        Component deny = Component.text(" ❌", NamedTextColor.RED)
                .hoverEvent(HoverEvent.showText(Component.text("拒絕", NamedTextColor.RED)))
                .clickEvent(ClickEvent.runCommand("/tpdeny " + sender.getName()));
        Component message = Component.text(Messages.PREFIX)
                .append(Component.text(luckPermsUtil.getPlayerPrefix(sender) + sender.getDisplayName(), NamedTextColor.WHITE))
                .append(Component.text(" §f想傳送到你這裡 "))
                .append(accept).append(deny);
        target.sendMessage(message);
        sender.sendMessage(Messages.PREFIX + "§f已向 " + luckPermsUtil.getPlayerPrefix(target) + target.getDisplayName() + " §f發送請求");
    }

    public void handleTpahereCommand(Player sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(Messages.PREFIX + "§c用法: /tpahere <玩家|all>");
            return;
        }
        if (!checkCooldown(sender)) return;

        List<Player> targets = new ArrayList<>();
        if (args[0].equalsIgnoreCase("all")) {
            if (!sender.hasPermission("useful.admin")) {
                sender.sendMessage(Messages.PREFIX + "§f你沒有權限使用此子指令");
                return;
            }
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!p.equals(sender) && sender.canSee(p)) targets.add(p);
            }
        } else {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null || !target.isOnline() || !sender.canSee(target)) {
                sender.sendMessage(Messages.PREFIX + "§f找不到該玩家或該玩家不在線上");
                return;
            }
            if (target.equals(sender)) {
                sender.sendMessage(Messages.PREFIX + "§f不能對自己使用");
                return;
            }
            targets.add(target);
        }

        int sent = 0, skipped = 0;
        for (Player target : targets) {
            if (isBlocked(target.getUniqueId(), sender.getUniqueId())) {
                skipped++;
                continue;
            }
            addRequest(tpahereRequests, target.getUniqueId(), sender.getUniqueId());
            Component accept = Component.text("✔", NamedTextColor.GREEN)
                    .hoverEvent(HoverEvent.showText(Component.text("同意", NamedTextColor.GREEN)))
                    .clickEvent(ClickEvent.runCommand("/tpaccept " + sender.getName()));
            Component deny = Component.text(" ❌", NamedTextColor.RED)
                    .hoverEvent(HoverEvent.showText(Component.text("拒絕", NamedTextColor.RED)))
                    .clickEvent(ClickEvent.runCommand("/tpdeny " + sender.getName()));
            Component message = Component.text(Messages.PREFIX)
                    .append(Component.text(luckPermsUtil.getPlayerPrefix(sender) + sender.getDisplayName(), NamedTextColor.WHITE))
                    .append(Component.text(" §f想你傳送到他那裡 "))
                    .append(accept).append(deny);
            target.sendMessage(message);
            sent++;
        }
        sender.sendMessage(Messages.PREFIX + "§f已向 " + sent + " §f位玩家發送傳送請求" + (skipped > 0 ? "，其中 " + skipped + " §f位玩家已封鎖你，已略過" : ""));
    }

    public void handleTpacceptCommand(Player acceptor, String[] args) {
        if (args.length < 1) {
            acceptor.sendMessage(Messages.PREFIX + "§c用法: /tpaccept <玩家>");
            return;
        }
        Player requester = Bukkit.getPlayer(args[0]);
        if (requester == null || !requester.isOnline()) {
            acceptor.sendMessage(Messages.PREFIX + "§f該玩家不在線上");
            return;
        }

        if (hasRequest(tpahereRequests, acceptor.getUniqueId(), requester.getUniqueId())) {
            removeRequest(tpahereRequests, acceptor.getUniqueId(), requester.getUniqueId());
            delayedTeleport(acceptor, requester.getLocation(), luckPermsUtil.getPlayerPrefix(requester) + requester.getDisplayName());
        } else if (hasRequest(tpaRequests, acceptor.getUniqueId(), requester.getUniqueId())) {
            removeRequest(tpaRequests, acceptor.getUniqueId(), requester.getUniqueId());
            delayedTeleport(requester, acceptor.getLocation(), luckPermsUtil.getPlayerPrefix(acceptor) + acceptor.getDisplayName());
        } else {
            acceptor.sendMessage(Messages.PREFIX + "§f該玩家沒有對你發送請求或已過期");
        }
    }

    public void handleTpdenyCommand(Player receiver, String[] args) {
        if (args.length < 1) {
            receiver.sendMessage(Messages.PREFIX + "§c用法: /tpdeny <玩家>");
            return;
        }
        Player sender = Bukkit.getPlayer(args[0]);
        if (sender == null || !sender.isOnline()) {
            receiver.sendMessage(Messages.PREFIX + "§f該玩家不在線上");
            return;
        }

        if (hasRequest(tpahereRequests, receiver.getUniqueId(), sender.getUniqueId())) {
            removeRequest(tpahereRequests, receiver.getUniqueId(), sender.getUniqueId());
            sender.sendMessage(Messages.PREFIX + luckPermsUtil.getPlayerPrefix(receiver) + receiver.getDisplayName() + " §f拒絕了你的請求");
            receiver.sendMessage(Messages.PREFIX + "§f你已拒絕請求");
        } else if (hasRequest(tpaRequests, receiver.getUniqueId(), sender.getUniqueId())) {
            removeRequest(tpaRequests, receiver.getUniqueId(), sender.getUniqueId());
            sender.sendMessage(Messages.PREFIX + luckPermsUtil.getPlayerPrefix(receiver) + receiver.getDisplayName() + " §f拒絕了你的請求");
            receiver.sendMessage(Messages.PREFIX + "§f你已拒絕請求");
        } else {
            receiver.sendMessage(Messages.PREFIX + "§f該玩家沒有對你發送請求或已過期");
        }
    }

    private Set<UUID> getBlockSet(UUID blocker) {
        return blocks.computeIfAbsent(blocker, k -> new HashSet<>());
    }

    public void handleBlockCommand(Player blocker, String[] args) {
        if (args.length < 1) {
            blocker.sendMessage(Messages.PREFIX + "§c用法: /block <玩家>");
            return;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (target.getUniqueId().equals(blocker.getUniqueId())) {
            blocker.sendMessage(Messages.PREFIX + "§f不能封鎖自己");
            return;
        }
        Set<UUID> set = getBlockSet(blocker.getUniqueId());
        if (!set.add(target.getUniqueId())) {
            blocker.sendMessage(Messages.PREFIX + "§f你已封鎖過 " + luckPermsUtil.getPlayerPrefix(target) + target.getName());
        } else {
            save();
            blocker.sendMessage(Messages.PREFIX + "§f已封鎖 " + luckPermsUtil.getPlayerPrefix(target) + target.getName() + " §f的傳送與私訊請求");
        }
    }

    public void handleUnblockCommand(Player blocker, String[] args) {
        if (args.length < 1) {
            blocker.sendMessage(Messages.PREFIX + "§c用法: /unblock <玩家>");
            return;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        Set<UUID> set = getBlockSet(blocker.getUniqueId());
        if (!set.remove(target.getUniqueId())) {
            blocker.sendMessage(Messages.PREFIX + "§f你並未封鎖 " + luckPermsUtil.getPlayerPrefix(target) + target.getName());
        } else {
            if (set.isEmpty()) blocks.remove(blocker.getUniqueId());
            save();
            blocker.sendMessage(Messages.PREFIX + "§f已解除封鎖 " + luckPermsUtil.getPlayerPrefix(target) + target.getName());
        }
    }

    public void handleBlockListCommand(Player viewer) {
        Set<UUID> set = blocks.getOrDefault(viewer.getUniqueId(), Collections.emptySet());
        if (set.isEmpty()) {
            viewer.sendMessage(Messages.PREFIX + "§f你沒有封鎖任何人");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (UUID u : set) {
            OfflinePlayer p = Bukkit.getOfflinePlayer(u);
            String name = p.getName() != null ? luckPermsUtil.getPlayerPrefix(p) + p.getName() : "§7(未知玩家)";
            if (!sb.isEmpty()) sb.append("§7, §f");
            sb.append(name);
        }
        viewer.sendMessage(Messages.PREFIX + "§f已封鎖: " + sb);
    }

    public void delayedTeleport(Player player, Location target, String name) {
        Location start = player.getLocation().clone();
        player.sendMessage(Messages.PREFIX + "§f將在 §e5 秒 §f後傳送到 §a" + name + " §f請不要移動..");

        final int[] count = {5};
        final BukkitTask[] task = new BukkitTask[1];

        task[0] = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), () -> {
            if (!player.isOnline()) {
                task[0].cancel();
                return;
            }
            Location now = player.getLocation();
            if (now.getWorld() != start.getWorld() || now.distanceSquared(start) > 0.2) {
                player.sendMessage(Messages.PREFIX + "§c傳送已取消，因為你移動了。");
                task[0].cancel();
                return;
            }
            if (count[0] <= 0) {
                player.teleport(target);
                player.sendMessage(Messages.PREFIX + "§a已傳送到 §f" + name);
                task[0].cancel();
                return;
            }
            player.sendMessage("§7將在 §e" + count[0] + "§7 秒後傳送..");
            count[0]--;
        }, 0L, 20L);
    }
}