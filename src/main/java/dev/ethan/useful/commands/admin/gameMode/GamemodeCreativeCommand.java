package dev.ethan.useful.commands.admin.gameMode;

import org.bukkit.GameMode;
import top.nontage.nontagelib.annotations.CommandInfo;

@CommandInfo(name = "gmc", permission = "useful.admin.gmc", description = "Set gamemode to creative", override = true)
public class GamemodeCreativeCommand extends AbstractGameModeCommand {
    public GamemodeCreativeCommand() {
        super(GameMode.CREATIVE);
    }
}