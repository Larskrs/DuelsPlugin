package net.larskrs.plugins.duels.Games;

import net.larskrs.plugins.duels.enums.GameState;
import net.larskrs.plugins.duels.instances.Arena;
import net.larskrs.plugins.duels.managers.ConfigManager;
import net.larskrs.plugins.duels.managers.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;
import net.larskrs.plugins.duels.Duels;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class Deathmatch extends Game {

    private HashMap<Team, Integer> points;
    private Duels duels;

    public Deathmatch(Duels duels, Arena arena) {
        super(duels, arena);
        this.duels = duels;
        points = new HashMap<>();

        arena.setState(GameState.LIVE);
        arena.sendMessage(ChatColor.GREEN + "Game has started! ");
        arena.sendMessage(ChatColor.RED + "[DE>THM>TCH] ");
        arena.sendMessage(ChatColor.RED + "[OBJECTIVE]" + ChatColor.GRAY + " Get " + duels.getConfig().getInt("arena." + arena.getId() + ".options.deathmatch-kills-to-win") + " kills for your team!");

        onStart();

        for (UUID uuid : arena.getPlayers()) {
            Player p = Bukkit.getPlayer(uuid);
            arena.getKits().get(uuid).onStart(p);
            p.closeInventory();
            p.teleport(ConfigManager.getTeamSpawn(arena.getId(), arena.getTeam(p)));

        }

        for (Team t : Team.values()) {
            points.put(t, 0);

        }
    }


    @Override
    public void onStart() {

    }

    public void addPoint(Team team) {
        int teamPoints = points.get(team) + 1;
        if (teamPoints >= arena.getOptions().winAmount) {
            arena.sendMessage(ChatColor.GOLD + "[GAME] " + ChatColor.GREEN + team.getDisplay() + " has won the game, thx for playing :)");
            arena.reset(true);
        }

        arena.sendMessage(ChatColor.GOLD + "[GAME] " +ChatColor.GREEN + "+1 POINT for " + team.getDisplay() + ChatColor.GREEN + "! ");
        points.replace(team, teamPoints);
    }

    @EventHandler
    public void onPlayerKill(PlayerDeathEvent e) {

        if (e.getEntity().getKiller() != null) {
            Player killer = e.getEntity().getKiller();
            Player p = e.getEntity();

            if (duels.getArenaManager().getArena(p) != null && duels.getArenaManager().getArena(killer) != null) {
                // The two players are both in arena.
                Arena pArena = duels.getArenaManager().getArena(p);
                Arena killerArena = duels.getArenaManager().getArena(killer);
                if (killerArena == pArena && killerArena.getState().equals(GameState.LIVE)) {
                    // Both players were in the live match.
                    killerArena.sendMessage(ChatColor.GOLD + "[GAME]" + ChatColor.GREEN + p.getName() + " was killed by " + killer.getName() + "!");
                    addPoint(pArena.getTeam(killer));

                    e.getDrops().clear();
                    e.getDrops().add(new ItemStack(Material.GOLDEN_APPLE));

                }
            }



        }

    }
}
