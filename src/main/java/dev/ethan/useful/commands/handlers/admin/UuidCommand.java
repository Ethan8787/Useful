package dev.ethan.useful.commands.handlers.admin;

import dev.ethan.useful.commands.handlers.CommandHandler;
import dev.ethan.useful.constants.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class UuidCommand implements CommandHandler {
    @Override
    public boolean handle(Player p, String label, String[] args) {
        if (args.length < 1) {
            p.sendMessage("§c用法: /uuid <玩家>");
            return true;
        }
        Player t = Bukkit.getPlayer(args[0]);
        if (t == null) {
            p.sendMessage("§c玩家不存在");
            return true;
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
        return true;
    }
}
