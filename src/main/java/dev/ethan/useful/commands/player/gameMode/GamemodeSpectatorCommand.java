package dev.ethan.useful.commands.player.gameMode;

import org.bukkit.GameMode;
import top.nontage.nontagelib.annotations.CommandInfo;

@CommandInfo(name = "gmsp", permission = "guildwars.player.gmsp", description = "Set gamemode to spectator", override = true)
public class GamemodeSpectatorCommand extends AbstractGameModeCommand {

    public GamemodeSpectatorCommand() {
        super(GameMode.SPECTATOR);
    }
}