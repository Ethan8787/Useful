package dev.ethan.useful.commands.admin;

import dev.ethan.useful.constants.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@CommandInfo(name = "uuid", permission = "useful.admin.uuid", description = "Get player UUID", override = true)
public class UuidCommand implements NontageCommand {
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player p)) return;
        if (args.length < 1) {
            p.sendMessage("§c用法: /uuid <玩家>");
            return;
        }
        Player t = Bukkit.getPlayer(args[0]);
        if (t == null) {
            p.sendMessage("§c玩家不存在");
            return;
        }
        Component msg = Component.text()
                .append(Component.text(Messages.PREFIX))
                .append(Component.text(t.getName() + " 的 UUID: "))
                .append(Component.text(t.getUniqueId().toString())
                        .color(NamedTextColor.YELLOW)
                        .hoverEvent(HoverEvent.showText(Component.text("點擊複製 UUID")))
                        .clickEvent(ClickEvent.copyToClipboard(t.getUniqueId().toString()))
                )
                .build();
        p.sendMessage(msg);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args, Location location) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            List<String> names = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (sender instanceof Player sp && !sp.canSee(p)) continue;
                names.add(p.getName());
            }
            StringUtil.copyPartialMatches(args[0], names, completions);
            Collections.sort(completions);
            return completions;
        }
        return completions;
    }
}