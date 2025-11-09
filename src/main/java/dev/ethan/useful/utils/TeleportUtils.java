package dev.ethan.useful.utils;

import dev.ethan.useful.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class TeleportUtils {
    private static final long REQUEST_EXPIRE = 5 * 60 * 1000;

    public static final Map<UUID, Map<UUID, Long>> tpaRequests = new HashMap<>();
    public static final Map<UUID, Map<UUID, Long>> tpahereRequests = new HashMap<>();

    public static final Map<UUID, Set<UUID>> blocks = new HashMap<>();
    private static final Map<UUID, Long> cooldowns = new HashMap<>();

    private static File file;
    private static YamlConfiguration config;

    public static void init(JavaPlugin plugin) {
        file = new File(plugin.getDataFolder(), "blocks.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ignored) {}
        }
        config = YamlConfiguration.loadConfiguration(file);

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

    public static void save() {
        if (config == null) return;
        Bukkit.getScheduler().runTaskAsynchronously(JavaPlugin.getProvidingPlugin(TeleportUtils.class), () -> {
            synchronized (TeleportUtils.class) {
                for (String key : new HashSet<>(config.getKeys(false))) config.set(key, null);
                for (Map.Entry<UUID, Set<UUID>> e : blocks.entrySet()) {
                    List<String> list = new ArrayList<>();
                    for (UUID u : e.getValue()) list.add(u.toString());
                    config.set(e.getKey().toString(), list);
                }
                try { config.save(file); } catch (IOException ignored) {}
            }
        });
    }

    public static boolean isBlocked(UUID receiver, UUID sender) {
        Set<UUID> set = blocks.get(receiver);
        return set != null && set.contains(sender);
    }

    private static void addRequest(Map<UUID, Map<UUID, Long>> map, UUID receiver, UUID requester) {
        map.computeIfAbsent(receiver, k -> new HashMap<>()).put(requester, System.currentTimeMillis() + REQUEST_EXPIRE);
    }

    private static boolean hasRequest(Map<UUID, Map<UUID, Long>> map, UUID receiver, UUID requester) {
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

    private static void removeRequest(Map<UUID, Map<UUID, Long>> map, UUID receiver, UUID requester) {
        Map<UUID, Long> inner = map.get(receiver);
        if (inner != null) {
            inner.remove(requester);
            if (inner.isEmpty()) map.remove(receiver);
        }
    }

    private static boolean checkCooldown(Player sender) {
        long now = System.currentTimeMillis();
        long cd = cooldowns.getOrDefault(sender.getUniqueId(), 0L);
        if (now < cd) {
            long sec = (cd - now) / 1000;
            sender.sendMessage(Main.Plugin_Prefix + "§f冷卻中，請等待 " + sec + " 秒");
            return false;
        }
        cooldowns.put(sender.getUniqueId(), now + 3000); // 3 秒冷卻
        return true;
    }

    public static void handleTpaCommand(Player sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(Main.Plugin_Prefix + "§c用法: /tpa <玩家>");
            return;
        }
        if (!checkCooldown(sender)) return;

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline() || !sender.canSee(target)) {
            sender.sendMessage(Main.Plugin_Prefix + "§f找不到該玩家或該玩家不在線上");
            return;
        }
        if (target.equals(sender)) {
            sender.sendMessage(Main.Plugin_Prefix + "§f不能傳送到自己");
            return;
        }
        if (isBlocked(target.getUniqueId(), sender.getUniqueId())) {
            sender.sendMessage(Main.Plugin_Prefix + "§f" + LuckPermsUtils.getPlayerPrefix(target) + target.getDisplayName() + " §f已封鎖你的請求");
            return;
        }

        addRequest(tpaRequests, target.getUniqueId(), sender.getUniqueId());

        Component accept = Component.text("✔", NamedTextColor.GREEN)
                .hoverEvent(HoverEvent.showText(Component.text("同意", NamedTextColor.GREEN)))
                .clickEvent(ClickEvent.runCommand("/tpaccept " + sender.getName()));
        Component deny = Component.text(" ❌", NamedTextColor.RED)
                .hoverEvent(HoverEvent.showText(Component.text("拒絕", NamedTextColor.RED)))
                .clickEvent(ClickEvent.runCommand("/tpdeny " + sender.getName()));
        Component message = Component.text(Main.Plugin_Prefix)
                .append(Component.text(LuckPermsUtils.getPlayerPrefix(sender) + sender.getDisplayName(), NamedTextColor.WHITE))
                .append(Component.text(" §f想傳送到你這裡 "))
                .append(accept).append(deny);
        target.sendMessage(message);
        sender.sendMessage(Main.Plugin_Prefix + "§f已向 " + LuckPermsUtils.getPlayerPrefix(target) + target.getDisplayName() + " §f發送請求");
    }

    public static void handleTpahereCommand(Player sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(Main.Plugin_Prefix + "§c用法: /tpahere <玩家|all>");
            return;
        }
        if (!checkCooldown(sender)) return;

        List<Player> targets = new ArrayList<>();
        if (args[0].equalsIgnoreCase("all")) {
            if (!sender.hasPermission("useful.admin")) {
                sender.sendMessage(Main.Plugin_Prefix + "§f你沒有權限使用此子指令");
                return;
            }
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!p.equals(sender) && sender.canSee(p)) targets.add(p);
            }
        } else {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null || !target.isOnline() || !sender.canSee(target)) {
                sender.sendMessage(Main.Plugin_Prefix + "§f找不到該玩家或該玩家不在線上");
                return;
            }
            if (target.equals(sender)) {
                sender.sendMessage(Main.Plugin_Prefix + "§f不能對自己使用");
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
            Component message = Component.text(Main.Plugin_Prefix)
                    .append(Component.text(LuckPermsUtils.getPlayerPrefix(sender) + sender.getDisplayName(), NamedTextColor.WHITE))
                    .append(Component.text(" §f想你傳送到他那裡 "))
                    .append(accept).append(deny);
            target.sendMessage(message);
            sent++;
        }
        sender.sendMessage(Main.Plugin_Prefix + "§f已向 " + sent + " §f位玩家發送傳送請求" + (skipped > 0 ? "，其中 " + skipped + " §f位玩家已封鎖你，已略過" : ""));
    }

    public static void handleTpacceptCommand(Player acceptor, String[] args) {
        if (args.length < 1) {
            acceptor.sendMessage(Main.Plugin_Prefix + "§c用法: /tpaccept <玩家>");
            return;
        }
        Player requester = Bukkit.getPlayer(args[0]);
        if (requester == null || !requester.isOnline()) {
            acceptor.sendMessage(Main.Plugin_Prefix + "§f該玩家不在線上");
            return;
        }

        if (hasRequest(tpahereRequests, acceptor.getUniqueId(), requester.getUniqueId())) {
            removeRequest(tpahereRequests, acceptor.getUniqueId(), requester.getUniqueId());
            acceptor.teleport(requester.getLocation());
            acceptor.sendMessage(Main.Plugin_Prefix + "§f你已傳送到 " + LuckPermsUtils.getPlayerPrefix(requester) + requester.getDisplayName());
            requester.sendMessage(Main.Plugin_Prefix + LuckPermsUtils.getPlayerPrefix(acceptor) + acceptor.getDisplayName() + " §f已傳送到你這裡");
        } else if (hasRequest(tpaRequests, acceptor.getUniqueId(), requester.getUniqueId())) {
            removeRequest(tpaRequests, acceptor.getUniqueId(), requester.getUniqueId());
            requester.teleport(acceptor.getLocation());
            requester.sendMessage(Main.Plugin_Prefix + "§f你已傳送到 " + LuckPermsUtils.getPlayerPrefix(acceptor) + acceptor.getDisplayName());
            acceptor.sendMessage(Main.Plugin_Prefix + LuckPermsUtils.getPlayerPrefix(requester) + requester.getDisplayName() + " §f已傳送到你這裡");
        } else {
            acceptor.sendMessage(Main.Plugin_Prefix + "§f該玩家沒有對你發送請求或已過期");
        }
    }

    public static void handleTpdenyCommand(Player receiver, String[] args) {
        if (args.length < 1) {
            receiver.sendMessage(Main.Plugin_Prefix + "§c用法: /tpdeny <玩家>");
            return;
        }
        Player sender = Bukkit.getPlayer(args[0]);
        if (sender == null || !sender.isOnline()) {
            receiver.sendMessage(Main.Plugin_Prefix + "§f該玩家不在線上");
            return;
        }

        if (hasRequest(tpahereRequests, receiver.getUniqueId(), sender.getUniqueId())) {
            removeRequest(tpahereRequests, receiver.getUniqueId(), sender.getUniqueId());
            sender.sendMessage(Main.Plugin_Prefix + LuckPermsUtils.getPlayerPrefix(receiver) + receiver.getDisplayName() + " §f拒絕了你的請求");
            receiver.sendMessage(Main.Plugin_Prefix + "§f你已拒絕請求");
        } else if (hasRequest(tpaRequests, receiver.getUniqueId(), sender.getUniqueId())) {
            removeRequest(tpaRequests, receiver.getUniqueId(), sender.getUniqueId());
            sender.sendMessage(Main.Plugin_Prefix + LuckPermsUtils.getPlayerPrefix(receiver) + receiver.getDisplayName() + " §f拒絕了你的請求");
            receiver.sendMessage(Main.Plugin_Prefix + "§f你已拒絕請求");
        } else {
            receiver.sendMessage(Main.Plugin_Prefix + "§f該玩家沒有對你發送請求或已過期");
        }
    }

    private static Set<UUID> getBlockSet(UUID blocker) {
        return blocks.computeIfAbsent(blocker, k -> new HashSet<>());
    }

    public static void handleBlockCommand(Player blocker, String[] args) {
        if (args.length < 1) {
            blocker.sendMessage(Main.Plugin_Prefix + "§c用法: /block <玩家>");
            return;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (target.getUniqueId().equals(blocker.getUniqueId())) {
            blocker.sendMessage(Main.Plugin_Prefix + "§f不能封鎖自己");
            return;
        }
        Set<UUID> set = getBlockSet(blocker.getUniqueId());
        if (!set.add(target.getUniqueId())) {
            blocker.sendMessage(Main.Plugin_Prefix + "§f你已封鎖過 " + LuckPermsUtils.getPlayerPrefix(target) + target.getName());
        } else {
            save();
            blocker.sendMessage(Main.Plugin_Prefix + "§f已封鎖 " + LuckPermsUtils.getPlayerPrefix(target) + target.getName() + " §f的傳送與私訊請求");
        }
    }

    public static void handleUnblockCommand(Player blocker, String[] args) {
        if (args.length < 1) {
            blocker.sendMessage(Main.Plugin_Prefix + "§c用法: /unblock <玩家>");
            return;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        Set<UUID> set = getBlockSet(blocker.getUniqueId());
        if (!set.remove(target.getUniqueId())) {
            blocker.sendMessage(Main.Plugin_Prefix + "§f你並未封鎖 " + LuckPermsUtils.getPlayerPrefix(target) + target.getName());
        } else {
            if (set.isEmpty()) blocks.remove(blocker.getUniqueId());
            save();
            blocker.sendMessage(Main.Plugin_Prefix + "§f已解除封鎖 " + LuckPermsUtils.getPlayerPrefix(target) + target.getName());
        }
    }

    public static void handleBlockListCommand(Player viewer) {
        Set<UUID> set = blocks.getOrDefault(viewer.getUniqueId(), Collections.emptySet());
        if (set.isEmpty()) {
            viewer.sendMessage(Main.Plugin_Prefix + "§f你沒有封鎖任何人");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (UUID u : set) {
            OfflinePlayer p = Bukkit.getOfflinePlayer(u);
            String name = p.getName() != null ? LuckPermsUtils.getPlayerPrefix(p) + p.getName() : "§7(未知玩家)";
            if (!sb.isEmpty()) sb.append("§7, §f");
            sb.append(name);
        }
        viewer.sendMessage(Main.Plugin_Prefix + "§f已封鎖: " + sb);
    }
}