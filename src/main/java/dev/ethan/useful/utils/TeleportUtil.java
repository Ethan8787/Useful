package dev.ethan.useful.utils;

import dev.ethan.useful.Main;
import dev.ethan.useful.constants.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static net.kyori.adventure.text.Component.text;

public class TeleportUtil {

    private void ensureMainThread() {
        if (!Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("TeleportUtil accessed async!");
        }
    }

    private final JavaPlugin plugin;
    private final LuckPermsUtil luckPermsUtil;
    private final PlayerBlockingUtil blockingUtil;

    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();

    private static final long REQUEST_EXPIRE_MS = 5L * 60L * 1000L;
    private static final long COOLDOWN_MS = 3000L;
    private static final int TELEPORT_DELAY_SEC = 5;

    private final Map<RequestKey, Long> tpaRequests = new ConcurrentHashMap<>();
    private final Map<RequestKey, Long> tpahereRequests = new ConcurrentHashMap<>();
    private final Map<UUID, Long> cooldowns = new ConcurrentHashMap<>();
    private final Map<UUID, TeleportSession> pendingTeleports = new ConcurrentHashMap<>();

    public TeleportUtil(JavaPlugin plugin) {
        this.plugin = plugin;
        this.luckPermsUtil = Main.getInstance().getLuckPermsUtil();
        this.blockingUtil = Main.getInstance().getPlayerBlockingUtil();

        new BukkitRunnable() {
            @Override
            public void run() {
                cleanupExpired();
            }
        }.runTaskTimer(plugin, 1200L, 1200L);
    }

    private Component legacy(String s) {
        if (s == null || s.isEmpty()) return Component.empty();
        return LEGACY.deserialize(s);
    }

    private void cleanupExpired() {
        ensureMainThread();
        long now = System.currentTimeMillis();
        cleanupMap(tpaRequests, now);
        cleanupMap(tpahereRequests, now);
        cooldowns.entrySet().removeIf(e -> e.getValue() <= now);
    }

    private void cleanupMap(Map<RequestKey, Long> map, long now) {
        map.entrySet().removeIf(e -> e.getValue() <= now);
    }

    private void addRequest(Map<RequestKey, Long> map, UUID receiver, UUID requester) {
        ensureMainThread();
        map.put(new RequestKey(receiver, requester), System.currentTimeMillis() + REQUEST_EXPIRE_MS);
    }

    private boolean hasRequest(Map<RequestKey, Long> map, UUID receiver, UUID requester) {
        ensureMainThread();
        RequestKey key = new RequestKey(receiver, requester);
        Long expire = map.get(key);
        if (expire == null) return false;
        if (System.currentTimeMillis() > expire) {
            map.remove(key);
            return false;
        }
        return true;
    }

    private void removeRequest(Map<RequestKey, Long> map, UUID receiver, UUID requester) {
        ensureMainThread();
        map.remove(new RequestKey(receiver, requester));
    }

    private Player findOnlinePlayer(String name) {
        Player p = Bukkit.getPlayerExact(name);
        if (p != null) return p;
        for (Player pl : Bukkit.getOnlinePlayers()) {
            if (pl.getName().equalsIgnoreCase(name)) return pl;
        }
        return null;
    }

    public List<String> getPendingRequesters(Player receiver) {
        ensureMainThread();
        UUID receiverId = receiver.getUniqueId();
        long now = System.currentTimeMillis();
        Set<String> requesterNames = new HashSet<>();
        for (Map.Entry<RequestKey, Long> entry : tpaRequests.entrySet()) {
            if (entry.getKey().receiver.equals(receiverId) && entry.getValue() > now) {
                OfflinePlayer op = Bukkit.getOfflinePlayer(entry.getKey().requester);
                if (op.getName() != null) requesterNames.add(op.getName());
            }
        }
        for (Map.Entry<RequestKey, Long> entry : tpahereRequests.entrySet()) {
            if (entry.getKey().receiver.equals(receiverId) && entry.getValue() > now) {
                OfflinePlayer op = Bukkit.getOfflinePlayer(entry.getKey().requester);
                if (op.getName() != null) requesterNames.add(op.getName());
            }
        }
        return new ArrayList<>(requesterNames);
    }

