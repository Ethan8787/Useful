package dev.ethan.useful.commands.admin.gameMode;

import org.bukkit.GameMode;
import top.nontage.nontagelib.annotations.CommandInfo;

@CommandInfo(name = "gma", permission = "useful.admin.gma", description = "Set gamemode to adventure", override = true)
public class GamemodeAdventureCommand extends AbstractGameModeCommand {

    public GamemodeAdventureCommand() {
        super(GameMode.ADVENTURE);
    }
}
