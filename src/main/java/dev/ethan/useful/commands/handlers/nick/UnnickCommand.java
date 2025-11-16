package dev.ethan.useful.commands.handlers.nick;

import dev.ethan.useful.commands.handlers.CommandHandler;
import dev.ethan.useful.constants.Messages;
import dev.ethan.useful.Main;
import dev.iiahmed.disguise.DisguiseManager;
import dev.iiahmed.disguise.DisguiseProvider;
import org.bukkit.entity.Player;

public class UnnickCommand implements CommandHandler {
    @Override
    public boolean handle(Player p, String label, String[] args) {
        String current = Main.nick().getNickname(p);
        if (current == null) {
            p.sendMessage(Messages.PREFIX + "§fYou are not nicked.");
            return true;
        }
        DisguiseProvider provider = DisguiseManager.getProvider();
        provider.undisguise(p);
        Main.nick().removeNickname(p);
        p.setDisplayName(p.getName());
        p.setPlayerListName(p.getName());
        p.sendMessage(Messages.PREFIX + "§fUnnicked.");
        return true;
    }
}
