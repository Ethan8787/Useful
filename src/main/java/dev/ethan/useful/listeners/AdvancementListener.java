package dev.ethan.useful.listeners;

import dev.ethan.useful.Main;
import dev.ethan.useful.utils.LuckPermsUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import top.nontage.nontagelib.annotations.AutoListener;

@AutoListener
public class AdvancementListener implements Listener {

    private final LuckPermsUtil luckPermsUtil = Main.getInstance().getLuckPermsUtil();

    @EventHandler
    public void onAdvancementDone(PlayerAdvancementDoneEvent e) {
        if (e.getAdvancement().getDisplay() == null) return;
        if (!e.getAdvancement().getDisplay().doesAnnounceToChat()) return;
        Player player = e.getPlayer();
        Component playerName = Component.text(luckPermsUtil.getPlayerPrefix(player) + player.getDisplayName());
        Component advTitle = e.getAdvancement().getDisplay().displayName();
        String frameName = e.getAdvancement().getDisplay().frame().name().toLowerCase();
        String translationKey = "chat.type.advancement." + frameName;
        Component formattedMessage = Component.translatable(translationKey, playerName, advTitle);
        e.message(formattedMessage);
    }
}