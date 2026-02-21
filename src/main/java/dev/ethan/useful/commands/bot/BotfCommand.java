package dev.ethan.useful.commands.bot;

import dev.ethan.useful.Main;
import dev.ethan.useful.constants.Messages;
import dev.ethan.useful.utils.BotUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

@CommandInfo(name = "botf", permission = "useful.bot.follow", description = "Spawn a fake player that follows someone", override = true)
public class BotfCommand implements NontageCommand {

    private final BotUtil botUtil = Main.getInstance().getBotUtil();

    @Override
    public void execute(CommandSender sender, String label, String[] args) {

        if (!(sender instanceof Player p)) return;

        if (args.length < 2) {
            p.sendMessage(Messages.PREFIX + "§c用法: /botf <name> <player>");
            return;
        }

        String botName = args[0];

        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            p.sendMessage(Messages.PREFIX + "§c找不到玩家 " + args[1]);
            return;
        }

        botUtil.spawnFakePlayer(Main.getInstance(), p.getLocation(), botName, target);

        p.sendMessage(Messages.PREFIX + "§a已生成 NPC：" + botName + " §a，跟隨 " + target.getName());
    }
}
