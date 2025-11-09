package dev.ethan.useful;

import com.github.retrooper.packetevents.PacketEvents;
import dev.ethan.useful.commands.GameCommands;
import dev.ethan.useful.events.GameListener;
import dev.ethan.useful.handlers.ConfigHandler;
import dev.ethan.useful.handlers.PlaceHolderHandler;
import dev.ethan.useful.handlers.PlayerStatusHandler;
import dev.ethan.useful.utils.*;
import dev.iiahmed.disguise.DisguiseManager;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;

public final class Main extends JavaPlugin {
    public static Main instance;
    public static LuckPerms luckPerms;
    public static NickUtil nickStorage;
    public static Set<String> frozenPlayers = new HashSet<>();
    public static String Plugin_Prefix = "§c系統 §7» ";
    public static HashMap<UUID, UUID> lastMessaged = new HashMap<>();
    public static File ipsFile;
    public static FileConfiguration ipsConfig;
    public static String FeatherName = "§dFeather";
    public static HashMap<UUID, Integer> killStreaks = new HashMap<>();
    public static String VandalName = "§dVandal";
    public static Set<UUID> dmListeners = new HashSet<>();
    public static File statsFile;
    public static FileConfiguration statsConfig;

    @Override
    public void onEnable() {
        instance = this;
        Player author = Bukkit.getPlayer("27ms__");
        if (!(author == null)) author.sendMessage(Plugin_Prefix + "§aUseful-5.8.2.jar");
        TeleportUtil.init(this);
        MessageUtil.init(this);
        SnowballUtil.init(this);
        IPTrackerUtil.init(this);
        LuckPermsUtil.init(this);
        PlayerStatusHandler.init(this);
        PlaceHolderHandler.init(this);
        DisguiseManager.initialize(this, true);
        PacketEvents.getAPI().init();
        ConfigHandler config = new ConfigHandler(this);
        nickStorage = NickUtil.init(
                this,
                config.getHost(),
                config.getPort(),
                config.getDatabase(),
                config.getUsername(),
                config.getPassword(),
                config.getPoolSize()
        );
        register();
    }

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
    }

    @Override
    public void onDisable() {
        TeleportUtil.save();
        NickUtil.close();
    }

    private void register() {
        GameCommands cmd = new GameCommands();
        String[] commands = {
                "kms","l","heal","boom","gms","gmc","gma","gmsp","sudo","freeze",
                "unfreeze","feather","nick","unnick","msg","r","w","tell","god","hat",
                "dupe","ips","alts","gun","botf","bot","removenpc","nuke","explosion",
                "particle","position","dmlisten","tpa","tpahere","tpaccept","tpdeny",
                "fly","sethome","homes","home","delhome","block","unblock","uuid","blocklist"
        };
        for (String s : commands) registerCommand(s, cmd);
        getServer().getPluginManager().registerEvents(new GameListener(), this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }
    private void registerCommand(String name, GameCommands executor) {
        var cmd = getCommand(name);
        if (cmd == null) {
            getLogger().warning("Command not found in plugin.yml: " + name);
            return;
        }
        cmd.setExecutor(executor);
        cmd.setTabCompleter(executor);
    }

    public static Main getInstance() {
        return instance;
    }
}
