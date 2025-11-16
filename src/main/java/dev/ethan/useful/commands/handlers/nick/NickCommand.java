package dev.ethan.useful.commands.handlers.nick;

import dev.ethan.useful.commands.handlers.CommandHandler;
import dev.ethan.useful.constants.Messages;
import dev.ethan.useful.Main;
import dev.ethan.useful.utils.LuckPermsUtil;
import dev.ethan.useful.utils.PlayerUtil;
import dev.iiahmed.disguise.Disguise;
import dev.iiahmed.disguise.DisguiseManager;
import dev.iiahmed.disguise.DisguiseProvider;
import dev.iiahmed.disguise.SkinAPI;
import org.bukkit.entity.Player;
import org.bukkit.entity.EntityType;

import java.util.UUID;

public class NickCommand implements CommandHandler {
    private final PlayerUtil playerUtil = Main.getInstance().getPlayerUtil();
    private final LuckPermsUtil luckPermsUtil = Main.getInstance().getLuckPermsUtil();

    @Override
    public boolean handle(Player p, String label, String[] args) {
        if (args.length < 1) {
            p.sendMessage(Messages.PREFIX + "§fUsage: /nick <nickname> [--skin]");
            return true;
        }
        String nick = args[0].replace("&", "§");
        boolean changeSkin = args.length > 1 && (args[1].equalsIgnoreCase("--skin") || (args.length > 2 && args[2].equalsIgnoreCase("--skin")));
        Main.nick().setNickname(p, nick);
        p.setDisplayName(nick);
        p.setPlayerListName(nick);
        Disguise.Builder builder = Disguise.builder()
                .setName(nick)
                .setEntityType(EntityType.PLAYER);
        if (changeSkin) {
            try {
                UUID skinUUID = playerUtil.getUUID(nick);
                builder.setSkin(SkinAPI.MOJANG,
                        skinUUID != null ? skinUUID : p.getUniqueId());
            } catch (Exception e) {
                builder.setSkin(SkinAPI.MOJANG, p.getUniqueId());
            }
        } else {
            builder.setSkin(SkinAPI.MOJANG, p.getUniqueId());
        }
        try {
            Disguise disguise = builder.build();
            DisguiseProvider provider = DisguiseManager.getProvider();
            provider.disguise(p, disguise);
        } catch (Exception e) {
            e.printStackTrace();
        }
        p.sendMessage(Messages.PREFIX + "§fNick set: " + luckPermsUtil.getPlayerPrefix(p) + nick + (changeSkin ? " §7(Skin)" : ""));
        return true;
    }
}
