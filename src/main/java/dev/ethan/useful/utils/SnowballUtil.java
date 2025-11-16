package dev.ethan.useful.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Snowball;
import org.bukkit.plugin.java.JavaPlugin;

public class SnowballUtil {
    private final JavaPlugin plugin;
    private final ProtocolManager manager;
    private PacketAdapter listener;

    public SnowballUtil(JavaPlugin plugin) {
        this.plugin = plugin;
        this.manager = ProtocolLibrary.getProtocolManager();
        register();
    }

    private void register() {
        listener = new PacketAdapter(plugin, ListenerPriority.HIGHEST, PacketType.Play.Server.SPAWN_ENTITY) {
            @Override
            public void onPacketSending(PacketEvent e) {
                int entityId = e.getPacket().getIntegers().read(0);
                Entity entity = manager.getEntityFromID(e.getPlayer().getWorld(), entityId);

                if (entity instanceof Snowball && entity.getScoreboardTags().contains("gunbullet_invisible")) {
                    e.setCancelled(true);
                }
            }
        };
        manager.addPacketListener(listener);
    }
}
