package net.larskrs.plugins.duels.Games;

import dev.jcsoftware.jscoreboards.*;
import net.larskrs.plugins.duels.Files.PlayerDataFile;
import net.larskrs.plugins.duels.enums.GameState;
import net.larskrs.plugins.duels.instances.Arena;
import net.larskrs.plugins.duels.instances.LiveGameTimer;
import net.larskrs.plugins.duels.listener.RespawnCountdown;
import net.larskrs.plugins.duels.managers.ArenaManager;
import net.larskrs.plugins.duels.managers.ConfigManager;
import net.larskrs.plugins.duels.managers.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import net.larskrs.plugins.duels.Duels;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.*;

public class Deathmatch extends Game {

    private HashMap<Team, Integer> points;
    private HashMap<UUID, Integer> Playerpoints;
    private int pointsToWin;
    private Duels duels;
    private Team winner;
    private JPerPlayerScoreboard scoreboard;


    public Deathmatch(Duels duels, Arena arena) {
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
        for (UUID uuid : arena.getPlayers()) {
            Player p = Bukkit.getPlayer(uuid);
            p.teleport(ConfigManager.getTeamSpawn(arena.getId(), arena.getTeam(p)));
            p.setHealth(p.getMaxHealth());
            p.setFoodLevel(20);
            p.setArrowsInBody(0);
        }
    }


    @Override
    public void onStart() {
        arena.setState(GameState.LIVE);

        this.pointsToWin = Math.round(ConfigManager.getGamePointsToWin(arena.getId()) * (arena.getPlayers().size() / 2));
        for (UUID uuid : arena.getPlayers()) {
            Playerpoints.put(uuid, 0);
        }
        liveGameTimer = new LiveGameTimer(duels, arena, 120* pointsToWin);
        liveGameTimer.start();
        arena.sendMessage(ChatColor.GREEN + "Game has started! ");
        arena.sendMessage(ChatColor.RED + "[DE>THM>TCH] ");
        arena.sendMessage(ChatColor.RED + "[OBJECTIVE]" + ChatColor.GRAY + " Get " + pointsToWin + " kills for your team!");


            for (UUID uuid : arena.getPlayers()) {
                Player p = Bukkit.getPlayer(uuid);
                assert p != null;
                p.setFireTicks(0);
                p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 9999, 5));
                p.sendTitle(arena.getTeam(p).getDisplay(), ChatColor.GRAY + "You are playing as: " + arena.getTeam(p).getDisplay(), 1, 2, 1);

            }
            this.scoreboard= new JPerPlayerScoreboard(
                    (player) -> {
                        return "&e&lDUELS";
                    },
                    (player) -> {
                            return Arrays.asList(
                                    "",
                                    "&eTime " + liveGameTimer.getOutput(),
                                    "&d",
                                    Team.RED.getDisplay() + ChatColor.AQUA + " " + points.get(Team.RED) + (arena.getTeam(player).equals(Team.RED) ? ChatColor.GRAY + " (you)" : ""),
                                    Team.BLUE.getDisplay() + ChatColor.AQUA + " " + points.get(Team.BLUE) + (arena.getTeam(player).equals(Team.BLUE) ? ChatColor.GRAY + " (you)" : ""),
                                    "&2"
                            );
                    }

            );
            scoreboard.setOptions(new JScoreboardOptions(JScoreboardTabHealthStyle.HEARTS, true));
        for (Team t : Team.values()) {
            JScoreboardTeam jScoreboardTeam = scoreboard.createTeam(t.name(), t.getDisplay() + " ", t.getChatColor());
            for (UUID u : arena.getTeams().keySet()) {
                if (arena.getTeam(Bukkit.getPlayer(u)) == t) {
                    jScoreboardTeam.addPlayer(Bukkit.getPlayer(u));
                }
            }
        }

        List<Player> players = new ArrayList<>();
        for (UUID u: arena.getPlayers()
        ) {
            players.add(Bukkit.getPlayer(u));

        }
        players.forEach(this::addToScoreboard);
    }
    private void addToScoreboard(Player player) {
        scoreboard.addPlayer(player);
        scoreboard.updateScoreboard();
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
            scoreboard.updateScoreboard();


        }
    }

    @Override
    public void addPoint(Team team) {
        int teamPoints = points.get(team) + 1;
        if (teamPoints >= pointsToWin + 1) {
            arena.sendMessage(ChatColor.GOLD + "[GAME] " + ChatColor.GREEN + team.getDisplay() + " has won the game, thx for playing :)");
            for (UUID pl : arena.getPlayers()) {
                if (arena.getTeam(Bukkit.getPlayer(pl)) == team) {
                    arena.sendMessage(ChatColor.GRAY + " - " + team.getDisplay() + " " + Bukkit.getPlayer(pl).getName());
                    PlayerDataFile.addPlayerWin(Bukkit.getPlayer(pl), 1);
                }
            }
            winner = team;
            endGame();

        }


        points.replace(team, teamPoints);
    }

    @Override
    public void endGame() {

        liveGameTimer.endGameTime();

        arena.sendMessage("§6§l§m|------------|§c§l GAME OVER "  + " §6§l§m|------------|");
        arena.sendMessage("");
        if (winner != null)  {
        arena.sendMessage(ChatColor.YELLOW + "  WINNER - " + winner.getDisplay());
        }
        arena.sendMessage(ChatColor.GREEN +"  - game resets in 10 seconds.");
        arena.sendMessage("");


    }

    @Override
    public void onScoreboardUpdate() {
        if (scoreboard!= null) {scoreboard.updateScoreboard(); }
    }

    @EventHandler
    public void onPlayerKill(PlayerDeathEvent e) {

        if (duels.getArenaManager().getArena(e.getEntity()) != arena) {
            return;
        }

        if (e.getEntity().getKiller() != null) {
            Player killer = e.getEntity().getKiller();
            Player p = e.getEntity();

            e.getDrops().clear();
            Random r = new Random();
            e.getDrops().add(new ItemStack(Material.ARROW, r.nextInt(4 - 1) + 1));
            e.getDrops().add(new ItemStack(Material.COOKED_BEEF, r.nextInt(4 - 1) + 1));
            arena.sendMessage(ChatColor.GOLD + "  " + ChatColor.GREEN + p.getName() + " was killed!");

            new RespawnCountdown(duels, p, 10).start();
            arena.getGame().onCustomRespawn(p, killer);
            p.setGameMode(GameMode.SPECTATOR);
            p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 9999, 5));

        } else {
            Player p = e.getEntity();

            e.getDrops().clear();
            Random r = new Random();
            e.getDrops().add(new ItemStack(Material.ARROW, r.nextInt(4 - 1) + 1));
            e.getDrops().add(new ItemStack(Material.COOKED_BEEF, r.nextInt(4 - 1) + 1));
            arena.sendMessage(ChatColor.GOLD + "  " + ChatColor.GREEN + p.getName() + " was killed!");
            new RespawnCountdown(duels, p, 10).start();
            p.setGameMode(GameMode.SPECTATOR);
        }

    }
}
