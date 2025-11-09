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

public class TeleportUtil {
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
            } catch (IOException ignored) {
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
        for (String str : config.getKeys(false)) {
            try {
                UUID blocker = UUID.fromString(str);
                List<String> blockedList = config.getStringList(str);
                Set<UUID> blocked = new HashSet<>();
                for (String s : blockedList) {
                    try {
                        blocked.add(UUID.fromString(s));
                    } catch (IllegalArgumentException ignored) {
                    }
                }
                blocks.put(blocker, blocked);
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    public static void save() {
        if (config == null) return;
        Bukkit.getScheduler().runTaskAsynchronously(JavaPlugin.getProvidingPlugin(TeleportUtil.class), () -> {
            synchronized (TeleportUtil.class) {
                for (String key : new HashSet<>(config.getKeys(false))) config.set(key, null);
                for (Map.Entry<UUID, Set<UUID>> e : blocks.entrySet()) {
                    List<String> list = new ArrayList<>();
                    for (UUID u : e.getValue()) list.add(u.toString());
                    config.set(e.getKey().toString(), list);
                }
                try {
                    config.save(file);
                } catch (IOException ignored) {
                }
            }
        });
    }

    public static boolean isBlocked(UUID r, UUID s) {
        Set<UUID> set = blocks.get(r);
        return set != null && set.contains(s);
    }

    private static void addRequest(Map<UUID, Map<UUID, Long>> map, UUID rec, UUID req) {
        map.computeIfAbsent(rec, k -> new HashMap<>()).put(req, System.currentTimeMillis() + REQUEST_EXPIRE);
    }

    private static boolean hasRequest(Map<UUID, Map<UUID, Long>> map, UUID rec, UUID req) {
        Map<UUID, Long> inner = map.get(rec);
        if (inner == null) return false;
        Long expire = inner.get(req);
        if (expire == null) return false;
        if (System.currentTimeMillis() > expire) {
            inner.remove(req);
            if (inner.isEmpty()) map.remove(rec);
            return false;
        }
        return true;
    }

    private static void removeRequest(Map<UUID, Map<UUID, Long>> map, UUID rec, UUID req) {
        Map<UUID, Long> inner = map.get(rec);
        if (inner != null) {
            inner.remove(req);
            if (inner.isEmpty()) map.remove(rec);
        }
    }

    private static boolean checkCooldown(Player p) {
        long now = System.currentTimeMillis();
        long cd = cooldowns.getOrDefault(p.getUniqueId(), 0L);
        if (now < cd) {
            long sec = (cd - now) / 1000;
            p.sendMessage(Main.Plugin_Prefix + "§c請等待 " + sec + " §c秒");
            return true;
        }
        cooldowns.put(p.getUniqueId(), now + 3000);
        return false;
    }

    public static void handleTpaCommand(Player p, String[] args) {
        if (args.length < 1) {
            p.sendMessage(Main.Plugin_Prefix + "§c用法: /tpa <玩家>");
            return;
        }
        if (checkCooldown(p)) return;
        Player t = Bukkit.getPlayer(args[0]);
        if (t == null || !t.isOnline() || !p.canSee(t)) {
            p.sendMessage(Main.Plugin_Prefix + "§f找不到該玩家或該玩家不在線上");
            return;
        }
        if (t.equals(p)) {
            p.sendMessage(Main.Plugin_Prefix + "§f不能傳送到自己");
            return;
        }
        if (isBlocked(t.getUniqueId(), p.getUniqueId())) {
            p.sendMessage(Main.Plugin_Prefix + "§f" + LuckPermsUtil.getPlayerPrefix(t) + t.getDisplayName() + " §f已封鎖你的請求");
            return;
        }
        addRequest(tpaRequests, t.getUniqueId(), p.getUniqueId());
        Component accept = Component.text("✔", NamedTextColor.GREEN)
                .hoverEvent(HoverEvent.showText(Component.text("同意", NamedTextColor.GREEN)))
                .clickEvent(ClickEvent.runCommand("/tpaccept " + p.getName()));
        Component deny = Component.text(" ❌", NamedTextColor.RED)
                .hoverEvent(HoverEvent.showText(Component.text("拒絕", NamedTextColor.RED)))
                .clickEvent(ClickEvent.runCommand("/tpdeny " + p.getName()));
        Component message = Component.text(Main.Plugin_Prefix)
                .append(Component.text(LuckPermsUtil.getPlayerPrefix(p) + p.getDisplayName(), NamedTextColor.WHITE))
                .append(Component.text(" §f想傳送到你這裡 "))
                .append(accept).append(deny);
        t.sendMessage(message);
        p.sendMessage(Main.Plugin_Prefix + "§f已向 " + LuckPermsUtil.getPlayerPrefix(t) + t.getDisplayName() + " §f發送請求");
    }

    //todo: check if this works, if no we change back to target
    public static void handleTpahereCommand(Player p, String[] args) {
        if (args.length < 1) {
            p.sendMessage(Main.Plugin_Prefix + "§c用法: /tpahere <玩家|all>");
            return;
        }
        if (checkCooldown(p)) return;
        List<Player> targets = new ArrayList<>();
        if (args[0].equalsIgnoreCase("all")) {
            if (!p.hasPermission("useful.admin")) {
                p.sendMessage(Main.Plugin_Prefix + "§f你沒有權限使用此子指令");
                return;
            }
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!player.equals(p) && p.canSee(player)) targets.add(player);
            }
        } else {
            Player t = Bukkit.getPlayer(args[0]);
            if (t == null || !t.isOnline() || !p.canSee(t)) {
                p.sendMessage(Main.Plugin_Prefix + "§f找不到該玩家或該玩家不在線上");
                return;
            }
            if (t.equals(p)) {
                p.sendMessage(Main.Plugin_Prefix + "§f不能對自己使用");
                return;
            }
            targets.add(t);
        }
        int sent = 0, skipped = 0;
        for (Player t : targets) {
            if (isBlocked(t.getUniqueId(), p.getUniqueId())) {
                skipped++;
                continue;
            }
            addRequest(tpahereRequests, t.getUniqueId(), p.getUniqueId());
            Component accept = Component.text("✔", NamedTextColor.GREEN)
                    .hoverEvent(HoverEvent.showText(Component.text("同意", NamedTextColor.GREEN)))
                    .clickEvent(ClickEvent.runCommand("/tpaccept " + p.getName()));
            Component deny = Component.text(" ❌", NamedTextColor.RED)
                    .hoverEvent(HoverEvent.showText(Component.text("拒絕", NamedTextColor.RED)))
                    .clickEvent(ClickEvent.runCommand("/tpdeny " + p.getName()));
            Component message = Component.text(Main.Plugin_Prefix)
                    .append(Component.text(LuckPermsUtil.getPlayerPrefix(p) + p.getDisplayName(), NamedTextColor.WHITE))
                    .append(Component.text(" §f想你傳送到他那裡 "))
                    .append(accept).append(deny);
            t.sendMessage(message);
            sent++;
        }
        p.sendMessage(Main.Plugin_Prefix + "§f已向 " + sent + " §f位玩家發送傳送請求" + (skipped > 0 ? " §f已略過其中封鎖你的 " + skipped + " §f位玩家" : ""));
    }

    public static void handleTpacceptCommand(Player p, String[] args) {
        if (args.length < 1) {
            p.sendMessage(Main.Plugin_Prefix + "§c用法: /tpaccept <玩家>");
            return;
        }
        Player req = Bukkit.getPlayer(args[0]);
        if (req == null || !req.isOnline()) {
            p.sendMessage(Main.Plugin_Prefix + "§f該玩家不在線上");
            return;
        }
        if (hasRequest(tpahereRequests, p.getUniqueId(), req.getUniqueId())) {
            removeRequest(tpahereRequests, p.getUniqueId(), req.getUniqueId());
            p.teleport(req.getLocation());
            p.sendMessage(Main.Plugin_Prefix + "§f你已傳送到 " + LuckPermsUtil.getPlayerPrefix(req) + req.getDisplayName());
            req.sendMessage(Main.Plugin_Prefix + LuckPermsUtil.getPlayerPrefix(p) + p.getDisplayName() + " §f已傳送到你這裡");
        } else if (hasRequest(tpaRequests, p.getUniqueId(), req.getUniqueId())) {
            removeRequest(tpaRequests, p.getUniqueId(), req.getUniqueId());
            req.teleport(p.getLocation());
            req.sendMessage(Main.Plugin_Prefix + "§f你已傳送到 " + LuckPermsUtil.getPlayerPrefix(p) + p.getDisplayName());
            p.sendMessage(Main.Plugin_Prefix + LuckPermsUtil.getPlayerPrefix(req) + req.getDisplayName() + " §f已傳送到你這裡");
        } else {
            p.sendMessage(Main.Plugin_Prefix + "§f該玩家沒有對你發送請求或已過期");
        }
    }

    public static void handleTpdenyCommand(Player p, String[] args) {
        if (args.length < 1) {
            p.sendMessage(Main.Plugin_Prefix + "§c用法: /tpdeny <玩家>");
            return;
        }
        Player sender = Bukkit.getPlayer(args[0]);
        if (sender == null || !sender.isOnline()) {
            p.sendMessage(Main.Plugin_Prefix + "§f該玩家不在線上");
            return;
        }

        if (hasRequest(tpahereRequests, p.getUniqueId(), sender.getUniqueId())) {
            removeRequest(tpahereRequests, p.getUniqueId(), sender.getUniqueId());
            sender.sendMessage(Main.Plugin_Prefix + LuckPermsUtil.getPlayerPrefix(p) + p.getDisplayName() + " §f拒絕了你的請求");
            p.sendMessage(Main.Plugin_Prefix + "§f你已拒絕請求");
        } else if (hasRequest(tpaRequests, p.getUniqueId(), sender.getUniqueId())) {
            removeRequest(tpaRequests, p.getUniqueId(), sender.getUniqueId());
            sender.sendMessage(Main.Plugin_Prefix + LuckPermsUtil.getPlayerPrefix(p) + p.getDisplayName() + " §f拒絕了你的請求");
            p.sendMessage(Main.Plugin_Prefix + "§f你已拒絕請求");
        } else {
            p.sendMessage(Main.Plugin_Prefix + "§f該玩家沒有對你發送請求或已過期");
        }
    }

    private static Set<UUID> getBlockSet(UUID uuid) {
        return blocks.computeIfAbsent(uuid, k -> new HashSet<>());
    }

    public static void handleBlockCommand(Player p, String[] args) {
        if (args.length < 1) {
            p.sendMessage(Main.Plugin_Prefix + "§c用法: /block <玩家>");
            return;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (target.getUniqueId().equals(p.getUniqueId())) {
            p.sendMessage(Main.Plugin_Prefix + "§f不能封鎖自己");
            return;
        }
        Set<UUID> set = getBlockSet(p.getUniqueId());
        if (!set.add(target.getUniqueId())) {
            p.sendMessage(Main.Plugin_Prefix + "§f你已封鎖過 " + LuckPermsUtil.getPlayerPrefix(target) + target.getName());
        } else {
            save();
            p.sendMessage(Main.Plugin_Prefix + "§f已封鎖 " + LuckPermsUtil.getPlayerPrefix(target) + target.getName() + " §f的傳送與私訊請求");
        }
    }

    public static void handleUnblockCommand(Player p, String[] args) {
        if (args.length < 1) {
            p.sendMessage(Main.Plugin_Prefix + "§c用法: /unblock <玩家>");
            return;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        Set<UUID> set = getBlockSet(p.getUniqueId());
        if (!set.remove(target.getUniqueId())) {
            p.sendMessage(Main.Plugin_Prefix + "§f你並未封鎖 " + LuckPermsUtil.getPlayerPrefix(target) + target.getName());
        } else {
            if (set.isEmpty()) blocks.remove(p.getUniqueId());
            save();
            p.sendMessage(Main.Plugin_Prefix + "§f已解除封鎖 " + LuckPermsUtil.getPlayerPrefix(target) + target.getName());
        }
    }

    public static void handleBlockListCommand(Player p) {
        Set<UUID> set = blocks.getOrDefault(p.getUniqueId(), Collections.emptySet());
        if (set.isEmpty()) {
            p.sendMessage(Main.Plugin_Prefix + "§f你沒有封鎖任何人");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (UUID u : set) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(u);
            String name = player.getName() != null ? LuckPermsUtil.getPlayerPrefix(player) + player.getName() : "§7(未知玩家)";
            if (!sb.isEmpty()) sb.append("§7, §f");
            sb.append(name);
        }
        p.sendMessage(Main.Plugin_Prefix + "§f已封鎖: " + sb);
    }
}