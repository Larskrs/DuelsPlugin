package net.larskrs.plugins.duels.managers;

import net.larskrs.plugins.duels.Duels;
import net.larskrs.plugins.duels.instances.Arena;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import sun.security.util.Debug;

import java.util.Objects;

public class ConfigManager {

    private static FileConfiguration config;
    private static Duels duels;

    public static void setupConfig(Duels main) {
        duels = main;
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "loading... config.yml");
        ConfigManager.config = duels.getConfig();
        duels.saveDefaultConfig();
    }

    public static int getRequiredPlayers() {
        return config.getInt("required-players");
    }

    public static int getStartupTime() {
        return config.getInt("startup-time");
    }

    public static Location getLobbySpawnLocation() {
        return getLocation("lobby-spawn");
    }

    public static Location getArenaSpawn(int id) {
        return getLocation("arenas." + id + ".spawn");
    }

    public static Location getTeamSpawn(int id, Team team) {
        for (String s : config.getConfigurationSection("arenas." + id + ".teams").getKeys(false)) {
            System.out.println(s);
            if (s.equalsIgnoreCase(team.name())) {

                        return getLocation("arenas." + id + ".teams." + team.name() + ".spawn");


            }
        }
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
    public static String getGameType (int id) {
        return config.getString("arenas." + id + ".options.game-type");
    }
    public static int getGamePointsToWin (int id) {
        return config.getInt("arenas." + id + ".options.points-to-win");
    }
    public static String getArenaName(int id) { return ChatColor.translateAlternateColorCodes('&', config.getString("arenas." + id + ".options.name"));}
    public static void setArenaName(int id, String name) {  config.set("arenas." + id + ".options.name", name); duels.saveConfig(); duels.reloadConfig(); }
}
