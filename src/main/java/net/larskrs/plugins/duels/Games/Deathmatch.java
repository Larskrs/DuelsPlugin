package net.larskrs.plugins.duels.Games;

import net.larskrs.plugins.duels.enums.GameState;
import net.larskrs.plugins.duels.instances.Arena;
import net.larskrs.plugins.duels.managers.ConfigManager;
import net.larskrs.plugins.duels.managers.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
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
        this.points = new HashMap<>();

        for (Team t : Team.values()) {
            points.put(t, 0);

        }
    }

    @Override
    public void onNewRoundBegin() {
        for (UUID uuid : arena.getPlayers()) {
            Player p = Bukkit.getPlayer(uuid);
            p.teleport(ConfigManager.getTeamSpawn(arena.getId(),arena.getTeam(p)));
            p.setHealth(p.getMaxHealth());
            p.setFoodLevel(20);
            p.setArrowsInBody(0);
        }
    }


    @Override
    public void onStart() {
        arena.setState(GameState.LIVE);
        arena.sendMessage(ChatColor.GREEN + "Game has started! ");
        arena.sendMessage(ChatColor.RED + "[DE>THM>TCH] ");
        arena.sendMessage(ChatColor.RED + "[OBJECTIVE]" + ChatColor.GRAY + " Get " + ConfigManager.getGamePointsToWin(arena.getId()) + " kills for your team!");

    }

    @Override
    public void onCustomRespawn(Player hurt, Player killer) {
        if (arena.getPlayers().contains(hurt.getUniqueId()) && arena.getPlayers().contains(killer.getUniqueId()) && arena.getState().equals(GameState.LIVE)) {
            // Both players were in the live match.
            arena.sendMessage(ChatColor.GOLD + "  " + ChatColor.GREEN + hurt.getName() + " was killed by " + killer.getName() + "!");
            arena.sendMessage(ChatColor.GOLD + "" + arena.getTeam(killer).getDisplay() + ChatColor.YELLOW + "'s points (" + ChatColor.AQUA + (this.points.get(arena.getTeam(killer)) + 1) + ChatColor.YELLOW + "/" + ChatColor.AQUA + ConfigManager.getGamePointsToWin(arena.getId()) + ChatColor.YELLOW + ")" + "!");
            addPoint(arena.getTeam(killer));


        }
    }

    public void addPoint(Team team) {
        int teamPoints = points.get(team) + 1;
        if (teamPoints >= ConfigManager.getGamePointsToWin(arena.getId())) {
            arena.sendMessage(ChatColor.GOLD + "[GAME] " + ChatColor.GREEN + team.getDisplay() + " has won the game, thx for playing :)");
            arena.reset(true);
        }


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

