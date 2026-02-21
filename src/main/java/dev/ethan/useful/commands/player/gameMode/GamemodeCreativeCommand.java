package dev.ethan.useful.commands.player.gameMode;

import org.bukkit.GameMode;
import top.nontage.nontagelib.annotations.CommandInfo;

@CommandInfo(name = "gmc", permission = "guildwars.player.gmc", description = "Set gamemode to creative", override = true)
public class GamemodeCreativeCommand extends AbstractGameModeCommand {

    public GamemodeCreativeCommand() {
        super(GameMode.CREATIVE);
    }
}