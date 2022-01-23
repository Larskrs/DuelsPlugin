package net.larskrs.plugins.duels.tools;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

public class WorldLoaderTool {


    public static void loadWorld(String worldName) {
        Bukkit.getServer().createWorld(new WorldCreator(worldName));
    }

}
