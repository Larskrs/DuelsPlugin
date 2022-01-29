package net.larskrs.plugins.duels.managers;

import net.larskrs.plugins.duels.Duels;
import net.larskrs.plugins.duels.instances.Arena;
import net.larskrs.plugins.duels.tools.WorldLoaderTool;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ArenaManager {

    private Duels duels;
    private List<Arena> arenas;

    public ArenaManager (Duels duels) {

        this.duels = duels;
        arenas  = new ArrayList<>();
        FileConfiguration config = duels.getConfig();
        for (String s : config.getConfigurationSection("arenas").getKeys(false)) {
            arenas.add(new Arena(duels, Integer.parseInt(s), ConfigManager.getArenaSpawn(Integer.parseInt(s))));
                WorldLoaderTool.loadWorld(ConfigManager.getArenaSpawn(Integer.parseInt(s)).getWorld().getName());
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "loaded: " + arenas.size() + " arenas.");
    }

    public List<Arena> getArenas () { return arenas; }
    public Arena getArena (Player player) {
        for (Arena arena : arenas) {
            if (arena.getPlayers().contains(player.getUniqueId())) {
                return arena;
            }
        }
        return null;
    }
    public Arena getArena (String name) {
        for (Arena arena : arenas) {
            System.out.println(arena.getName() + " ... " + name);
            if (arena.getName().equalsIgnoreCase(name)) {
                System.out.println(arena.getName() + " == " + name);
                return arena;
            }
        }
        return null;
    }
    public Arena getArena(int id) {
        for (Arena arena : arenas) {
            if (arena.getId() == id) {
                return arena;
            }
        }
        return null;
    }

}
