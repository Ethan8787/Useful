package dev.ethan.useful.managers;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class EconomyManager {

    private static Economy econ;

    public EconomyManager(JavaPlugin plugin) {
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
            econ = null;
            plugin.getLogger().severe("Vault not found! Economy features will be disabled.");
            return;
        }

        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            econ = null;
            plugin.getLogger().severe("Cannot find any economy provider! Economy features will be disabled.");
            return;
        }

        econ = rsp.getProvider();
        plugin.getLogger().info("Successfully connect to economy system: " + econ.getName());
    }

    public static Economy getEconomy() {
        return econ;
    }
}