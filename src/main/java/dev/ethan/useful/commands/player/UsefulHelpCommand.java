package dev.ethan.useful.commands.player;

import dev.ethan.useful.constants.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

import java.util.List;

@CommandInfo(name = "useful", permission = "useful.help", description = "顯示 Useful 插件的指令列表", override = true)
public class UsefulHelpCommand implements NontageCommand {
    private static final List<String> COMMANDS = List.of(
            "§a/alts §7<玩家> §8- §f查詢共用 IP 的帳號",
            "§a/ips §7<玩家> §8- §f查詢玩家 IP 紀錄",
            "§a/freeze §7<玩家> §8- §f凍結玩家",
            "§a/unfreeze §7<玩家> §8- §f解凍玩家",
            "§a/uuid §7<玩家> §8- §f查詢玩家 UUID",
            "§a/sudo §7<玩家> <指令> §8- §f強制玩家執行指令",
            "§a/crash §7<方法> <玩家> §8- §f嘗試使玩家客戶端崩潰",
            "§a/dmlisten §8- §f切換私訊監聽模式"
    );
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player p)) return;
        p.sendMessage(Messages.PREFIX + "§eUseful Commands:");
        for (String line : COMMANDS) {
            p.sendMessage(line);
        }
    }
}