    private boolean removeRequestByNameFromMap(Map<RequestKey, Long> map, UUID receiver, String requesterName) {
        ensureMainThread();
        long now = System.currentTimeMillis();
        for (Map.Entry<RequestKey, Long> e : map.entrySet()) {
            RequestKey k = e.getKey();
            Long exp = e.getValue();
            if (exp == null || exp <= now) {
                map.remove(k);
                continue;
            }
            if (!k.receiver.equals(receiver)) continue;
            OfflinePlayer op = Bukkit.getOfflinePlayer(k.requester);
            String n = op.getName();
            if (n != null && n.equalsIgnoreCase(requesterName)) {
                map.remove(k);
                return true;
            }
        }
        return false;
    }

    private boolean checkCooldown(Player sender) {
        ensureMainThread();
        long now = System.currentTimeMillis();
        long next = cooldowns.getOrDefault(sender.getUniqueId(), 0L);
        if (now < next) {
            long sec = Math.max(1, (next - now + 999) / 1000);
            sender.sendMessage(Messages.PREFIX + "§f請等待 §e" + sec + " §f秒" + "§7(冷卻中)");
            return false;
        }
        cooldowns.put(sender.getUniqueId(), now + COOLDOWN_MS);
        return true;
    }

    public void handleTpaCommand(Player sender, String[] args) {
        ensureMainThread();

        if (args.length < 1) {
            sender.sendMessage(Messages.PREFIX + "§c用法: /tpa <玩家>");
            return;
        }
        if (!checkCooldown(sender)) return;

        Player target = findOnlinePlayer(args[0]);
        if (target == null || !target.isOnline() || !sender.canSee(target)) {
            sender.sendMessage(Messages.PREFIX + "§f找不到該玩家或該玩家不在線上");
            return;
        }
        if (target.equals(sender)) {
            sender.sendMessage(Messages.PREFIX + "§f無法傳送自己");
            return;
        }
        if (blockingUtil.isBlocked(target.getUniqueId(), sender.getUniqueId())) {
            sender.sendMessage(Messages.PREFIX + "§f" + luckPermsUtil.getPlayerPrefix(target) + target.getDisplayName() + " §f已封鎖你的請求");
            return;
        }

        addRequest(tpaRequests, target.getUniqueId(), sender.getUniqueId());

        Component accept = Component.text("✔", NamedTextColor.GREEN).hoverEvent(HoverEvent.showText(Component.text("同意", NamedTextColor.GREEN))).clickEvent(ClickEvent.runCommand("/tpaccept " + sender.getName()));
        Component deny = Component.text(" ❌", NamedTextColor.RED).hoverEvent(HoverEvent.showText(Component.text("拒絕", NamedTextColor.RED))).clickEvent(ClickEvent.runCommand("/tpdeny " + sender.getName()));

        Component message = legacy(Messages.PREFIX).append(legacy(luckPermsUtil.getPlayerPrefix(sender) + sender.getDisplayName())).append(Component.text(" 想傳送到你這裡 ", NamedTextColor.WHITE)).append(accept).append(deny);
        target.sendMessage(message);

        Component cancelBtn = text(" §7| §c取消")
                .hoverEvent(HoverEvent.showText(text("撤回請求", NamedTextColor.RED)))
                .clickEvent(ClickEvent.runCommand("/tpcancel"));

        Component successMsg = legacy(Messages.PREFIX)
                .append(text("已向 ", NamedTextColor.WHITE))
                .append(legacy(luckPermsUtil.getPlayerPrefix(target) + target.getDisplayName()))
                .append(text(" 發送請求", NamedTextColor.WHITE))
                .append(cancelBtn);

        sender.sendMessage(successMsg);
    }

