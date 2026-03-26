package dev.ethan.useful.listeners;

import dev.ethan.useful.Main;
import dev.ethan.useful.utils.LuckPermsUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import top.nontage.nontagelib.annotations.AutoListener;

//todo: fix the name color
@AutoListener
public class AdvancementListener implements Listener {

    private final LuckPermsUtil luckPermsUtil = Main.getInstance().getLuckPermsUtil();

    @EventHandler
    public void onAdvancementDone(PlayerAdvancementDoneEvent e) {
        if (e.getAdvancement().getDisplay() == null) return;
        if (!e.getAdvancement().getDisplay().doesAnnounceToChat()) return;

        Player player = e.getPlayer();

        String prefixStr = luckPermsUtil.getPlayerPrefix(player);
        Component prefix = LegacyComponentSerializer.legacySection().deserialize(prefixStr);

        String name = player.getDisplayName();
        Component playerName = LegacyComponentSerializer.legacySection().deserialize(name);

        Component playerWithPrefix = Component.text().append(prefix).append(playerName).build();

        Component advTitle = e.getAdvancement().getDisplay().displayName();

        String frameName = e.getAdvancement().getDisplay().frame().name().toLowerCase();
        String translationKey = "chat.type.advancement." + frameName;

        Component formattedMessage = Component.translatable(translationKey, playerWithPrefix, advTitle);

        e.message(formattedMessage);
    }
}