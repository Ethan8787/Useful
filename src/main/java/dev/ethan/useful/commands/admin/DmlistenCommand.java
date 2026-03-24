package dev.ethan.useful.commands.admin;

import dev.ethan.useful.Main;
import dev.ethan.useful.utils.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

@CommandInfo(name = "dmlisten", permission = "useful.admin.dmlisten", description = "Toggle DM listener", override = true)
public class DmlistenCommand implements NontageCommand {
    private final MessageUtil messageUtil = Main.getInstance().getMessageUtil();
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player p)) return;
        if (messageUtil.isDmListenerActive(p)) {
            messageUtil.removeDmListener(p);
        } else {
            messageUtil.addDmListener(p);
        }
    }
}