    public void handleTpahereCommand(Player sender, String[] args) {
        ensureMainThread();

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
            Player target = findOnlinePlayer(args[0]);
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

        int sent = 0;
        int skipped = 0;

        for (Player target : targets) {
            if (blockingUtil.isBlocked(target.getUniqueId(), sender.getUniqueId())) {
                skipped++;
                continue;
            }

            addRequest(tpahereRequests, target.getUniqueId(), sender.getUniqueId());

            Component accept = Component.text("✔", NamedTextColor.GREEN).hoverEvent(HoverEvent.showText(Component.text("同意", NamedTextColor.GREEN))).clickEvent(ClickEvent.runCommand("/tpaccept " + sender.getName()));
            Component deny = Component.text(" ❌", NamedTextColor.RED).hoverEvent(HoverEvent.showText(Component.text("拒絕", NamedTextColor.RED))).clickEvent(ClickEvent.runCommand("/tpdeny " + sender.getName()));

            Component message = legacy(Messages.PREFIX).append(legacy(luckPermsUtil.getPlayerPrefix(sender) + sender.getDisplayName())).append(Component.text(" 想把你傳送到他那裡 ", NamedTextColor.WHITE)).append(accept).append(deny);
            target.sendMessage(message);
            sent++;
        }

        Component cancelBtn = text(" §7| §c取消")
                .hoverEvent(HoverEvent.showText(text("撤回所有請求", NamedTextColor.RED)))
                .clickEvent(ClickEvent.runCommand("/tpcancel"));

        Component resultMsg = legacy(Messages.PREFIX)
                .append(text("已向 ", NamedTextColor.WHITE))
                .append(text(sent, NamedTextColor.YELLOW))
                .append(text(" 位玩家發送傳送請求", NamedTextColor.WHITE))
                .append(text(skipped > 0 ? " (已略過 " + skipped + " 位)" : "", NamedTextColor.GRAY))
                .append(cancelBtn);

        sender.sendMessage(resultMsg);
    }

    public void handleTpacceptCommand(Player acceptor, String[] args) {
        ensureMainThread();

        if (args.length < 1) {
            acceptor.sendMessage(Messages.PREFIX + "§c用法: /tpaccept <玩家>");
            return;
        }

        Player requester = findOnlinePlayer(args[0]);
        UUID acc = acceptor.getUniqueId();

        if (requester == null || !requester.isOnline()) {
            boolean removed = removeRequestByNameFromMap(tpahereRequests, acc, args[0]) || removeRequestByNameFromMap(tpaRequests, acc, args[0]);
            if (removed) {
                acceptor.sendMessage(Messages.PREFIX + "§f該玩家不在線上，已清除請求");
            } else {
                acceptor.sendMessage(Messages.PREFIX + "§f找不到該請求或已過期");
            }
            return;
        }

        UUID req = requester.getUniqueId();
        String requesterDisplay = luckPermsUtil.getPlayerPrefix(requester) + requester.getDisplayName();

        Component cancelBtn = text(" §7| §c取消")
                .hoverEvent(HoverEvent.showText(Component.text("取消傳送", NamedTextColor.RED)))
                .clickEvent(ClickEvent.runCommand("/tpcancel"));

        if (hasRequest(tpahereRequests, acc, req)) {
            removeRequest(tpahereRequests, acc, req);
            acceptor.sendMessage(legacy(Messages.PREFIX).append(Component.text("已同意來自 ", NamedTextColor.WHITE)).append(legacy(requesterDisplay)).append(Component.text(" 的傳送邀請", NamedTextColor.WHITE)).append(cancelBtn));
            requester.sendMessage(legacy(Messages.PREFIX).append(legacy(luckPermsUtil.getPlayerPrefix(acceptor) + acceptor.getDisplayName())).append(Component.text(" 同意了你的傳送請求", NamedTextColor.WHITE)).append(cancelBtn));
            acceptor.playSound(acceptor.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1.2f);
            delayedTeleport(acceptor, requester);
            return;
        }

        if (hasRequest(tpaRequests, acc, req)) {
            removeRequest(tpaRequests, acc, req);
            acceptor.sendMessage(legacy(Messages.PREFIX).append(Component.text("已同意 ", NamedTextColor.WHITE)).append(legacy(requesterDisplay)).append(Component.text(" 傳送到你這裡", NamedTextColor.WHITE)).append(cancelBtn));
            requester.sendMessage(legacy(Messages.PREFIX).append(legacy(luckPermsUtil.getPlayerPrefix(acceptor) + acceptor.getDisplayName())).append(Component.text(" 同意了你的傳送請求", NamedTextColor.WHITE)).append(cancelBtn));
            acceptor.playSound(acceptor.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1.2f);
            delayedTeleport(requester, acceptor);
            return;
        }

        acceptor.sendMessage(Messages.PREFIX + "§f找不到該請求或已過期");
    }

