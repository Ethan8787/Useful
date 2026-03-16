package dev.ethan.useful.commands.admin.gameMode;

import org.bukkit.GameMode;
import top.nontage.nontagelib.annotations.CommandInfo;

@CommandInfo(name = "gmsp", permission = "useful.admin.gmsp", description = "Set gamemode to spectator", override = true)
public class GamemodeSpectatorCommand extends AbstractGameModeCommand {

    public GamemodeSpectatorCommand() {
        super(GameMode.SPECTATOR);
    }
}