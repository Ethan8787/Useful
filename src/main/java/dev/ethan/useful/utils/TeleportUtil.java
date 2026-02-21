package dev.ethan.useful.utils;

import dev.ethan.useful.Main;
import dev.ethan.useful.constants.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TeleportUtil {

    // 主線程專用：如果哪天你不小心 async 叫到，會直接炸，避免 silent bug
    private void ensureMainThread() {
        if (!Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("TeleportUtil accessed async!");
        }
    }

    private final JavaPlugin plugin;
    private final LuckPermsUtil luckPermsUtil;
    private final PlayerBlockingUtil blockingUtil;

    private static final long REQUEST_EXPIRE_MS = 5L * 60L * 1000L;
    private static final long COOLDOWN_MS = 3000L;
    private static final int TELEPORT_DELAY_SEC = 5;
    private static final double MOVE_CANCEL_DISTANCE_SQ = 0.2;

    // 單一 Map：key(receiver, requester) -> expireAt
    private final Map<RequestKey, Long> tpaRequests = new ConcurrentHashMap<>();
    private final Map<RequestKey, Long> tpahereRequests = new ConcurrentHashMap<>();

    // cooldown: sender -> nextAllowedAt
    private final Map<UUID, Long> cooldowns = new ConcurrentHashMap<>();

    public TeleportUtil(JavaPlugin plugin) {
        this.plugin = plugin;
        this.luckPermsUtil = Main.getInstance().getLuckPermsUtil();
        this.blockingUtil = Main.getInstance().getPlayerBlockingUtil();

        // 每 1 分鐘清掉過期資料，避免 map 一直長
        new BukkitRunnable() {
            @Override
            public void run() {
                cleanupExpired();
            }
        }.runTaskTimer(plugin, 1200L, 1200L);
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

        Player target = Bukkit.getPlayer(args[0]);
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

        int sent = 0;
        int skipped = 0;

        for (Player target : targets) {
            if (blockingUtil.isBlocked(target.getUniqueId(), sender.getUniqueId())) {
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

        sender.sendMessage(Messages.PREFIX + "§f已向 " + sent + " §f位玩家發送傳送請求"
                + (skipped > 0 ? "，其中 " + skipped + " §f位玩家已封鎖你，已略過" : ""));
    }

    public void handleTpacceptCommand(Player acceptor, String[] args) {
        ensureMainThread();

        if (args.length < 1) {
            acceptor.sendMessage(Messages.PREFIX + "§c用法: /tpaccept <玩家>");
            return;
        }

        Player requester = Bukkit.getPlayer(args[0]);
        if (requester == null || !requester.isOnline()) {
            acceptor.sendMessage(Messages.PREFIX + "§f該玩家不在線上");
            return;
        }

        UUID acc = acceptor.getUniqueId();
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

        Player sender = Bukkit.getPlayer(args[0]);
        if (sender == null || !sender.isOnline()) {
            receiver.sendMessage(Messages.PREFIX + "§f該玩家不在線上");
            return;
        }

        UUID rec = receiver.getUniqueId();
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
    }

    public void delayedTeleport(Player player, Location target, String name) {
        ensureMainThread();

        Location start = player.getLocation().clone();
        player.sendMessage(Messages.PREFIX + "§f將在 §e" + TELEPORT_DELAY_SEC + " 秒 §f後傳送到 §a" + name + " §f請不要移動..");

        new BukkitRunnable() {
            int count = TELEPORT_DELAY_SEC;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    return;
                }

                Location now = player.getLocation();
                if (now.getWorld() != start.getWorld() || now.distanceSquared(start) > MOVE_CANCEL_DISTANCE_SQ) {
                    player.sendMessage(Messages.PREFIX + "§c傳送已取消，因為你移動了。");
                    cancel();
                    return;
                }

                if (count <= 0) {
                    player.teleport(target);
                    player.sendMessage(Messages.PREFIX + "§a已傳送到 §f" + name);
                    cancel();
                    return;
                }

                player.sendMessage("§7將在 §e" + count + "§7 秒後傳送..");
                count--;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private record RequestKey(UUID receiver, UUID requester) {}
}