    public void handleTpdenyCommand(Player receiver, String[] args) {
        ensureMainThread();

        if (args.length < 1) {
            receiver.sendMessage(Messages.PREFIX + "§c用法: /tpdeny <玩家>");
            return;
        }

        UUID rec = receiver.getUniqueId();
        String name = args[0];
        Player sender = findOnlinePlayer(name);

        boolean removed = false;
        UUID sndId = (sender != null) ? sender.getUniqueId() : null;

        if (sndId != null) {
            if (hasRequest(tpahereRequests, rec, sndId)) {
                removeRequest(tpahereRequests, rec, sndId);
                removed = true;
            } else if (hasRequest(tpaRequests, rec, sndId)) {
                removeRequest(tpaRequests, rec, sndId);
                removed = true;
            }
        } else {
            removed = removeRequestByNameFromMap(tpahereRequests, rec, name) || removeRequestByNameFromMap(tpaRequests, rec, name);
        }

        if (!removed) {
            receiver.sendMessage(Messages.PREFIX + "§f找不到該請求或已過期");
            return;
        }

        receiver.sendMessage(legacy(Messages.PREFIX).append(Component.text("你已拒絕來自 ", NamedTextColor.WHITE)).append(Component.text(name, NamedTextColor.WHITE)).append(Component.text(" 的請求", NamedTextColor.WHITE)));

        if (sender != null && sender.isOnline()) {
            sender.sendMessage(legacy(Messages.PREFIX).append(legacy(luckPermsUtil.getPlayerPrefix(receiver) + receiver.getDisplayName())).append(Component.text(" 拒絕了你的傳送請求", NamedTextColor.RED)));
            sender.playSound(sender.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.5f);
        }
    }

    public void handleTpcancelCommand(Player canceller, String[] args) {
        ensureMainThread();

        UUID cid = canceller.getUniqueId();
        TeleportSession sessionToCancel = null;

        for (TeleportSession session : pendingTeleports.values()) {
            if (session.teleporter.equals(cid) || session.target.equals(cid)) {
                sessionToCancel = session;
                break;
            }
        }

        if (sessionToCancel != null) {
            Bukkit.getScheduler().cancelTask(sessionToCancel.taskId);
            pendingTeleports.remove(sessionToCancel.teleporter);

            Player teleporter = Bukkit.getPlayer(sessionToCancel.teleporter);
            Player target = Bukkit.getPlayer(sessionToCancel.target);

            if (teleporter != null) teleporter.sendMessage(Messages.PREFIX + "§c傳送已取消");
            if (target != null && !target.equals(teleporter)) target.sendMessage(Messages.PREFIX + "§c傳送已取消");
            return;
        }

        boolean requestRemoved = false;
        Set<UUID> notifiedReceivers = new HashSet<>();
        String cancellerDisplay = luckPermsUtil.getPlayerPrefix(canceller) + canceller.getDisplayName();

        var it1 = tpaRequests.entrySet().iterator();
        while (it1.hasNext()) {
            var entry = it1.next();
            if (entry.getKey().requester.equals(cid)) {
                notifiedReceivers.add(entry.getKey().receiver);
                it1.remove();
                requestRemoved = true;
            }
        }

        var it2 = tpahereRequests.entrySet().iterator();
        while (it2.hasNext()) {
            var entry = it2.next();
            if (entry.getKey().requester.equals(cid)) {
                notifiedReceivers.add(entry.getKey().receiver);
                it2.remove();
                requestRemoved = true;
            }
        }

        if (requestRemoved) {
            canceller.sendMessage(Messages.PREFIX + "§c已撤回發出的傳送請求");

            for (UUID receiverId : notifiedReceivers) {
                Player receiver = Bukkit.getPlayer(receiverId);
                if (receiver != null && receiver.isOnline()) {
                    receiver.sendMessage(legacy(Messages.PREFIX)
                            .append(legacy(cancellerDisplay))
                            .append(text(" 撤回了傳送請求", NamedTextColor.RED)));
                }
            }
        } else {
            canceller.sendMessage(Messages.PREFIX + "§f你目前沒有正在進行的傳送或請求");
        }
    }

