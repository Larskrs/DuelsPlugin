package net.larskrs.plugins.duels.Games;

import net.larskrs.plugins.duels.Duels;
import net.larskrs.plugins.duels.enums.GameState;
import net.larskrs.plugins.duels.instances.Arena;
import net.larskrs.plugins.duels.managers.ConfigManager;
import net.larskrs.plugins.duels.managers.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class LastStanding extends Game {

    private HashMap<Team, Integer> points;
    private Duels duels;

    public LastStanding(Duels duels, Arena arena) {
        super(duels, arena);
        this.duels = duels;
        this.points = new HashMap<>();

    }

    @Override
    public void onNewRoundBegin() {

    }



    @Override
    public void onStart() {
        arena.setState(GameState.LIVE);
        arena.sendMessage(ChatColor.GREEN + "Game has started! ");
        arena.sendMessage(ChatColor.RED + "[L<ST ST>NDING] ");
        arena.sendMessage(ChatColor.RED + "[OBJECTIVE]" + ChatColor.GRAY + " Kill the other team! Your team shall remain!");

        for (UUID uuid : arena.getPlayers()) {
            Player p = Bukkit.getPlayer(uuid);
            arena.getKits().get(uuid).giveKit(p);
            p.closeInventory();
            p.teleport(ConfigManager.getTeamSpawn(arena.getId(), arena.getTeam(p)));

        }

        for (Team t : Team.values()) {
            points.put(t, 0);

        }
    }

    @Override
    public void onCustomRespawn(Player hurt, Player killer) {
        if (arena.getPlayers().contains(hurt.getUniqueId()) && arena.getPlayers().contains(killer.getUniqueId()) && arena.getState().equals(GameState.LIVE)) {
            // Both players were in the live match.
            arena.sendMessage(ChatColor.GOLD + "  " + ChatColor.GREEN + hurt.getName() + " was killed by " + killer.getName() + "!");
            addPoint(arena.getTeam(killer));


        }
    }

    public void addPoint(Team team) {
        int teamPoints = points.get(team) + 1;
        if (teamPoints >= arena.getPlayers().size() - arena.getTeamCount(team)) {
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

                if (arena.getPlayers().contains(p.getUniqueId()) && arena.getPlayers().contains(killer.getUniqueId()) && arena.getState().equals(GameState.LIVE)) {
                    // Both players were in the live match.
                    arena.sendMessage(ChatColor.GOLD + "[GAME]" + ChatColor.GREEN + p.getName() + " was killed by " + killer.getName() + "!");
                    addPoint(arena.getTeam(killer));

                    e.getDrops().clear();
                    e.getDrops().add(new ItemStack(Material.GOLDEN_APPLE));

                }
            }



        }


    }
