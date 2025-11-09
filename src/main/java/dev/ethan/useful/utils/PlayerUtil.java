package dev.ethan.useful.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.ethan.useful.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.util.Vector;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.logging.Level;

import static de.myzelyam.api.vanish.VanishAPI.isInvisible;
import static dev.ethan.useful.Main.*;

public class PlayerUtil {
    public static void shoot(Player player) {
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

    public static void sendToServer(Player player, String serverName) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);

        try {
            out.writeUTF("Connect");
            out.writeUTF(serverName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        player.sendPluginMessage(Main.getInstance(), "BungeeCord", b.toByteArray());
    }

    public static boolean isVanished(Player player) {
        return isInvisible(player);
    }

    public static boolean isNicked(Player player) {
        String nickname = nickStorage.getNickname(player);
        return nickname != null && !nickname.isEmpty();
    }

    public static UUID getUUID(String name) throws IOException {
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

    public static void savePluginResource(String resourcePath, boolean replace) {
        Main.getInstance().saveResource(resourcePath, replace);
    }

    public static File getPluginDataFolder() {
        return Main.getInstance().getDataFolder();
    }

    public static void loadStats() {
        statsFile = new File(getPluginDataFolder(), "stats.yml");
        if (!statsFile.exists()) {
            savePluginResource("stats.yml", false);
        }
        statsConfig = YamlConfiguration.loadConfiguration(statsFile);
    }

    public static void saveStats() {
        try {
            statsConfig.save(statsFile);
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Error", e);
        }
    }

    public static int getStat(Player player, String stat) {
        return statsConfig.getInt("players." + player.getUniqueId() + "." + stat, 0);
    }

    public static void setStat(Player player, String stat, int value) {
        statsConfig.set("players." + player.getUniqueId() + "." + stat, value);
    }
}
