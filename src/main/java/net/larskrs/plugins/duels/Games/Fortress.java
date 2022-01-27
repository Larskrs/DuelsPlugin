package net.larskrs.plugins.duels.Games;

import net.larskrs.plugins.duels.Duels;
import net.larskrs.plugins.duels.enums.GameState;
import net.larskrs.plugins.duels.instances.Arena;
import net.larskrs.plugins.duels.managers.ConfigManager;
import net.larskrs.plugins.duels.managers.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.*;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class Fortress extends Game {

    private HashMap<Team, Integer> points;
    private HashMap<UUID, Integer> Playerpoints;


    private int pointsToWin;
    private Duels duels;


    public Fortress(Duels duels, Arena arena) {
        super(duels, arena);
        this.duels = duels;
        this.points = new HashMap<>();
        this.Playerpoints = new HashMap<>();

        for (Team t : Team.values()) {
            points.put(t, 0);
        }
    }

    @Override
    public void onNewRoundBegin() {

    }


    @Override
    public void onStart() {
        arena.setState(GameState.LIVE);

        this.pointsToWin = Math.round(ConfigManager.getGamePointsToWin(arena.getId()) * (arena.getPlayers().size() / 2));
        for (UUID uuid : arena.getPlayers()) {
            Playerpoints.put(uuid, 0);
        }
        arena.sendMessage(ChatColor.GREEN + "Game has started! ");
        arena.sendMessage(ChatColor.RED + "[F>RTR>SS] ");
        arena.sendMessage(ChatColor.RED + "[OBJECTIVE]" + ChatColor.GRAY + " Get as many points for your team as you can before the timer runs out!");
        arena.sendMessage(ChatColor.RED + "[OBJECTIVE]" + ChatColor.GRAY + "You can get points by looting barrels or killing players.");

        for (UUID uuid : arena.getPlayers()) {
            Player p = Bukkit.getPlayer(uuid);
            Scoreboard board = p.getScoreboard();
            Objective obj;
            if (board.getObjective("deathmatchBoard") == null) {
            obj = board.registerNewObjective("deathmatchBoard", "dummy");
            } else {
                obj = board.getObjective("deathmatchBoard");
            }
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
            obj.setDisplayName(ChatColor.YELLOW.toString() + ChatColor.BOLD + "DUELS");

            Set<String> scoreList = board.getEntries();
            for (String s : scoreList) {
                board.resetScores(s);
            }

            Score s1 = obj.getScore(""); s1.setScore(0);
            Score s2 = obj.getScore(ChatColor.AQUA + "Team: " + arena.getTeam(p).getDisplay()); s2.setScore(1);
            Score s3 = obj.getScore(ChatColor.RED + ""); s3.setScore(2);

            Objective h = board.registerNewObjective("showhealth", Criterias.HEALTH);
            h.setDisplaySlot(DisplaySlot.BELOW_NAME);
            h.setDisplayName(ChatColor.DARK_RED + "❤");



            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
            obj.setDisplayName(ChatColor.YELLOW.toString() + ChatColor.BOLD + "DUELS");

            p.setScoreboard(board);

            p.setFireTicks(0);

        }

    }
    public void lootStorage(Player player, Block block) {
        arena.sendMessage(arena.getTeam(player).getDisplay() + ChatColor.GOLD + " just looted a " + block.getType().name().toLowerCase() + " " + ChatColor.YELLOW + "'s points (" + ChatColor.AQUA + (this.points.get(arena.getTeam(player)) + 1) + ChatColor.YELLOW + "/" + ChatColor.AQUA + pointsToWin + ChatColor.YELLOW + ")" + "!");
    }

    @Override
    public void onCustomRespawn(Player hurt, Player killer) {

        Player lHit = killer;

        if (arena.getPlayers().contains(hurt.getUniqueId()) && arena.getPlayers().contains(lHit.getUniqueId()) && arena.getState().equals(GameState.LIVE)) {
            // Both players were in the live match.
            arena.sendMessage(ChatColor.GOLD + "  " + ChatColor.GREEN + hurt.getName() + " was killed by " + lHit.getName() + "!");
            arena.sendMessage(ChatColor.GOLD + "" + arena.getTeam(lHit).getDisplay() + ChatColor.YELLOW + "'s points (" + ChatColor.AQUA + (this.points.get(arena.getTeam(lHit)) + 1) + ChatColor.YELLOW + "/" + ChatColor.AQUA + pointsToWin + ChatColor.YELLOW + ")" + "!");
            addPoint(arena.getTeam(lHit));
            Playerpoints.replace(killer.getUniqueId(), Playerpoints.get(killer.getUniqueId()) + 1);


        }
    }

    public void addPoint(Team team) {
        int teamPoints = points.get(team) + 1;
        if (teamPoints >= pointsToWin) {
            arena.sendMessage(ChatColor.GOLD + "[GAME] " + ChatColor.GREEN + team.getDisplay() + " has won the game, thx for playing :)");
            for (UUID pl : arena.getPlayers()) {
                if (arena.getTeam(Bukkit.getPlayer(pl)) == team) {
                    arena.sendMessage(ChatColor.GRAY + " - " + team.getDisplay() + " " + Bukkit.getPlayer(pl).getName());
                }
            }
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
