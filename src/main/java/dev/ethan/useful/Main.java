package dev.ethan.useful;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.github.retrooper.packetevents.PacketEvents;
import dev.ethan.useful.commands.GameCommands;
import dev.ethan.useful.events.GameListener;
import dev.ethan.useful.handlers.ActionBarHandler;
import dev.ethan.useful.handlers.ConfigHandler;
import dev.ethan.useful.handlers.PlaceHolderHandlers;
import dev.ethan.useful.utils.NickUtils;
import dev.ethan.useful.utils.TeleportUtils;
import dev.iiahmed.disguise.DisguiseManager;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;

import static dev.ethan.useful.utils.IPTrackerUtils.createIpsFile;
import static dev.ethan.useful.utils.MessageUtils.setupDmListenerFile;


public final class Main extends JavaPlugin {
    public static Main instance;
    public static LuckPerms luckPerms;
    public static NickUtils nickStorage;
    public static Set<String> frozenPlayers = new HashSet<>();
    public static int MAX_NICKNAME_LENGTH = 16;
    public static String Plugin_Prefix = "§c系統 §7» ";
    public static HashMap<UUID, UUID> lastMessaged = new HashMap<>();
    public static File ipsFile;
    public static FileConfiguration ipsConfig;
    public static String FeatherName = ChatColor.LIGHT_PURPLE + "Feather " + ChatColor.GRAY + "(Right Click)";
    public static HashMap<UUID, Integer> killStreaks = new HashMap<>();
    public static String VandalName = ChatColor.LIGHT_PURPLE + "Vandal " + ChatColor.GRAY + "(Left Click)";
    public static Set<UUID> dmListeners = new HashSet<>();
    public static File statsFile;
    public static FileConfiguration statsConfig;

