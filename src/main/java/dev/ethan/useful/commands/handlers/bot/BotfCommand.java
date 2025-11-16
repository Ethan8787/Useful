package dev.ethan.useful.commands.handlers.bot;

import dev.ethan.useful.commands.handlers.CommandHandler;
import dev.ethan.useful.constants.Messages;
import dev.ethan.useful.utils.BotUtil;
import dev.ethan.useful.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BotfCommand implements CommandHandler {
    private final BotUtil botUtil = Main.getInstance().getBotUtil();

    @Override
    public boolean handle(Player p, String label, String[] args) {
        if (args.length < 2) {
            p.sendMessage(Messages.PREFIX + "§c用法: /botf <name> <player>");
            return true;
        }
        String botName = args[0];
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            p.sendMessage(Messages.PREFIX + "§c找不到玩家 " + args[1]);
            return true;
        }
        botUtil.spawnFakePlayer(Main.getInstance(), p.getLocation(), botName, target);
        p.sendMessage(Messages.PREFIX + "§a已生成 NPC：" + botName + " §a，跟隨 " + target.getName());
        return true;
    }
}
