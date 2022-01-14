package net.larskrs.plugins.duels.instances;

import net.larskrs.plugins.duels.enums.GameState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class Game {

    private Arena arena;
    private HashMap<UUID, Integer> points;

    public Game(Arena arena) {
        this.arena = arena;
        this.points = new HashMap<>();
    }

    public void start() {
        arena.setState(GameState.LIVE);
        arena.sendMessage(ChatColor.GREEN + "Game has started! ");
        arena.sendMessage(ChatColor.RED + "[OBJECTIVE]" + ChatColor.GRAY + " Kill the other team!");

        for (UUID uuid : arena.getPlayers()) {
            points.put(uuid, 0);
            Bukkit.getPlayer(uuid).closeInventory();
        }
    }
    public void addPoint(Player player) {
        int playerPoints = points.get(player.getUniqueId()) + 1;
        if (playerPoints == 3) {
            arena.sendMessage(ChatColor.GOLD + "[GAME]" + ChatColor.GREEN + player.getName() + " has won the game, thx for playing :)");
            arena.reset(true);
        }

        player.sendMessage(ChatColor.GREEN + "+1 POINT!");
        points.replace(player.getUniqueId(), playerPoints);
    }
}