    public void delayedTeleport(Player teleporter, Location targetLoc, String destinationName) {
        ensureMainThread();
        UUID teleporterId = teleporter.getUniqueId();
        UUID targetId = teleporterId;
        TeleportSession prev = pendingTeleports.remove(teleporterId);
        if (prev != null) {
            Bukkit.getScheduler().cancelTask(prev.taskId);
        }
        final int[] myTaskId = new int[1];
        BukkitRunnable runner = new BukkitRunnable() {
            int count = TELEPORT_DELAY_SEC;

            @Override
            public void run() {
                if (!teleporter.isOnline()) {
                    pendingTeleports.remove(teleporterId);
                    cancel();
                    return;
                }
                TeleportSession cur = pendingTeleports.get(teleporterId);
                if (cur == null || cur.taskId != myTaskId[0]) {
                    cancel();
                    return;
                }
                if (count <= 0) {
                    teleporter.teleport(targetLoc);
                    teleporter.sendMessage(Messages.PREFIX + "§a已傳送到 §f" + destinationName);
                    pendingTeleports.remove(teleporterId);
                    cancel();
                    return;
                }

                Component cancelBtn = text(" §7| §c取消")
                        .hoverEvent(HoverEvent.showText(text("點擊取消傳送", NamedTextColor.RED)))
                        .clickEvent(ClickEvent.runCommand("/tpcancel"));

                Component countdownMsg = legacy(Messages.PREFIX)
                        .append(text("將在 ", NamedTextColor.WHITE))
                        .append(text(count, NamedTextColor.YELLOW))
                        .append(text(" 秒後傳送到 ", NamedTextColor.WHITE))
                        .append(text(destinationName, NamedTextColor.GREEN))
                        .append(cancelBtn);

                teleporter.sendMessage(countdownMsg);
                count--;
            }
        };

        int id = runner.runTaskTimer(plugin, 0L, 20L).getTaskId();
        myTaskId[0] = id;
        pendingTeleports.put(teleporterId, new TeleportSession(id, teleporterId, targetId));
    }

    public void delayedTeleport(Player teleporter, Player targetPlayer) {
        ensureMainThread();

        UUID teleporterId = teleporter.getUniqueId();
        UUID targetId = targetPlayer.getUniqueId();
        String targetNameRaw = luckPermsUtil.getPlayerPrefix(targetPlayer) + targetPlayer.getDisplayName();

        TeleportSession prev = pendingTeleports.remove(teleporterId);
        if (prev != null) {
            Bukkit.getScheduler().cancelTask(prev.taskId);
        }

        final int[] myTaskId = new int[1];

        BukkitRunnable runner = new BukkitRunnable() {
            int count = TELEPORT_DELAY_SEC;

            @Override
            public void run() {
                if (!teleporter.isOnline() || !targetPlayer.isOnline()) {
                    TeleportSession cur = pendingTeleports.get(teleporterId);
                    if (cur != null && cur.taskId == myTaskId[0]) pendingTeleports.remove(teleporterId);
                    cancel();
                    return;
                }

                TeleportSession cur = pendingTeleports.get(teleporterId);
                if (cur == null || cur.taskId != myTaskId[0]) {
                    cancel();
                    return;
                }

                if (count <= 0) {
                    teleporter.teleport(targetPlayer.getLocation());
                    teleporter.sendMessage(Messages.PREFIX + "§a已傳送到 §f" + targetNameRaw);
                    pendingTeleports.remove(teleporterId);
                    cancel();
                    return;
                }

                Component cancelBtn = text(" §7| §c取消")
                        .hoverEvent(HoverEvent.showText(text("點擊取消傳送", NamedTextColor.RED)))
                        .clickEvent(ClickEvent.runCommand("/tpcancel"));

                Component countdownMsg = legacy(Messages.PREFIX)
                        .append(text("將在 ", NamedTextColor.WHITE))
                        .append(text(count, NamedTextColor.YELLOW))
                        .append(text(" 秒後傳送到 ", NamedTextColor.WHITE))
                        .append(legacy(targetNameRaw))
                        .append(cancelBtn);

                teleporter.sendMessage(countdownMsg);
                count--;
            }
        };

        int id = runner.runTaskTimer(plugin, 0L, 20L).getTaskId();
        myTaskId[0] = id;
        pendingTeleports.put(teleporterId, new TeleportSession(id, teleporterId, targetId));
    }

    private static final class TeleportSession {
        private final int taskId;
        private final UUID teleporter;
        private final UUID target;

        private TeleportSession(int taskId, UUID teleporter, UUID target) {
            this.taskId = taskId;
            this.teleporter = teleporter;
            this.target = target;
        }
    }

    private static final class RequestKey {
        private final UUID receiver;
        private final UUID requester;

        private RequestKey(UUID receiver, UUID requester) {
            this.receiver = receiver;
            this.requester = requester;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof RequestKey)) return false;
            RequestKey that = (RequestKey) o;
            return receiver.equals(that.receiver) && requester.equals(that.requester);
        }

        @Override
        public int hashCode() {
            return Objects.hash(receiver, requester);
        }
    }
}