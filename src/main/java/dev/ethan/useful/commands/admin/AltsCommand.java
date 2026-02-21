package dev.ethan.useful.commands.admin;

import dev.ethan.useful.Main;
import dev.ethan.useful.constants.Messages;
import dev.ethan.useful.utils.IPTrackerUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

import java.util.*;

@CommandInfo(name = "alts", permission = "guildwars.admin.alts", description = "Check alternate accounts by IP", override = true)
public class AltsCommand implements NontageCommand {
    private final IPTrackerUtil ipTrackerUtil = Main.getInstance().getIPTrackerUtil();
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player p)) return;
        if (args.length != 1) {
            p.sendMessage(Messages.PREFIX + "§c用法: /alts <玩家>");
            return;
        }
        String targetName = args[0];
        if (!ipTrackerUtil.ipsConfig.contains(targetName)) {
            p.sendMessage(Messages.PREFIX + "§c找不到玩家 §f" + targetName + " §c的紀錄");
            return;
        }
        List<String> targetIps = ipTrackerUtil.ipsConfig.getStringList(targetName);
        if (targetIps.isEmpty()) {
            p.sendMessage(Messages.PREFIX + "§e玩家 §f" + targetName + " §e沒有任何已記錄的 IP");
            return;
        }
        Map<String, Set<String>> ipMap = new HashMap<>();
        for (String name : ipTrackerUtil.ipsConfig.getKeys(false)) {
            for (String ip : ipTrackerUtil.ipsConfig.getStringList(name)) {
                ipMap.computeIfAbsent(ip, k -> new HashSet<>()).add(name);
            }
        }
        Set<String> alts = new HashSet<>();
        for (String ip : targetIps) {
            alts.addAll(ipMap.getOrDefault(ip, Collections.emptySet()));
        }
        alts.remove(targetName);
        if (alts.isEmpty()) {
            p.sendMessage(Messages.PREFIX + "§a未找到與 §f" + targetName + " §a共用 IP 的其他帳號");
        } else {
            p.sendMessage(Messages.PREFIX + "§f" + String.join("§7, §f", alts));
        }
    }
}
