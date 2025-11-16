package dev.ethan.useful.commands;

import dev.ethan.useful.Main;
import dev.ethan.useful.commands.handlers.CommandHandler;
import dev.ethan.useful.commands.handlers.admin.*;
import dev.ethan.useful.commands.handlers.bot.BotCommand;
import dev.ethan.useful.commands.handlers.bot.BotfCommand;
import dev.ethan.useful.commands.handlers.bot.RemovenpcCommand;
import dev.ethan.useful.commands.handlers.fun.BoomCommand;
import dev.ethan.useful.commands.handlers.fun.GunCommand;
import dev.ethan.useful.commands.handlers.nick.NickCommand;
import dev.ethan.useful.commands.handlers.nick.UnnickCommand;
import dev.ethan.useful.commands.handlers.player.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class GameCommands implements CommandExecutor, TabCompleter {

    private final Map<String, CommandHandler> handlers = new HashMap<>();

    public GameCommands() {
        registerHandlers();
    }

    private void registerHandlers() {
        // admin
        register("alts", new AltsCommand());
        register("ips", new IpsCommand());
        register("freeze", new FreezeCommand());
        register("unfreeze", new UnfreezeCommand());
        register("sudo", new SudoCommand());
        register("dmlisten", new DmlistenCommand());
        register("crash", new CrashCommand());

        // bot
        register("bot", new BotCommand());
        register("botf", new BotfCommand());
        register("removenpc", new RemovenpcCommand());

        // fun
        register("boom", new BoomCommand());
        register("gun", new GunCommand());

        // nick
        register("nick", new NickCommand());
        register("unnick", new UnnickCommand());

        // player
        register("kms", new KmsCommand());
        register("heal", new HealCommand());
        register("god", new GodCommand());
        register("fly", new FlyCommand());
        register("hat", new HatCommand());
        register("dupe", new DupeCommand());
        register("gmsp", new GameModeCommand(GameMode.SPECTATOR));
        register("gms", new GameModeCommand(GameMode.SURVIVAL));
        register("gma", new GameModeCommand(GameMode.ADVENTURE));
        register("gmc", new GameModeCommand(GameMode.CREATIVE));
        register("home", new HomeCommand());
        register("homes", new HomesCommand());
        register("sethome", new SethomeCommand());
        register("delhome", new DelhomeCommand());
        register("tpa", new TpaCommand());
        register("tpahere", new TpahereCommand());
        register("tpaccept", new TpacceptCommand());
        register("tpdeny", new TpdenyCommand());
        register("block", new BlockCommand());
        register("unblock", new UnblockCommand());
        register("blocklist", new BlocklistCommand());
        register("msg", new MsgCommand());
        register("tell", new MsgCommand());
        register("w", new MsgCommand());
        register("r", new ReplyCommand());
        register("uuid", new UuidCommand());
        register("world", new WorldCommand());
        register("useful", new UsefulHelpCommand(this));
    }

    private void register(String name, CommandHandler handler) {
        handlers.put(name.toLowerCase(), handler);
    }

    public Map<String, CommandHandler> getHandlers() {
        return Collections.unmodifiableMap(handlers);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender s,
                             @NotNull Command cmd,
                             @NotNull String label,
                             String[] args) {
        if (!(s instanceof Player p)) return true;
        CommandHandler handler = handlers.get(cmd.getName().toLowerCase());
        if (handler == null) return false;
        return handler.handle(p, label, args);
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender s,
                                      @NotNull Command cmd,
                                      @NotNull String alias,
                                      String[] args) {
        if (!(s instanceof Player p)) return Collections.emptyList();

        String name = cmd.getName().toLowerCase();
        List<String> result = new ArrayList<>();

        // 1. 特化 tab（home / delhome / world / crash / nick 等）
        switch (name) {
            case "home":
            case "delhome": {
                if (args.length == 1) {
                    var homeUtil = Main.getInstance().getHomeUtil();
                    Set<String> homeNames = homeUtil.getHomeNames(p.getUniqueId());
                    String input = args[0].toLowerCase();
                    for (String home : homeNames) {
                        if (home.toLowerCase().startsWith(input)) {
                            result.add(home);
                        }
                    }
                }
                break;
            }
            case "tpa":
            case "tpahere":
            case "tpaccept":
            case "tpdeny":
            case "freeze":
            case "unfreeze":
            case "sudo":
            case "msg":
            case "w":
            case "tell":
            case "ips":
            case "alts":
            case "botf":
            case "uuid", "gms", "gmc", "gma", "gmsp", "fly": {
                if (args.length == 1) {
                    String input = args[0].toLowerCase();
                    for (Player target : Bukkit.getOnlinePlayers()) {
                        String name0 = target.getName();
                        if (name0.toLowerCase().startsWith(input)) {
                            result.add(name0);
                        }
                    }
                }
                break;
            }
            case "world": {
                if (args.length == 1) {
                    String input = args[0].toLowerCase();
                    for (World w : Bukkit.getWorlds()) {
                        String wn = w.getName();
                        if (wn.toLowerCase().startsWith(input)) {
                            result.add(wn);
                        }
                    }
                }
                break;
            }
            case "nick": {
                if (args.length == 2) {
                    String input = args[1].toLowerCase();
                    if ("--skin".startsWith(input)) {
                        result.add("--skin");
                    }
                }
                break;
            }
            case "crash": {
                if (args.length == 1) {
                    String input = args[0].toLowerCase();
                    for (String m : List.of("explosion", "particle", "position", "nuke")) {
                        if (m.startsWith(input)) {
                            result.add(m);
                        }
                    }
                } else if (args.length == 2) {
                    String input = args[1].toLowerCase();
                    for (Player target : Bukkit.getOnlinePlayers()) {
                        String name0 = target.getName();
                        if (name0.toLowerCase().startsWith(input)) {
                            result.add(name0);
                        }
                    }
                }
                break;
            }
            default:
                break;
        }

        CommandHandler handler = handlers.get(name);
        if (handler != null) {
            List<String> fromHandler = handler.tabComplete(p, alias, args);
            if (fromHandler != null && !fromHandler.isEmpty()) {
                if (result.isEmpty()) {
                    return fromHandler;
                } else {
                    result.addAll(fromHandler);
                }
            }
        }

        if (result.isEmpty()) return Collections.emptyList();
        return result;
    }
}
