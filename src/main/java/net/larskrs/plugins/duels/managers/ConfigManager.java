package net.larskrs.plugins.duels.managers;

import net.larskrs.plugins.duels.Duels;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {

    private static FileConfiguration config;

    public static void setupConfig (Duels duels) {
        System.out.println("Loading ConfigManager...");
        ConfigManager.config = duels.getConfig();
        duels.saveDefaultConfig();
    }

    public static int getRequiredPlayers() { return config.getInt("required-players");}
    public static int getStartupTime() { return config.getInt("startup-time");}
    public static Location getLobbySpawnLocation() {
        return getLocation("lobby-spawn");
    }
    public static Location getArenaSpawn(int id) {
        return getLocation("arenas." + id + ".spawn");
    }


    public static Location getLocation(String configLocation) {
        return new Location(
                Bukkit.getWorld(config.getString(configLocation + ".world")),
         config.getDouble(configLocation + ".x"),
         config.getDouble(configLocation + ".y"),
         config.getDouble(configLocation + ".z"),
         (float) config.getDouble(configLocation + ".yaw"),
         (float) config.getDouble(configLocation + ".pitch")
        );
    }
 }
