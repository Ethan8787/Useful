package dev.ethan.useful.commands.player.gameMode;

import org.bukkit.GameMode;
import top.nontage.nontagelib.annotations.CommandInfo;

@CommandInfo(name = "gms", permission = "guildwars.player.gms", description = "Set gamemode to survival", override = true)
public class GamemodeSurvivalCommand extends AbstractGameModeCommand {

    public GamemodeSurvivalCommand() {
        super(GameMode.SURVIVAL);
    }
}