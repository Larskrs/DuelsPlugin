package net.larskrs.plugins.duels.Games;

import dev.jcsoftware.jscoreboards.JPerPlayerScoreboard;
import dev.jcsoftware.jscoreboards.JScoreboardOptions;
import dev.jcsoftware.jscoreboards.JScoreboardTabHealthStyle;
import dev.jcsoftware.jscoreboards.JScoreboardTeam;
import net.larskrs.plugins.duels.Duels;
import net.larskrs.plugins.duels.Files.PlayerDataFile;
import net.larskrs.plugins.duels.enums.GameState;
import net.larskrs.plugins.duels.instances.Arena;
import net.larskrs.plugins.duels.instances.LiveGameTimer;
import net.larskrs.plugins.duels.listener.RespawnCountdown;
import net.larskrs.plugins.duels.managers.ConfigManager;
import net.larskrs.plugins.duels.managers.Team;
import net.larskrs.plugins.duels.tools.StorageBlockTool;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.*;

import java.security.Key;
import java.util.*;

public class Fortress extends Game {

    private HashMap<Team, Integer> points;
    private HashMap<UUID, Integer> Playerpoints;
    private HashMap<Team, Integer> teamCount;
    private int pointsToWin;
    private Duels duels;
    private Team winner;
    private JPerPlayerScoreboard scoreboard;
    private List<Block> emptyContainers;


    public Fortress(Duels duels, Arena arena) {
        super(duels, arena);
        this.duels = duels;
        this.points = new HashMap<>();
        this.Playerpoints = new HashMap<>();
        this.teamCount = new HashMap<>();
        this.emptyContainers = new ArrayList<>();

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

        liveGameTimer = new LiveGameTimer(duels, arena, 600);
        liveGameTimer.start();

        this.pointsToWin = Math.round(arena.getPlayers().size() / 2);
        for (UUID uuid : arena.getPlayers()) {
            Playerpoints.put(uuid, 0);
        }
        arena.sendMessage(ChatColor.GREEN + "Game has started! ");
        arena.sendMessage(ChatColor.RED + "[F<RTR<SS] ");
        arena.sendMessage(ChatColor.RED + "[OBJECTIVE]" + ChatColor.GRAY + " your team may only remain!");
        arena.sendMessage(ChatColor.RED + "[OBJECTIVE]" + ChatColor.GRAY + " get as many points as you can!");



        for (UUID uuid : arena.getPlayers()) {
            Player p = Bukkit.getPlayer(uuid);
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


        }
    }

    @Override
    public void addPoint(Team team) {
        int teamPoints = points.get(team) + 1;
        arena.sendMessage(ChatColor.GOLD + "" + team.getDisplay() + ChatColor.YELLOW + "'s points (" + ChatColor.AQUA + (this.points.get(team) + 1) + ChatColor.YELLOW + "/" + ChatColor.AQUA + pointsToWin + ChatColor.YELLOW + ")" + "!");
        points.replace(team, teamPoints);
    }

    @Override
    public void endGame() {

        liveGameTimer.endGameTime();
        Team winningTeam = Collections.max(points.entrySet(), Map.Entry.comparingByValue()).getKey();
        winner = winningTeam;

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

            Player killer = e.getEntity().getKiller();
            Player p = e.getEntity();

            e.getDrops().clear();
            Random r = new Random();
            e.getDrops().add(new ItemStack(Material.ARROW, r.nextInt(4 - 1) + 1));
            e.getDrops().add(new ItemStack(Material.COOKED_BEEF, r.nextInt(4 - 1) + 1));
            if (killer != null) { onCustomRespawn(p, killer); }

            new RespawnCountdown(duels, p, 60).start();
            p.setGameMode(GameMode.SPECTATOR);
            p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 9999, 5));


    }
    @EventHandler
    public void onChestClear(PlayerInteractEvent e) {

        if (duels.getArenaManager().getArena(e.getPlayer()) != arena) {
            return;
        }

        if (e.getHand().equals(EquipmentSlot.HAND) && e.hasBlock()) {
            Player p = e.getPlayer();
            if (StorageBlockTool.isStorageBlock(e.getClickedBlock())) {
                if (emptyContainers.contains(e.getClickedBlock())) {
                    p.sendMessage(ChatColor.RED + " This has already been looted! :(");
                } else {
                    addPoint(arena.getTeam(p));
                    emptyContainers.add(e.getClickedBlock());
                    System.out.println(emptyContainers.get(0));
                }
            }
        }
    }
}