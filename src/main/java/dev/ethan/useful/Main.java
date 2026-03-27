package dev.ethan.useful;

import com.github.retrooper.packetevents.PacketEvents;
import dev.ethan.useful.constants.Messages;
import dev.ethan.useful.listeners.AnvilListener;
import dev.ethan.useful.listeners.CommandBlockerListener;
import dev.ethan.useful.listeners.CrystalListener;
import dev.ethan.useful.listeners.DeathListener;
import dev.ethan.useful.listeners.KillEffectListener;
import dev.ethan.useful.listeners.MovementListener;
import dev.ethan.useful.listeners.PlayerJoinQuitListener;
import dev.ethan.useful.listeners.WeaponListener;
import dev.ethan.useful.managers.GameManager;
import dev.ethan.useful.managers.PlaceHolderManager;
import dev.ethan.useful.managers.PlayerStatusManager;
import dev.ethan.useful.utils.AceUtil;
import dev.ethan.useful.utils.BotUtil;
import dev.ethan.useful.utils.ConsoleUtil;
import dev.ethan.useful.utils.CrashUtil;
import dev.ethan.useful.utils.HomeUtil;
import dev.ethan.useful.utils.IPTrackerUtil;
import dev.ethan.useful.utils.LuckPermsUtil;
import dev.ethan.useful.utils.MessageUtil;
import dev.ethan.useful.utils.NickUtil;
import dev.ethan.useful.utils.PlayerBlockingUtil;
import dev.ethan.useful.utils.PlayerUtil;
import dev.ethan.useful.utils.SnowballUtil;
import dev.ethan.useful.utils.TeleportUtil;
import dev.ethan.useful.utils.TranslationUtil;
import dev.iiahmed.disguise.DisguiseManager;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import top.nontage.nontagelib.command.NontageCommandLoader;
import top.nontage.nontagelib.listener.ListenerRegister;

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
    private PlayerBlockingUtil playerBlockingUtil;
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
        shutdownServices();
    }

    private void initServices() {
        luckPermsUtil = new LuckPermsUtil(this);
        playerBlockingUtil = new PlayerBlockingUtil(this, luckPermsUtil);
        teleportUtil = new TeleportUtil(this);
        messageUtil = new MessageUtil(this);
        snowballUtil = new SnowballUtil(this);
        ipTrackerUtil = new IPTrackerUtil(this);
        homeUtil = new HomeUtil(this);
        nickUtil = new NickUtil(this);

        gameManager = new GameManager();
        playerUtil = new PlayerUtil(gameManager);

        translationUtil = new TranslationUtil();
        aceUtil = new AceUtil();
        botUtil = new BotUtil();
        crashUtil = new CrashUtil();

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

    private void shutdownServices() {
        try {
            if (playerBlockingUtil != null) {
                playerBlockingUtil.save();
            }
            if (nickUtil != null) {
                nickUtil.close();
            }
            Bukkit.getScheduler().cancelTasks(this);
        } catch (Exception ex) {
            getLogger().severe("Shutdown error: " + ex.getMessage());
        }
    }

    private void registerCommands() {
        NontageCommandLoader.registerAll(this);
        getLogger().info("All commands registered (NontageCommandLoader)");
    }

    private void registerListeners() {
        ListenerRegister.registerAll(this);
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

    public static Main getInstance() {
        return instance;
    }

    public AceUtil getAceUtil() {
        return aceUtil;
    }

    public BotUtil getBotUtil() {
        return botUtil;
    }

    public CrashUtil getCrashUtil() {
        return crashUtil;
    }

    public HomeUtil getHomeUtil() {
        return homeUtil;
    }

    public IPTrackerUtil getIPTrackerUtil() {
        return ipTrackerUtil;
    }

    public LuckPermsUtil getLuckPermsUtil() {
        return luckPermsUtil;
    }

    public MessageUtil getMessageUtil() {
        return messageUtil;
    }

    public PlayerUtil getPlayerUtil() {
        return playerUtil;
    }

    public SnowballUtil getSnowballUtil() {
        return snowballUtil;
    }

    public PlayerBlockingUtil getPlayerBlockingUtil() {
        return playerBlockingUtil;
    }

    public PlaceHolderManager getPlaceHolderManager() {
        return placeHolderManager;
    }

    public PlayerStatusManager getPlayerStatusManager() {
        return playerStatusManager;
    }

    public TeleportUtil getTeleportUtil() {
        return teleportUtil;
    }

    public TranslationUtil getTranslationUtil() {
        return translationUtil;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public NickUtil getNickUtil() {
        return nickUtil;
    }

    public static NickUtil nick() {
        return instance.nickUtil;
    }
}