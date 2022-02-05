package net.larskrs.plugins.duels.managers;

import net.larskrs.plugins.duels.Duels;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

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
    public static void serializeLocation(String url, Location loc) {
        config.set(url + ".world", loc.getWorld().getName());
        config.set(url + ".x", loc.getX());
        config.set(url + ".y", loc.getY());
        config.set(url + ".z", loc.getZ());
        config.set(url + ".yaw", loc.getYaw());
        config.set(url + ".pitch", loc.getPitch());
        duels.saveConfig();
    }
    public static String getGameType (int id) {
        return config.getString("arenas." + id + ".options.game-type");
    }
    public static int getGamePointsToWin (int id) {
        return config.getInt("arenas." + id + ".options.points-to-win");
    }
    public static String getArenaName(int id) { return config.getString("arenas." + id + ".options.name");}
    public static void setArenaName(int id, String name) {  config.set("arenas." + id + ".options.name", name); duels.saveConfig(); duels.reloadConfig(); }
    public static void setArenaLobbyLocation(int id, Location location) {
        serializeLocation("arenas." + id + ".spawn", location);
    }
    public static void setArenaTeamSpawn(int id, Team team, Location location) {
        serializeLocation("arenas." + id + ".teams." + team.name() + ".spawn", location);
        duels.saveConfig();

    }
    public static void setArena(int id, String game) {
        config.set("arenas." + id + ".options.game-type", game);
        duels.saveConfig();

    }
}
