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
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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
    private final Map<UUID, Integer> pendingTeleports = new ConcurrentHashMap<>();

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
            sender.sendMessage(Messages.PREFIX + "§f冷卻中，請等待 " + sec + " 秒");
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
            sender.sendMessage(Messages.PREFIX + "§f不能傳送到自己");
            return;
        }
        if (blockingUtil.isBlocked(target.getUniqueId(), sender.getUniqueId())) {
            sender.sendMessage(Messages.PREFIX + "§f" + luckPermsUtil.getPlayerPrefix(target) + target.getDisplayName() + " §f已封鎖你的請求");
            return;
        }

        addRequest(tpaRequests, target.getUniqueId(), sender.getUniqueId());

        Component accept = Component.text("✔", NamedTextColor.GREEN).hoverEvent(HoverEvent.showText(Component.text("同意", NamedTextColor.GREEN))).clickEvent(ClickEvent.runCommand("/tpaccept " + sender.getName()));

        Component deny = Component.text(" ❌", NamedTextColor.RED).hoverEvent(HoverEvent.showText(Component.text("拒絕", NamedTextColor.RED))).clickEvent(ClickEvent.runCommand("/tpdeny " + sender.getName()));

        Component message = legacy(Messages.PREFIX).append(legacy(luckPermsUtil.getPlayerPrefix(sender) + sender.getDisplayName())).append(Component.text(" 想傳送到你這裡 ", NamedTextColor.GRAY)).append(accept).append(deny);

        target.sendMessage(message);
        sender.sendMessage(Messages.PREFIX + "§f已向 " + luckPermsUtil.getPlayerPrefix(target) + target.getDisplayName() + " §f發送請求");
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

            Component message = legacy(Messages.PREFIX).append(legacy(luckPermsUtil.getPlayerPrefix(sender) + sender.getDisplayName())).append(Component.text(" 想把你傳送到他那裡 ", NamedTextColor.GRAY)).append(accept).append(deny);

            target.sendMessage(message);
            sent++;
        }

        sender.sendMessage(Messages.PREFIX + "§f已向 " + sent + " §f位玩家發送傳送請求" + (skipped > 0 ? "，其中 " + skipped + " §f位玩家已封鎖你，已略過" : ""));
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
                acceptor.sendMessage(Messages.PREFIX + "§f該玩家不在線上，已清除該請求");
            } else {
                acceptor.sendMessage(Messages.PREFIX + "§f該玩家沒有對你發送請求或已過期");
            }
            return;
        }

        UUID req = requester.getUniqueId();

        if (hasRequest(tpahereRequests, acc, req)) {
            removeRequest(tpahereRequests, acc, req);
            delayedTeleport(acceptor, requester.getLocation(), luckPermsUtil.getPlayerPrefix(requester) + requester.getDisplayName());
            return;
        }

        if (hasRequest(tpaRequests, acc, req)) {
            removeRequest(tpaRequests, acc, req);
            delayedTeleport(requester, acceptor.getLocation(), luckPermsUtil.getPlayerPrefix(acceptor) + acceptor.getDisplayName());
            return;
        }

        acceptor.sendMessage(Messages.PREFIX + "§f該玩家沒有對你發送請求或已過期");
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
        if (sender != null && sender.isOnline()) {
            UUID snd = sender.getUniqueId();

            boolean removed = false;
            if (hasRequest(tpahereRequests, rec, snd)) {
                removeRequest(tpahereRequests, rec, snd);
                removed = true;
            } else if (hasRequest(tpaRequests, rec, snd)) {
                removeRequest(tpaRequests, rec, snd);
                removed = true;
            }

            if (!removed) {
                receiver.sendMessage(Messages.PREFIX + "§f該玩家沒有對你發送請求或已過期");
                return;
            }

            sender.sendMessage(Messages.PREFIX + luckPermsUtil.getPlayerPrefix(receiver) + receiver.getDisplayName() + " §f拒絕了你的請求");
            receiver.sendMessage(Messages.PREFIX + "§f你已拒絕請求");
            return;
        }

        boolean removed = removeRequestByNameFromMap(tpahereRequests, rec, name) || removeRequestByNameFromMap(tpaRequests, rec, name);
        if (!removed) {
            receiver.sendMessage(Messages.PREFIX + "§f該玩家沒有對你發送請求或已過期");
            return;
        }

        receiver.sendMessage(Messages.PREFIX + "§f你已拒絕請求");
    }

    public void delayedTeleport(Player player, Location target, String name) {
        ensureMainThread();

        UUID pid = player.getUniqueId();
        Integer prev = pendingTeleports.remove(pid);
        if (prev != null) {
            Bukkit.getScheduler().cancelTask(prev);
        }

        player.sendMessage(Messages.PREFIX + "§f將在 §e" + TELEPORT_DELAY_SEC + " 秒 §f後傳送到 §a" + name + " §f請不要移動..");

        final int[] myTaskId = new int[1];

        BukkitRunnable runner = new BukkitRunnable() {
            int count = TELEPORT_DELAY_SEC;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    Integer cur = pendingTeleports.get(pid);
                    if (cur != null && cur == myTaskId[0]) pendingTeleports.remove(pid);
                    cancel();
                    return;
                }

                Integer cur = pendingTeleports.get(pid);
                if (cur == null || cur != myTaskId[0]) {
                    cancel();
                    return;
                }

                if (count <= 0) {
                    player.teleport(target);
                    player.sendMessage(Messages.PREFIX + "§a已傳送到 §f" + name);
                    pendingTeleports.remove(pid);
                    cancel();
                    return;
                }

                player.sendMessage("§7將在 §e" + count + "§7 秒後傳送..");
                count--;
            }
        };

        int id = runner.runTaskTimer(plugin, 0L, 20L).getTaskId();
        myTaskId[0] = id;
        pendingTeleports.put(pid, id);
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