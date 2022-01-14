package net.larskrs.plugins.duels.instances;

import net.larskrs.plugins.duels.enums.GameState;
import net.larskrs.plugins.duels.managers.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class Game {

    private Arena arena;
    private HashMap<Team, Integer> points;

    public Game(Arena arena) {
        this.arena = arena;
        this.points = new HashMap<>();
    }

    public void start() {
        arena.setState(GameState.LIVE);
        arena.sendMessage(ChatColor.GREEN + "Game has started! ");
        arena.sendMessage(ChatColor.RED + "[OBJECTIVE]" + ChatColor.GRAY + " Kill the other team!");

        for (UUID uuid : arena.getPlayers()) {
            arena.getKits().get(uuid).onStart(Bukkit.getPlayer(uuid));
            Bukkit.getPlayer(uuid).closeInventory();
        }

        for (Team t : Team.values()) {
            points.put(t, 0);
        }
    }
    public void addPoint(Team team) {
        int teamPoints = points.get(team) + 1;
        if (teamPoints == 3) {
            arena.sendMessage(ChatColor.GOLD + "[GAME] " + ChatColor.GREEN + team.getDisplay() + " has won the game, thx for playing :)");
            arena.reset(true);
        }

        arena.sendMessage(ChatColor.GOLD + "[GAME] " +ChatColor.GREEN + "+1 POINT for " + team.getDisplay() + ChatColor.GREEN + "! ");
        points.replace(team, teamPoints);
    }
}
