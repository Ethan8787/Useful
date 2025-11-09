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
import net.kyori.adventure.text.format.TextDecoration;
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
import static dev.ethan.useful.utils.CrasherUtils.*;
import static dev.ethan.useful.utils.LuckPermsUtils.getPlayerPrefix;

public class GameCommands implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) return true;

        switch (command.getName().toLowerCase()) {
            case "kms" -> handleKms(player);
            case "heal" -> handleHeal(player, args);
            case "god" -> handleGod(player, args);
            case "boom" -> handleBoom(player, args);
            case "gms" -> handleGamemode(player, GameMode.SURVIVAL, args);
            case "gmc" -> handleGamemode(player, GameMode.CREATIVE, args);
            case "gma" -> handleGamemode(player, GameMode.ADVENTURE, args);
            case "gmsp" -> handleGamemode(player, GameMode.SPECTATOR, args);
            case "sword" -> handleSword(player);
            case "fly" -> handleFly(player, args);
            case "gun" -> handleGun(player);
            case "hat" -> handleHat(player);
            case "dupe" -> handleDupe(player);
            case "l" -> handleLobby(player, args);
            case "sudo" -> handleSudo(sender, args);
            case "nick" -> handleNick(player, args);
            case "unnick" -> handleUnnick(player);
            case "freeze" -> handleFreeze(sender, args);
            case "unfreeze" -> handleUnfreeze(sender, args);
            case "ips" -> handleIps(sender, args);
            case "alts" -> handleAlts(sender, args);
            case "bot" -> handleBot(player, args);
            case "botf" -> handleBotFollow(player, args);
            case "removenpc" -> NpcUtils.removeAllNPCs();
            case "r" -> handleReply(player, args);
            case "msg", "w", "tell" -> handleMessage(player, label, args);
            case "dmlisten" -> handleDmToggle(player);
            case "explosion" -> handleExplosion(sender, args);
            case "particle" -> handleParticle(sender, args);
            case "position" -> handlePosition(sender, args);
            case "nuke" -> handleNuke(sender, args);
            case "sethome" -> HomeUtils.handleSetHome(player, args);
            case "homes" -> HomeUtils.handleHomes(player);
            case "home" -> HomeUtils.handleHome(player, args);
            case "delhome" -> HomeUtils.handleDelHome(player, args);
            case "tpa" -> TeleportUtils.handleTpaCommand(player, args);
            case "tpahere" -> TeleportUtils.handleTpahereCommand(player, args);
            case "tpaccept" -> TeleportUtils.handleTpacceptCommand(player, args);
            case "tpdeny" -> TeleportUtils.handleTpdenyCommand(player, args);
            case "block" -> TeleportUtils.handleBlockCommand(player, args);
            case "unblock" -> TeleportUtils.handleUnblockCommand(player, args);
            case "blocklist" -> TeleportUtils.handleBlockListCommand(player);
            case "uuid" -> handleUUID(player, args);
            default -> { return false; }
        }
        return true;
    }

    private void handleKms(Player p) {
        p.setHealth(0.0F);
    }

    private void handleHeal(Player player, String[] args) {
        if (args.length == 0) {
            if (!player.hasPermission("useful.heal")) return;
            player.setHealth(20);
            player.setFoodLevel(20);
            player.sendMessage(Plugin_Prefix + "§d已治癒");
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
            return;
        }
        try {
            double value = Double.parseDouble(args[0]);
            if (player.hasPermission("useful.heal.amount")) {
                player.setHealth(value);
                player.sendMessage(Plugin_Prefix + "§d已設定生命值為 " + value);
                return;
            }
        } catch (NumberFormatException ignored) {}

        if (player.hasPermission("useful.heal.others")) {
            Player t = Bukkit.getPlayer(args[0]);
            if (t == null) {
                player.sendMessage(Plugin_Prefix + "§c玩家不存在或離線");
                return;
            }
            t.setHealth(Objects.requireNonNull(t.getAttribute(Attribute.MAX_HEALTH)).getValue());
            t.setFoodLevel(20);
            t.sendMessage(Plugin_Prefix + "§d你被 " + getPlayerPrefix(player) + player.getName() + " §d治癒了");
            player.sendMessage(Plugin_Prefix + "§d你治癒了 " + getPlayerPrefix(t) + t.getName());
        } else player.sendMessage(Plugin_Prefix + "§c用法: /heal <玩家名稱|血量>");
    }

    private void handleGamemode(Player player, GameMode mode, String[] args) {
        if (args.length == 0) {
            player.setGameMode(mode);
            player.sendMessage(Plugin_Prefix + "§f您的遊戲模式已更新");
            return;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(Plugin_Prefix + "§c玩家不存在");
            return;
        }
        target.setGameMode(mode);
        player.sendMessage(Plugin_Prefix + "§f已更新 " + getPlayerPrefix(target) + target.getName() + " §f的遊戲模式");
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

    private void handleBoom(Player player, String[] args) {
        if (args.length != 1) {
            player.sendMessage(Plugin_Prefix + "§c用法: /boom <玩家|all>");
            return;
        }
        if (args[0].equalsIgnoreCase("all")) {
            Bukkit.getOnlinePlayers().forEach(p -> launchFirework(player, p));
            player.sendMessage(Plugin_Prefix + "§a你將所有玩家射向高空");
        } else {
            Player t = Bukkit.getPlayer(args[0]);
            if (t == null) {
                player.sendMessage(Plugin_Prefix + "§c玩家不存在");
                return;
            }
            launchFirework(player, t);
        }
    }

    private void launchFirework(Player shooter, Player target) {
        Location loc = target.getLocation();
        Firework firework = (Firework) Objects.requireNonNull(loc.getWorld())
                .spawnEntity(loc, EntityType.FIREWORK_ROCKET);

        FireworkMeta meta = firework.getFireworkMeta();
        meta.setPower(5);

        // same RGB color palette from /boom
        Color[] palette = new Color[]{
                Color.fromRGB(12801229), Color.fromRGB(8073150), Color.fromRGB(11743532),
                Color.fromRGB(14188952), Color.fromRGB(14602026), Color.fromRGB(15435844),
                Color.fromRGB(15790320)
        };

        // build same seven effects
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
        firework.addPassenger(target);

        // flight motion logic
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

        target.sendMessage(Plugin_Prefix + ChatColor.WHITE +
                "Launched by " + getPlayerPrefix(shooter) + shooter.getName());
        shooter.sendMessage(Plugin_Prefix + ChatColor.GREEN +
                "Fireworks launched for " + getPlayerPrefix(target) + target.getName());
        shooter.playSound(shooter.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
    }

    private void handleBotFollow(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(Plugin_Prefix + "§cUsage: /botf <name> <player>");
            return;
        }

        Player toFollow = Bukkit.getPlayer(args[1]);
        if (toFollow == null) {
            player.sendMessage(Plugin_Prefix + "§c找不到玩家 " + args[1]);
            return;
        }

        player.sendMessage(Plugin_Prefix + "§a已生成 NPC：" + args[0] + "§a，跟隨 " + toFollow.getName());
        NpcUtils.spawnFakePlayer(Main.getInstance(), player.getLocation(), args[0], toFollow);
    }

    private void handleBot(Player player, String[] args) {
        if (args.length < 1) {
            player.sendMessage(Plugin_Prefix + "§cUsage: /bot <name>");
            return;
        }

        String botName = args[0];
        player.sendMessage(Plugin_Prefix + "§a已生成機器人：" + botName);
        NpcUtils.spawnBot(player.getLocation(), botName);
    }

    private void handleDupe(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            player.sendMessage(Plugin_Prefix + "§c手上沒有物品");
            return;
        }
        ItemStack clone = item.clone();
        player.getInventory().addItem(clone);
    }

    private void handleHat(Player player) {
        ItemStack hand = player.getInventory().getItemInMainHand();
        if (hand.getType() == Material.AIR) {
            player.sendMessage(Plugin_Prefix + "§c手中物品無效");
            return;
        }

        ItemStack oldHelmet = player.getInventory().getHelmet();
        player.getInventory().setHelmet(hand);
        player.getInventory().setItemInMainHand(oldHelmet != null ? oldHelmet : new ItemStack(Material.AIR));
        player.sendMessage(Plugin_Prefix + "§a已將手中物品裝備在頭盔欄位");
    }

    private void handleFly(Player player, String[] args) {
        Player t = args.length == 0 ? player : Bukkit.getPlayer(args[0]);
        if (t == null) {
            player.sendMessage(Plugin_Prefix + "§c玩家不存在");
            return;
        }
        t.setAllowFlight(!t.getAllowFlight());
        player.sendMessage(Plugin_Prefix + getPlayerPrefix(t) + t.getName() + " §f的飛行狀態 " + (t.getAllowFlight() ? "§aOn" : "§cOff"));
        t.sendMessage(Plugin_Prefix + "§f飛行狀態 " + (t.getAllowFlight() ? "§aOn" : "§cOff"));
    }

    private void handleGun(Player player) {
        ItemStack weapon = new ItemStack(Material.FEATHER);
        ItemMeta meta = weapon.getItemMeta();
        if (meta != null) {
            String name = VandalName;
            meta.setDisplayName(ChatColor.RESET + name);
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Effective Range: " + ChatColor.WHITE + "80m");
            meta.setLore(lore);
            meta.addEnchant(Enchantment.UNBREAKING, -32769, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            NamespacedKey key = new NamespacedKey(Main.getInstance(), "range");
            double amount = -5;
            AttributeModifier.Operation op = AttributeModifier.Operation.ADD_SCALAR;
            EquipmentSlotGroup slotGroup = EquipmentSlotGroup.HAND;
            AttributeModifier modifier = new AttributeModifier(key, amount, op, slotGroup);
            meta.addAttributeModifier(Attribute.BLOCK_INTERACTION_RANGE, modifier);
            meta.addAttributeModifier(Attribute.ENTITY_INTERACTION_RANGE, modifier);
            weapon.setItemMeta(meta);
        }
        player.getInventory().addItem(weapon);
    }

    private void handleSword(Player player) {
        ItemStack sword = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta meta = sword.getItemMeta();
        meta.displayName(Component.text("昇華・蒼界之刃")
                .color(NamedTextColor.DARK_RED)
                .decoration(TextDecoration.ITALIC, false)
                .decorate(TextDecoration.BOLD));
        meta.addEnchant(Enchantment.SHARPNESS, 10, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        sword.setItemMeta(meta);
        player.getInventory().addItem(sword);
    }

    private void handleLobby(Player p, String[] args) {
        if (args.length > 0) {
            p.sendMessage(Plugin_Prefix + "§c用法: /l");
            return;
        }
        PlayerUtils.sendToServer(p, "lobby");
    }

    private void handleSudo(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Plugin_Prefix + "§c用法: /sudo <玩家> <指令>");
            return;
        }
        Player t = Bukkit.getPlayer(args[0]);
        if (t == null) {
            sender.sendMessage(Plugin_Prefix + "§c玩家不存在");
            return;
        }
        String msg = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        if (msg.startsWith("/")) Bukkit.dispatchCommand(t, msg.substring(1));
        else t.chat(msg);
    }

    private void handleNick(Player player, String[] args) {
        if (args.length < 1) {
            player.sendMessage(Plugin_Prefix + "§fUsage: /nick <nickname> [--skin]");
            return;
        }
        String nick = args[0].replace("&", "§");
        boolean skin = Arrays.asList(args).contains("--skin");
        nickStorage.setNickname(player, nick);
        player.setDisplayName(nick);
        player.setPlayerListName(nick);

        Disguise.Builder builder = Disguise.builder().setName(nick).setEntityType(EntityType.PLAYER);
        builder.setSkin(SkinAPI.MOJANG, player.getUniqueId());
        Disguise disguise = builder.build();
        DisguiseManager.getProvider().disguise(player, disguise);

        player.sendMessage(Plugin_Prefix + "§fNick set: " + getPlayerPrefix(player) + nick + (skin ? " §7(Skin)" : ""));
    }

    private void handleUnnick(Player player) {
        if (nickStorage.getNickname(player) == null) {
            player.sendMessage(Plugin_Prefix + "§fYou are not nicked.");
            return;
        }
        DisguiseManager.getProvider().undisguise(player);
        nickStorage.removeNicknameSync(player);
        player.setDisplayName(player.getName());
        player.setPlayerListName(player.getName());
        player.sendMessage(Plugin_Prefix + "§fUnnicked.");
    }

    private void handleFreeze(CommandSender sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(Plugin_Prefix + "§c用法: /freeze <玩家>");
            return;
        }
        Player target = Bukkit.getPlayer(args[0]);
        frozenPlayers.add(target.getName());
        sender.sendMessage(Plugin_Prefix + "§c你已凍結" + getPlayerPrefix(target) + target.getName());
        target.sendMessage(Plugin_Prefix + "§c你已被凍結");
    }

    private void handleUnfreeze(CommandSender sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(Plugin_Prefix + "§c用法: /unfreeze <玩家>");
            return;
        }
        Player t = Bukkit.getPlayer(args[0]);
        frozenPlayers.remove(t.getName());
        sender.sendMessage(Plugin_Prefix + "§c你已解凍" + getPlayerPrefix(t) + t.getName());
        t.sendMessage(Plugin_Prefix + "§a你已被解凍");
    }

    private void handleIps(CommandSender sender, String[] args) {
        if (!sender.hasPermission("useful.ips")) return;

        if (args.length != 1) {
            sender.sendMessage(Plugin_Prefix + "§c用法: /ips <玩家>");
            return;
        }

        String playerName = args[0];

        if (!ipsConfig.contains(playerName)) {
            sender.sendMessage(Plugin_Prefix + "§c找不到玩家 §f" + playerName + " §c的紀錄");
            return;
        }

        List<String> ipList = ipsConfig.getStringList(playerName);
        if (ipList == null || ipList.isEmpty()) {
            sender.sendMessage(Plugin_Prefix + "§e玩家 §f" + playerName + " §e沒有任何已記錄的 IP");
            return;
        }

        sender.sendMessage(Plugin_Prefix + "§e" + playerName + " 的 IP 列表:");
        sender.sendMessage("§f" + String.join("§7, §f", ipList));
    }

    private void handleAlts(CommandSender sender, String[] args) {
        if (!sender.hasPermission("useful.alts")) return;

        if (args.length != 1) {
            sender.sendMessage(Plugin_Prefix + "§c用法: /alts <玩家>");
            return;
        }

        String targetName = args[0];

        if (!ipsConfig.contains(targetName)) {
            sender.sendMessage(Plugin_Prefix + "§c找不到玩家 §f" + targetName + " §c的紀錄");
            return;
        }

        List<String> targetIps = ipsConfig.getStringList(targetName);
        if (targetIps == null || targetIps.isEmpty()) {
            sender.sendMessage(Plugin_Prefix + "§e玩家 §f" + targetName + " §e沒有任何已記錄的 IP");
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
            sender.sendMessage(Plugin_Prefix + "§a未找到與 §f" + targetName + " §a共用 IP 的其他帳號");
        } else {
            sender.sendMessage(Plugin_Prefix + "§f" + String.join("§7, §f", alts));
        }
    }

    private void handleReply(Player p, String[] args) {
        Player target = MessageUtils.getLastMessaged(p);
        if (target == null) {
            p.sendMessage("§c無法回覆，沒有找到對象");
            return;
        }
        String msg = String.join(" ", args);
        MessageUtils.sendMessage(p, target, msg);
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
        MessageUtils.sendMessage(p, t, msg);
    }

    private void handleDmToggle(Player player) {
        if (MessageUtils.isDmListenerActive(player)) MessageUtils.removeDmListener(player);
        else MessageUtils.addDmListener(player);
    }

    private void handleExplosion(CommandSender sender, String[] args) {
        if (args.length < 1) return;
        Player t = Bukkit.getPlayer(args[0]);
        if (t == null) return;
        sendExplosion(PacketEvents.getAPI().getPlayerManager().getUser(t));
    }

    private void handleParticle(CommandSender sender, String[] args) {
        if (args.length < 1) return;
        Player t = Bukkit.getPlayer(args[0]);
        if (t == null) return;
        sendParticle(PacketEvents.getAPI().getPlayerManager().getUser(t));
    }

    private void handlePosition(CommandSender sender, String[] args) {
        if (args.length < 1) return;
        Player t = Bukkit.getPlayer(args[0]);
        if (t == null) return;
        sendPosition(PacketEvents.getAPI().getPlayerManager().getUser(t));
    }

    private void handleNuke(CommandSender sender, String[] args) {
        if (args.length < 1) return;
        Player t = Bukkit.getPlayer(args[0]);
        if (t == null) return;
        User u = PacketEvents.getAPI().getPlayerManager().getUser(t);
        sendExplosion(u);
        sendParticle(u);
        sendPosition(u);
    }

    private void handleUUID(Player player, String[] args) {
        if (args.length < 1) {
            player.sendMessage("§c用法: /uuid <玩家>");
            return;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage("§c玩家不存在");
            return;
        }
        Component message = Component.text()
                .append(Component.text(Plugin_Prefix))
                .append(Component.text(target.getName() + " 的 UUID: "))
                .append(Component.text(target.getUniqueId().toString())
                        .color(NamedTextColor.YELLOW)
                        .hoverEvent(HoverEvent.showText(Component.text("點擊複製 UUID")))
                        .clickEvent(ClickEvent.copyToClipboard(target.getUniqueId().toString())))
                .build();
        player.sendMessage(message);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player p)) return null;
        List<String> result = new ArrayList<>();
        String cmd = command.getName().toLowerCase();

        if (cmd.equals("home") || cmd.equals("delhome")) {
            if (args.length == 1 && HomeUtils.homesConfig.contains(p.getUniqueId().toString())) {
                Set<String> homeNames = HomeUtils.homesConfig.getConfigurationSection(p.getUniqueId().toString()).getKeys(false);
                for (String name : homeNames)
                    if (name.toLowerCase().startsWith(args[0].toLowerCase()))
                        result.add(name);
            }
        }
        return result;
    }
}
