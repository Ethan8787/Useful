package dev.ethan.useful.commands.admin.gameMode;

import org.bukkit.GameMode;
import top.nontage.nontagelib.annotations.CommandInfo;

@CommandInfo(name = "gms", permission = "useful.admin.gms", description = "Set gamemode to survival", override = true)
public class GamemodeSurvivalCommand extends AbstractGameModeCommand {

    public GamemodeSurvivalCommand() {
        super(GameMode.SURVIVAL);
    }
}