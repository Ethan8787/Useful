package dev.ethan.useful.commands.nick;

import dev.ethan.useful.Main;
import dev.ethan.useful.constants.Messages;
import dev.iiahmed.disguise.DisguiseManager;
import dev.iiahmed.disguise.DisguiseProvider;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

@CommandInfo(
        name = "unnick",
        permission = "useful.nick",
        description = "Remove nickname",
        override = true
)
public class UnnickCommand implements NontageCommand {

    private final DisguiseProvider provider = DisguiseManager.getProvider();

    @Override
    public void execute(CommandSender sender, String label, String[] args) {

        if (!(sender instanceof Player p)) {
            sender.sendMessage(Messages.PREFIX + "§cOnly players can use this command.");
            return;
        }

        String current = Main.nick().getNickname(p);

        if (current == null) {
            p.sendMessage(Messages.PREFIX + "§cYou are not nicked.");
            return;
        }

        provider.undisguise(p);
        Main.nick().removeNickname(p);

        p.setDisplayName(p.getName());
        p.setPlayerListName(p.getName());

        p.sendMessage(Messages.PREFIX + "§aUnnicked.");
    }
}