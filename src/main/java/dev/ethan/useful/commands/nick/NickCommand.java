package dev.ethan.useful.commands.nick;

import dev.ethan.useful.Main;
import dev.ethan.useful.constants.Messages;
import dev.ethan.useful.utils.LuckPermsUtil;
import dev.ethan.useful.utils.PlayerUtil;
import dev.iiahmed.disguise.Disguise;
import dev.iiahmed.disguise.DisguiseManager;
import dev.iiahmed.disguise.DisguiseProvider;
import dev.iiahmed.disguise.SkinAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

import java.util.UUID;

//todo: Fix nick not working properly
@CommandInfo(name = "nick", permission = "useful.nick.use", description = "Change nickname", override = true)
public class NickCommand implements NontageCommand {

    private final PlayerUtil playerUtil = Main.getInstance().getPlayerUtil();
    private final LuckPermsUtil luckPermsUtil = Main.getInstance().getLuckPermsUtil();
    private final DisguiseProvider provider = DisguiseManager.getProvider();

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage(Messages.PREFIX + "§cOnly players can use this command.");
            return;
        }

        if (args.length == 0) {
            p.sendMessage(Messages.PREFIX + "§cUsage: /nick <nickname> [--skin]");
            return;
        }

        String nick = args[0].replace("&", "§");
        boolean changeSkin = hasSkinFlag(args);

        p.sendMessage(Messages.PREFIX + "§7Processing...");

        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            UUID skinUUID;
            try {
                skinUUID = changeSkin ? resolveSkin(p, nick) : p.getUniqueId();
            } catch (Exception e) {
                skinUUID = p.getUniqueId();
            }

            final UUID finalSkinUUID = skinUUID;

            Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                Main.nick().setNickname(p, nick);

                Disguise disguise = Disguise.builder()
                        .setName(nick)
                        .setSkin(SkinAPI.MOJANG, finalSkinUUID)
                        .build();

                provider.disguise(p, disguise);

                p.sendMessage(Messages.PREFIX + "§aNick set: §f" + luckPermsUtil.getPlayerPrefix(p) + nick + (changeSkin ? " §7(Skin)" : ""));
            });
        });
    }

    private UUID resolveSkin(Player p, String nick) {
        try {
            UUID uuid = playerUtil.getUUID(nick);
            return uuid != null ? uuid : p.getUniqueId();
        } catch (Exception ignored) {
            return p.getUniqueId();
        }
    }

    private boolean hasSkinFlag(String[] args) {
        for (String arg : args) {
            if (arg.equalsIgnoreCase("--skin")) return true;
        }
        return false;
    }
}