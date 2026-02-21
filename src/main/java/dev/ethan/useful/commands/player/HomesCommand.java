package dev.ethan.useful.commands.player;

import dev.ethan.useful.Main;
import dev.ethan.useful.utils.HomeUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

@CommandInfo(name = "homes", permission = "guildwars.player.homes", description = "List homes", override = true)
public class HomesCommand implements NontageCommand {

    private final HomeUtil homeUtil = Main.getInstance().getHomeUtil();

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player player)) return;
        homeUtil.handleHomes(player);
    }
}