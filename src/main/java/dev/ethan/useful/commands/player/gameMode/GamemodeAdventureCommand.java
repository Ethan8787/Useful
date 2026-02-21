package dev.ethan.useful.commands.player.gameMode;

import org.bukkit.GameMode;
import top.nontage.nontagelib.annotations.CommandInfo;

@CommandInfo(name = "gma", permission = "guildwars.player.gma", description = "Set gamemode to adventure", override = true)
public class GamemodeAdventureCommand extends AbstractGameModeCommand {

    public GamemodeAdventureCommand() {
        super(GameMode.ADVENTURE);
    }
}
