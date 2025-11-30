package dev.ethan.useful;

import com.github.retrooper.packetevents.PacketEvents;
import dev.ethan.useful.commands.GameCommands;
import dev.ethan.useful.constants.Messages;
import dev.ethan.useful.listeners.*;
import dev.ethan.useful.managers.PlaceHolderManager;
import dev.ethan.useful.managers.PlayerStatusManager;
import dev.ethan.useful.managers.GameManager;
import dev.ethan.useful.utils.*;
import dev.iiahmed.disguise.DisguiseManager;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static Main instance;

    private AceUtil aceUtil;
    private BotUtil botUtil;
    private CrashUtil crashUtil;
    private HomeUtil homeUtil;
    private IPTrackerUtil ipTrackerUtil;
    private LuckPermsUtil luckPermsUtil;
    private MessageUtil messageUtil;
    private PlayerUtil playerUtil;
    private SnowballUtil snowballUtil;
    private TeleportUtil teleportUtil;
    private TranslationUtil translationUtil;
    private NickUtil nickUtil;
    private PlayerStatusManager playerStatusManager;
    private PlaceHolderManager placeHolderManager;
    private GameManager gameManager;

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
    }

    @Override
    public void onEnable() {
        instance = this;
        printStartupBanner();
        initServices();
        hookDependencies();
        registerCommands();
        registerListeners();
        sendDevMessage();
    }

    @Override
    public void onDisable() {
        try {
            teleportUtil.save();
            if (nickUtil != null) nickUtil.close();
        } catch (Exception ex) {
            getLogger().warning("Shutdown error: " + ex.getMessage());
        }
    }

    private void initServices() {
        luckPermsUtil = new LuckPermsUtil(this);
        teleportUtil = new TeleportUtil(this);
        messageUtil = new MessageUtil(this);
        snowballUtil = new SnowballUtil(this);
        ipTrackerUtil = new IPTrackerUtil(this);
        homeUtil = new HomeUtil(this);
        nickUtil = new NickUtil(this);
        gameManager = new GameManager();
        translationUtil = new TranslationUtil();
        aceUtil = new AceUtil();
        botUtil = new BotUtil();
        crashUtil = new CrashUtil();
        playerUtil = new PlayerUtil(gameManager);
        placeHolderManager = new PlaceHolderManager(this);
        playerStatusManager = new PlayerStatusManager(this);
        getLogger().info("All services initialized");
    }

    private void hookDependencies() {
        DisguiseManager.initialize(this, true);
        getLogger().info("ModernDisguise hooked");
        PacketEvents.getAPI().init();
        getLogger().info("PacketEvents hooked");
        getLogger().info("PlaceholderAPI hooked");
    }

    private void registerCommands() {
        GameCommands executor = new GameCommands();
        String[] commands = {
                "kms", "heal", "boom", "gms", "gmc", "gma", "gmsp", "sudo", "freeze",
                "unfreeze", "nick", "unnick", "msg", "r", "w", "tell", "god", "hat",
                "dupe", "ips", "alts", "gun", "botf", "bot", "removenpc", "crash",
                "dmlisten", "tpa", "tpahere", "tpaccept", "tpdeny", "fly", "sethome",
                "homes", "home", "delhome", "block", "unblock", "uuid", "blocklist",
                "world", "useful", "fix"
        };
        for (String name : commands) {
            var cmd = getCommand(name);
            if (cmd == null) {
                getLogger().warning("Missing command in plugin.yml: " + name);
                continue;
            }
            cmd.setExecutor(executor);
            cmd.setTabCompleter(executor);
        }
        getLogger().info("All commands registered");
    }

    private void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new PlayerJoinQuitListener(), this);
        pm.registerEvents(new MovementListener(), this);
        pm.registerEvents(new WeaponListener(), this);
        pm.registerEvents(new AnvilListener(), this);
        pm.registerEvents(new DeathListener(), this);
        pm.registerEvents(new KillEffectListener(), this);
        pm.registerEvents(new CommandBlockerListener(), this);
        pm.registerEvents(new CrystalListener(), this);
        getLogger().info("All listeners registered");
    }

    private void sendDevMessage() {
        Player p = Bukkit.getPlayer("27ms__");
        if (p != null) p.sendMessage(Messages.PREFIX + "§3Useful §bv6.2.1");
    }

    private void printStartupBanner() {
        log("&9┏━━━━━━━━━━━━━━━┓  ");
        log("&9┃ &3Useful &bv" + getDescription().getVersion() + " &9┃  ");
        log("&9┃ &3Author&f: &bEthan &9┃  ");
        log("&9┗━━━━━━━━━━━━━━━┛  ");
    }

    private void log(String msg) {
        Bukkit.getConsoleSender().sendMessage(ConsoleUtil.colorize(msg));
    }

    public static Main getInstance() { return instance; }

    public AceUtil getAceUtil() { return aceUtil; }
    public BotUtil getBotUtil() { return botUtil; }
    public CrashUtil getCrashUtil() { return crashUtil; }
    public HomeUtil getHomeUtil() { return homeUtil; }
    public IPTrackerUtil getIPTrackerUtil() { return ipTrackerUtil; }
    public LuckPermsUtil getLuckPermsUtil() { return luckPermsUtil; }
    public MessageUtil getMessageUtil() { return messageUtil; }
    public PlayerUtil getPlayerUtil() { return playerUtil; }
    public SnowballUtil getSnowballUtil() { return snowballUtil; }
    public PlaceHolderManager getPlaceHolderManager() { return placeHolderManager; }
    public PlayerStatusManager getPlayerStatusManager() { return playerStatusManager; }
    public TeleportUtil getTeleportUtil() { return teleportUtil; }
    public TranslationUtil getTranslationUtil() { return translationUtil; }
    public GameManager getGameManager() { return gameManager; }
    public NickUtil getNickUtil() { return nickUtil; }
    public static NickUtil nick() { return instance.nickUtil; }
}
