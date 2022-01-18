package net.larskrs.plugins.duels.Games;

import net.larskrs.plugins.duels.Duels;
import net.larskrs.plugins.duels.enums.GameState;
import net.larskrs.plugins.duels.instances.Arena;
import net.larskrs.plugins.duels.managers.ConfigManager;
import net.larskrs.plugins.duels.managers.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.UUID;

public abstract class Game implements Listener {

    protected Arena arena;


    public Game(Duels duels, Arena arena) {
        this.arena = arena;

        Bukkit.getPluginManager().registerEvents(this, duels);
    }

    public void start() {


        for (UUID uuid : arena.getPlayers()) {
            Player p = Bukkit.getPlayer(uuid);
            arena.getKits().get(uuid).onStart(p);
            p.closeInventory();
            p.teleport(ConfigManager.getTeamSpawn(arena.getId(), arena.getTeam(p)));

        }



        onStart();
    }
    public abstract void onNewRoundBegin();
    public abstract void onStart();

    public void unregister() {
        HandlerList.unregisterAll(this);
    }
    public abstract void onCustomRespawn(Player hurt, Player killer);
}