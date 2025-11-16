package dev.ethan.useful;

import com.github.retrooper.packetevents.PacketEvents;
import dev.ethan.useful.commands.GameCommands;
import dev.ethan.useful.constants.Messages;
import dev.ethan.useful.listeners.GameListener;
import dev.ethan.useful.managers.PlaceHolderManager;
import dev.ethan.useful.managers.PlayerStatusManager;
import dev.ethan.useful.managers.RuntimeManager;
import dev.ethan.useful.utils.*;
import dev.iiahmed.disguise.DisguiseManager;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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
    private GameListener gameListener;
    private NickUtil nickUtil;
    private PlayerStatusManager playerStatusManager;
    private PlaceHolderManager placeHolderManager;
    private LuckPerms luckPerms;
    public NickUtil nickStorage;
    public RuntimeManager runtimeManager;

    @Override
    public void onEnable() {
        instance = this;
        Player author = Bukkit.getPlayer("27ms__");
        if (author != null) {
            author.sendMessage(Messages.PREFIX + "§aUseful-5.8.2.jar");
        }
        teleportUtil = new TeleportUtil(this);
        messageUtil = new MessageUtil(this);
        snowballUtil = new SnowballUtil(this);
        ipTrackerUtil = new IPTrackerUtil(this);
        luckPermsUtil = new LuckPermsUtil(this);
        homeUtil = new HomeUtil(this);
        nickStorage = new NickUtil(this);
        playerUtil = new PlayerUtil();
        translationUtil = new TranslationUtil();
        aceUtil = new AceUtil();
        botUtil = new BotUtil();
        crashUtil = new CrashUtil();
        runtimeManager = new RuntimeManager();
        placeHolderManager = new PlaceHolderManager(this);
        DisguiseManager.initialize(this, true);
        PacketEvents.getAPI().init();
        gameListener = new GameListener();
        register();
        getServer().getPluginManager().registerEvents(gameListener, this);
        playerStatusManager = new PlayerStatusManager(this);
    }

    @Override
    public void onDisable() {
        teleportUtil.save();
        if (nickUtil != null) {
            nickUtil.close();
        }
    }

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
    }

    private void register() {
        GameCommands cmd = new GameCommands();
        String[] commands = {
                "kms", "heal", "boom", "gms", "gmc", "gma", "gmsp", "sudo", "freeze",
                "unfreeze", "nick", "unnick", "msg", "r", "w", "tell", "god", "hat",
                "dupe", "ips", "alts", "gun", "botf", "bot", "removenpc", "crash",
                "dmlisten", "tpa", "tpahere", "tpaccept", "tpdeny", "fly", "sethome",
                "homes", "home", "delhome", "block", "unblock", "uuid", "blocklist",
                "world", "useful"
        };
        for (String s : commands) registerCommand(s, cmd);
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

    public PlaceHolderManager getPlaceHolderManager() {
        return placeHolderManager;
    }

    public PlayerStatusManager getPlayerStatusManager() {
        return playerStatusManager;
    }

    public TeleportUtil getTeleportUtil() {
        return teleportUtil;
    }

    public GameListener getGameListener() {
        return gameListener;
    }

    public TranslationUtil getTranslationUtil() {
        return translationUtil;
    }

    public NickUtil getNickUtil(){
        return nickUtil;
    }

    public RuntimeManager getRuntimeManager() {
        return runtimeManager;
    }

    public LuckPerms getLuckPerms() {
        return luckPerms;
    }

    public static NickUtil nick() {
        return instance.nickStorage;
    }
}
