package net.larskrs.plugins.duels;

import net.larskrs.plugins.duels.Files.KitsFile;
import net.larskrs.plugins.duels.Files.PlayerDataFile;
import net.larskrs.plugins.duels.commands.DuelCommand;
import net.larskrs.plugins.duels.listener.ConnectListener;
import net.larskrs.plugins.duels.listener.GameListener;
import net.larskrs.plugins.duels.listener.ItemListener;
import net.larskrs.plugins.duels.managers.ArenaManager;
import net.larskrs.plugins.duels.managers.ConfigManager;
import net.larskrs.plugins.duels.managers.NametagManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class Duels extends JavaPlugin {

    private ArenaManager arenaManager;
    private static Duels instance;
    private static UpdateLoop loop;
    private static GameListener gameListener;

    @Override
    public void onEnable() {

        instance = this;
        reload();

    }



    @Override
    public void onDisable() {
        // Plugin shutdown logic
        loop.stop();
    }

    public void reload() {

        Bukkit.getConsoleSender().sendMessage("§6§m------------ §6Simple Duels §6§m------------");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "developed by: Larskrs");
        ConfigManager.setupConfig(this); // STEP 1
        arenaManager = new ArenaManager(this); // STEP 2

        gameListener = new GameListener(this);
        Bukkit.getPluginManager().registerEvents(gameListener, this);
        Bukkit.getPluginManager().registerEvents(new ConnectListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ItemListener(), this);

        getCommand("duel").setExecutor(new DuelCommand(this));
        getCommand("duel").setTabCompleter(new DuelCommand(this));

        new KitsFile(this);
        new PlayerDataFile(this);
        loop = new UpdateLoop(this);
        //NametagManager.setNameTags();
    }
    public ArenaManager getArenaManager () { return arenaManager; }

    public static Duels getInstance() {
        return instance;
    }
    public static GameListener getGameListener () {return gameListener; }
}