    @Override
    public void onEnable() {
        instance = this;
        ConfigHandler config = new ConfigHandler(this);
        nickStorage = NickUtils.init(
                this,
                config.getHost(),
                config.getPort(),
                config.getDatabase(),
                config.getUsername(),
                config.getPassword(),
                config.getPoolSize()
        );
        TeleportUtils.init(this);
        setupLuckPerms();
        DisguiseManager.initialize(this, true);
        createIpsFile();
        registerSnowballHider();

        setupDmListenerFile(this);

        Player author = Bukkit.getPlayer("27ms__");
        if (!(author == null)) author.sendMessage(Plugin_Prefix + "§aUseful-5.8.2.jar");

        Objects.requireNonNull(getCommand("kms")).setExecutor(new GameCommands());
        Objects.requireNonNull(getCommand("l")).setExecutor(new GameCommands());
        Objects.requireNonNull(getCommand("heal")).setExecutor(new GameCommands());
        Objects.requireNonNull(getCommand("boom")).setExecutor(new GameCommands());
        Objects.requireNonNull(getCommand("gms")).setExecutor(new GameCommands());
        Objects.requireNonNull(getCommand("gmc")).setExecutor(new GameCommands());
        Objects.requireNonNull(getCommand("gma")).setExecutor(new GameCommands());
        Objects.requireNonNull(getCommand("gmsp")).setExecutor(new GameCommands());
        Objects.requireNonNull(getCommand("sudo")).setExecutor(new GameCommands());
        Objects.requireNonNull(getCommand("freeze")).setExecutor(new GameCommands());
        Objects.requireNonNull(getCommand("unfreeze")).setExecutor(new GameCommands());
        Objects.requireNonNull(getCommand("feather")).setExecutor(new GameCommands());
        Objects.requireNonNull(getCommand("nick")).setExecutor(new GameCommands());
        Objects.requireNonNull(getCommand("unnick")).setExecutor(new GameCommands());
        Objects.requireNonNull(getCommand("msg")).setExecutor(new GameCommands());
        Objects.requireNonNull(getCommand("r")).setExecutor(new GameCommands());
        Objects.requireNonNull(getCommand("w")).setExecutor(new GameCommands());
        Objects.requireNonNull(getCommand("tell")).setExecutor(new GameCommands());
        Objects.requireNonNull(getCommand("god")).setExecutor(new GameCommands());
        Objects.requireNonNull(getCommand("hat")).setExecutor(new GameCommands());
        Objects.requireNonNull(getCommand("dupe")).setExecutor(new GameCommands());
        Objects.requireNonNull(getCommand("ips")).setExecutor(new GameCommands());
        Objects.requireNonNull(getCommand("alts")).setExecutor(new GameCommands());
        Objects.requireNonNull(getCommand("gun")).setExecutor(new GameCommands());
        Objects.requireNonNull(getCommand("botf")).setExecutor(new GameCommands());
        Objects.requireNonNull(getCommand("bot")).setExecutor(new GameCommands());
        Objects.requireNonNull(getCommand("removenpc")).setExecutor(new GameCommands());
        Objects.requireNonNull(getCommand("nuke")).setExecutor(new GameCommands());
        Objects.requireNonNull(getCommand("sword")).setExecutor(new GameCommands());
        Objects.requireNonNull(getCommand("dmlisten")).setExecutor(new GameCommands());
        Objects.requireNonNull(getCommand("tpa")).setExecutor(new GameCommands());
        Objects.requireNonNull(getCommand("tpahere")).setExecutor(new GameCommands());
        Objects.requireNonNull(getCommand("tpaccept")).setExecutor(new GameCommands());
        Objects.requireNonNull(getCommand("tpdeny")).setExecutor(new GameCommands());
        Objects.requireNonNull(getCommand("fly")).setExecutor(new GameCommands());
        Objects.requireNonNull(getCommand("explosion")).setExecutor(new GameCommands());
        Objects.requireNonNull(getCommand("particle")).setExecutor(new GameCommands());
        Objects.requireNonNull(getCommand("position")).setExecutor(new GameCommands());
        Objects.requireNonNull(getCommand("setxp")).setExecutor(new GameCommands());
        Objects.requireNonNull(getCommand("sethome")).setExecutor(new GameCommands());
        Objects.requireNonNull(getCommand("homes")).setExecutor(new GameCommands());
        Objects.requireNonNull(getCommand("home")).setExecutor(new GameCommands());
        Objects.requireNonNull(getCommand("delhome")).setExecutor(new GameCommands());
        Objects.requireNonNull(getCommand("block")).setExecutor(new GameCommands());
        Objects.requireNonNull(getCommand("unblock")).setExecutor(new GameCommands());
        Objects.requireNonNull(getCommand("uuid")).setExecutor(new GameCommands());
        Objects.requireNonNull(getCommand("blocklist")).setExecutor(new GameCommands());
        Objects.requireNonNull(getCommand("home")).setTabCompleter(new GameCommands());
        Objects.requireNonNull(getCommand("delhome")).setTabCompleter(new GameCommands());

        getServer().getPluginManager().registerEvents(new GameListener(), this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        new ActionBarHandler().runTaskTimer(this, 0L, 20L);
        try {
            new PlaceHolderHandlers().register();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
    }

    @Override
    public void onDisable() {
        TeleportUtils.save();
        NickUtils.close();
    }

    public static Main getInstance() {
        return instance;
    }

    private void setupLuckPerms() {
        RegisteredServiceProvider<LuckPerms> provider = getServer().getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            luckPerms = provider.getProvider();
        }
    }

    private void registerSnowballHider() {
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        manager.addPacketListener(new PacketAdapter(this, ListenerPriority.HIGHEST, PacketType.Play.Server.SPAWN_ENTITY) {
            @Override
            public void onPacketSending(PacketEvent event) {
                int entityId = event.getPacket().getIntegers().read(0);
                Entity entity = manager.getEntityFromID(event.getPlayer().getWorld(), entityId);

                if (entity instanceof Snowball && entity.getScoreboardTags().contains("gunbullet_invisible")) {
                    event.setCancelled(true);
                }
            }
        });
    }
}
