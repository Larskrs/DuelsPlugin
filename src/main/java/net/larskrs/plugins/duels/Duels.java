package net.larskrs.plugins.duels;

import com.sun.org.apache.xerces.internal.impl.xs.identity.XPathMatcher;
import net.larskrs.plugins.duels.Files.KitsFile;
import net.larskrs.plugins.duels.Files.PlayerDataFile;
import net.larskrs.plugins.duels.commands.DuelCommand;
import net.larskrs.plugins.duels.listener.ConnectListener;
import net.larskrs.plugins.duels.listener.GameListener;
import net.larskrs.plugins.duels.managers.ArenaManager;
import net.larskrs.plugins.duels.managers.ConfigManager;
import net.larskrs.plugins.duels.tools.XEntityType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;

public final class Duels extends JavaPlugin {

    private ArenaManager arenaManager;
    private static Duels instance;

    @Override
    public void onEnable() {
        instance = this;

        Bukkit.getConsoleSender().sendMessage("§6§m------------ §6Simple Duels §6§m------------");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "developed by: Larskrs");
        ConfigManager.setupConfig(this); // STEP 1
        arenaManager = new ArenaManager(this); // STEP 2

        Bukkit.getPluginManager().registerEvents(new GameListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ConnectListener(this), this);

        getCommand("duel").setExecutor(new DuelCommand(this));

        new KitsFile(this);
        new PlayerDataFile(this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    public ArenaManager getArenaManager () { return arenaManager; }

    public static Duels getInstance() {
        return instance;
    }
}
