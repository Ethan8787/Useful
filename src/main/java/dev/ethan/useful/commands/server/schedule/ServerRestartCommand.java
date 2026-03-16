package dev.ethan.useful.commands.server.schedule;

import dev.ethan.useful.Main;
import dev.ethan.useful.constants.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@CommandInfo(name = "serverrestart", permission = "useful.console.serverrestart", description = "Schedule server restart", override = true)
public class ServerRestartCommand implements NontageCommand {

    private static Integer taskId;
    private static LocalDateTime scheduledAt;
    private static BukkitTask countdownTask;

    private static final DateTimeFormatter OUT_FMT = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    private static final DateTimeFormatter[] IN_FMT = new DateTimeFormatter[]{
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    };

    @Override
    public void execute(CommandSender sender, String label, String[] args) {

        if (args.length == 0) {
            Bukkit.broadcastMessage(Messages.PREFIX + "§e伺服器即將重啟");
            startRestartCountdown(5);
            return;
        }

        if (args.length != 2) {
            sender.sendMessage(Messages.PREFIX + "§c用法：/serverrestart <yyyy/MM/dd> <HH:mm:ss>");
            sender.sendMessage(Messages.PREFIX + "§c例如：/serverrestart 2026/02/21 10:32:45");
            return;
        }

        LocalDateTime target = parse(args[0] + " " + args[1]);
        if (target == null) {
            sender.sendMessage(Messages.PREFIX + "§c時間格式錯誤，請用：yyyy/MM/dd HH:mm:ss");
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        if (!target.isAfter(now)) {
            sender.sendMessage(Messages.PREFIX + "§c時間必須是未來");
            return;
        }

        long delayTicks = Math.max(1L, Duration.between(now, target).toMillis() / 50L);

        if (taskId != null) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
        cancelCountdown();

        scheduledAt = target;
        taskId = Bukkit.getScheduler().runTaskLater(
                Main.getInstance(),
                () -> {
                    Bukkit.broadcastMessage(Messages.PREFIX + "§e伺服器排程重啟時間到，準備重啟");
                    startRestartCountdown(5);
                },
                delayTicks
        ).getTaskId();

        long seconds = Math.max(1L, Duration.between(now, target).getSeconds());
        startRestartCountdown(seconds);

        sender.sendMessage(Messages.PREFIX + "§a已排程重啟：§f" + scheduledAt.format(OUT_FMT));
    }

    private void startRestartCountdown(long totalSeconds) {
        cancelCountdown();

        final String title = "§b§l伺服器重啟";
        countdownTask = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), new Runnable() {
            long sec = Math.max(1L, totalSeconds);

            @Override
            public void run() {
                boolean shouldShow = sec <= 60 || sec % 60 == 0 || sec == 30 || sec == 10 || sec <= 5;
                if (shouldShow) {
                    String subtitle = "§7將在§f" + sec + "§7秒後執行";
                    sendTitleAll(title, subtitle, 0, 25, 5);
                }

                if (sec <= 1) {
                    cancelCountdown();
                    doRestart();
                    return;
                }

                sec--;
            }
        }, 0L, 20L);
    }

    private void cancelCountdown() {
        if (countdownTask != null) {
            countdownTask.cancel();
            countdownTask = null;
        }
    }

    private void doRestart() {
        try {
            Bukkit.getServer().restart();
        } catch (Throwable t) {
            Bukkit.shutdown();
        }
    }

    private LocalDateTime parse(String input) {
        for (DateTimeFormatter fmt : IN_FMT) {
            try {
                return LocalDateTime.parse(input, fmt);
            } catch (DateTimeParseException ignored) {
            }
        }
        return null;
    }

    private void sendTitleAll(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            sendTitle(p, title, subtitle, fadeIn, stay, fadeOut);
        }
    }

    private void sendTitle(Player p, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        try {
            Method m = p.getClass().getMethod("sendTitle", String.class, String.class, int.class, int.class, int.class);
            m.invoke(p, title, subtitle, fadeIn, stay, fadeOut);
            return;
        } catch (Throwable ignored) {
        }

        try {
            Class<?> craftPlayer = Class.forName("org.bukkit.craftbukkit." + getVersion() + ".entity.CraftPlayer");
            Object cp = craftPlayer.cast(p);

            Method getHandle = craftPlayer.getMethod("getHandle");
            Object ep = getHandle.invoke(cp);

            Object playerConnection = ep.getClass().getField("playerConnection").get(ep);
            Method sendPacket = playerConnection.getClass().getMethod("sendPacket", getNmsClass("Packet"));

            Class<?> ichat = getNmsClass("IChatBaseComponent");
            Class<?> chatSerializer = Class.forName(getNmsClassName("IChatBaseComponent$ChatSerializer"));
            Method a = chatSerializer.getMethod("a", String.class);

            Object titleComp = a.invoke(null, "{\"text\":\"" + escapeJson(title) + "\"}");
            Object subComp = a.invoke(null, "{\"text\":\"" + escapeJson(subtitle) + "\"}");

            Class<?> packetTitle = getNmsClass("PacketPlayOutTitle");
            Class<?> enumTitle = Class.forName(getNmsClassName("PacketPlayOutTitle$EnumTitleAction"));

            Constructor<?> c1 = packetTitle.getConstructor(enumTitle, ichat, int.class, int.class, int.class);

            Object actTitle = Enum.valueOf((Class<Enum>) enumTitle, "TITLE");
            Object actSub = Enum.valueOf((Class<Enum>) enumTitle, "SUBTITLE");

            Object pktTitle = c1.newInstance(actTitle, titleComp, fadeIn, stay, fadeOut);
            Object pktSub = c1.newInstance(actSub, subComp, fadeIn, stay, fadeOut);

            sendPacket.invoke(playerConnection, pktTitle);
            sendPacket.invoke(playerConnection, pktSub);
        } catch (Throwable ignored) {
        }
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private String getVersion() {
        String name = Bukkit.getServer().getClass().getPackage().getName();
        return name.substring(name.lastIndexOf('.') + 1);
    }

    private Class<?> getNmsClass(String simple) throws ClassNotFoundException {
        return Class.forName(getNmsClassName(simple));
    }

    private String getNmsClassName(String simple) {
        return "net.minecraft.server." + getVersion() + "." + simple;
    }
}