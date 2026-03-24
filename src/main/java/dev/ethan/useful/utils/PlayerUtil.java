package dev.ethan.useful.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.ethan.useful.Main;
import dev.ethan.useful.managers.GameManager;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class PlayerUtil {
    private final GameManager gameManager;

    public PlayerUtil(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public void shoot(Player player) {
        Location eyeLoc = player.getEyeLocation();
        Vector direction = eyeLoc.getDirection().normalize();
        Snowball snowball = player.getWorld().spawn(eyeLoc, Snowball.class, s -> {
            s.addScoreboardTag("gunbullet_invisible");
            s.setShooter(player);
            s.teleport(eyeLoc.add(direction));
            s.setCustomName("GunBullet");
            s.setGravity(false);
            s.setVelocity(direction.multiply(500));
        });

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1f, 1.5f);
    }

    public boolean isVanished(Player player) {
        for (MetadataValue meta : player.getMetadata("vanished")) {
            if (meta.asBoolean()) return true;
        }
        return false;
    }

    public boolean isNicked(Player player) {
        String nickname = Main.nick().getNickname(player);
        return nickname != null && !nickname.isEmpty();
    }

    public UUID getUUID(String name) throws IOException {
        URL url = new URL("https://playerdb.co/api/player/minecraft/" + name);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(5000);
        conn.setConnectTimeout(5000);
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() != 200) return null;
        InputStreamReader reader = new InputStreamReader(conn.getInputStream());
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(reader);
        JsonObject json = element.getAsJsonObject();
        JsonObject data = json.getAsJsonObject("data");
        JsonObject player = data.getAsJsonObject("player");
        String id = player.get("id").getAsString();
        return UUID.fromString(id);
    }
}
