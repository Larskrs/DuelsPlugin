package net.larskrs.plugins.duels;

import net.larskrs.plugins.duels.commands.DuelCommand;
import net.larskrs.plugins.duels.listener.ConnectListener;
import net.larskrs.plugins.duels.listener.GameListener;
import net.larskrs.plugins.duels.managers.ArenaManager;
import net.larskrs.plugins.duels.managers.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class Duels extends JavaPlugin {

    private ArenaManager arenaManager;
    private static Duels instance;

    @Override
    public void onEnable() {
        instance = this;
        ConfigManager.setupConfig(this); // STEP 1
        arenaManager = new ArenaManager(this); // STEP 2

        Bukkit.getPluginManager().registerEvents(new GameListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ConnectListener(this), this);

        getCommand("duel").setExecutor(new DuelCommand(this));

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
