package dev.ethan.useful.commands.handlers.admin;

import dev.ethan.useful.commands.GameCommands;
import dev.ethan.useful.commands.handlers.CommandHandler;
import dev.ethan.useful.constants.Messages;
import org.bukkit.entity.Player;

import java.util.*;

public class UsefulHelpCommand implements CommandHandler {
    private final GameCommands gameCommands;

    public UsefulHelpCommand(GameCommands gameCommands) {
        this.gameCommands = gameCommands;
    }

    @Override
    public boolean handle(Player p, String label, String[] args) {

        Map<String, CommandHandler> map = gameCommands.getHandlers();
        List<String> names = new ArrayList<>(map.keySet());
        Collections.sort(names);

        p.sendMessage(Messages.PREFIX + "§eUseful Commands:");
        for (String name : names) {
            CommandHandler h = map.get(name);
            String usage = h.getUsage();
            String desc = h.getDescription();

            String line = "§a/" + name;
            if (usage != null && !usage.isEmpty()) {
                line += " §7" + usage;
            }
            if (desc != null && !desc.isEmpty()) {
                line += " §8- §f" + desc;
            }

            p.sendMessage(line);
        }

        return true;
    }

    @Override
    public String getDescription() {
        return "顯示 Useful 插件的指令列表";
    }

    @Override
    public String getUsage() {
        return "/useful";
    }
}
