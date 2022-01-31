package net.larskrs.plugins.duels.instances;

import com.google.common.collect.TreeMultimap;
import net.larskrs.plugins.duels.Duels;
import net.larskrs.plugins.duels.Files.KitsFile;
import net.larskrs.plugins.duels.Files.PlayerDataFile;
import net.larskrs.plugins.duels.Games.Deathmatch;
import net.larskrs.plugins.duels.Games.Fortress;
import net.larskrs.plugins.duels.Games.Game;
import net.larskrs.plugins.duels.Games.LastStanding;
import net.larskrs.plugins.duels.Kits.CountdownItems;
import net.larskrs.plugins.duels.enums.GameState;
import net.larskrs.plugins.duels.managers.ConfigManager;
import net.larskrs.plugins.duels.managers.NametagManager;
import net.larskrs.plugins.duels.managers.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.*;

public class Arena {


    private Duels duels;
    private int id;
    private Location spawn;

    private List<UUID> players;
    private HashMap<UUID, CustomKit> kits;
    private HashMap<UUID, Team> teams;
    private GameState state;
    private Countdown countdown;
    private Game game;
    private Scoreboard scoreboard;
    private ArenaOptions options;
    public String name;
    

    public Arena(Duels duels, int id, Location spawn) {
        this.duels = duels;
        this.id = id;
        this.spawn = spawn;
        ScoreboardManager sm = Bukkit.getScoreboardManager();
        scoreboard = sm.getNewScoreboard();
        setState(GameState.RECRUITING);
        this.players = new ArrayList<>();
        this.teams = new HashMap<>();
        this.countdown = new Countdown(duels, this);
        this.kits = new HashMap<>();
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "[ID: " + id +  "] " + ConfigManager.getGameType(id) + ChatColor.RED + "" + (ConfigManager.getGameType(id).contains("DEATHMATCH")));
        if (ConfigManager.getGameType(id).contains("DEATHMATCH")) {
            this.game = new Deathmatch(duels, this);
        } else if (ConfigManager.getGameType(id).contains("LASTSTANDING")) {
            this.game = new LastStanding(duels, this);
        } else if (ConfigManager.getGameType(id).contains("FORTRESS")) {
            this.game = new Fortress(duels, this);
        }
        this.name = ConfigManager.getArenaName(id);
        

    }

    private ArenaOptions loadOptions() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "loading... arena-options");
        ArenaOptions o = new ArenaOptions(this, duels.getConfig().getString("arenas." + getId() + ".options.game-type"), duels.getConfig().getInt("arenas." + getId() + ".options.points-to-win"), false);
        o.type = duels.getConfig().getString("arenas." + getId() + ".options.game-type");
        o.winAmount = duels.getConfig().getInt("arenas." + getId() + ".options.points-to-win");
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "arena info: " + o.type + " " + o.winAmount);

        return o;
    }
    public ArenaOptions getOptions () {return options;}
    public List<UUID> getPlayers() { return players; }

    public GameState getState () {return state;}
    public int getId() {
        return id;
    }
    public Game getGame () {return game; }

    /* Game */

        public void start() {
            game.start();
        }
        public void reset(boolean kickPlayers) {

            setState(GameState.RECRUITING);
            if (kickPlayers) {
                Location loc = ConfigManager.getLobbySpawnLocation();
                for (UUID uuid : players) {
                    Player p = Bukkit.getPlayer(uuid);
                    p.getInventory().clear();
                    p.teleport(loc);
                    removeKit(p.getUniqueId());
                    p.setHealth(p.getMaxHealth());
                    p.setFoodLevel(20);
                    p.setArrowsInBody(0);
                    p.setFireTicks(0);
                }
                players.clear();
                teams.clear();
                kits.clear();
            }
            
            sendTitle("", ""); //Resets the title to instantly hide.
            countdown.cancel();
            countdown = new Countdown(duels, this);
            game.unregister();

            if (ConfigManager.getGameType(id).contains("DEATHMATCH")) {
                this.game = new Deathmatch(duels, this);
            } else if (ConfigManager.getGameType(id).contains("LASTSTANDING")) {
                this.game = new LastStanding(duels, this);
            } else if (ConfigManager.getGameType(id).contains("FORTRESS")) {
                this.game = new Fortress(duels, this);
            }
        }

    /* Tools */

    public void sendMessage(String message) {
        for (UUID uuid : players) {
            Bukkit.getPlayer(uuid).sendMessage(message);
        }
    }

    public void sendTitle(String title, String subtitle) {
        for (UUID uuid : players) {
            Bukkit.getPlayer(uuid).sendTitle(title, subtitle);
        }
    }


/* Player */
    public void addPlayer(Player player) {
        CustomKit lastSavedKit = PlayerDataFile.getLastSavedKit(player.getUniqueId());
        player.sendMessage("default kit has been equipped.");
        if (lastSavedKit == null) {
            Random rand = new Random();
            lastSavedKit = KitsFile.getKits().get(rand.nextInt(KitsFile.getKits().size()));
            player.sendMessage("Random Kit Has Been Equipped!");
        }
            setKit(player.getUniqueId(), lastSavedKit);


        players.add(player.getUniqueId());
        player.teleport(this.spawn);
        player.setHealth(player.getMaxHealth());
        player.getInventory().clear();
        player.setFoodLevel(20);
        player.setGameMode(GameMode.SURVIVAL);
        player.setFireTicks(0);

        TreeMultimap<Integer, Team> count = TreeMultimap.create();
        for (Team t : Team.values()) {
            count.put(getTeamCount(t), t);
        }

        Team lowest = (Team) count.values().toArray()[0];
        setTeam(player, lowest);


        //if (state == GameState.RECRUITING && players.size() >= ConfigManager.getRequiredPlayers() ) {
        if (players.size() >= ConfigManager.getRequiredPlayers() && state.equals(GameState.RECRUITING)) {
            countdown.start();
        }

        CountdownItems.onStart(player);

    }
    public void removePlayer(Player player) {


        players.remove(player.getUniqueId());
        player.teleport(ConfigManager.getLobbySpawnLocation());
        player.setHealth(player.getMaxHealth());
        removeTeam(player);
        player.closeInventory();
        player.sendTitle("", ""); //Resets the title to instantly hide.
        player.getInventory().clear();
        player.setFoodLevel(20);
        player.setGameMode(GameMode.SURVIVAL);
        NametagManager.removeTag(player);
        player.getActivePotionEffects().clear();

        if (state == GameState.COUNTDOWN && players.size() < ConfigManager.getRequiredPlayers()) {
            sendMessage(ChatColor.RED + "Not enough players for game to start. :(");
            this.reset(false);
        }
        if (state == GameState.LIVE && players.size() < ConfigManager.getRequiredPlayers()) {
            sendMessage(ChatColor.RED + "The game has ended as too many players have left. :(");
            this.reset(true);
            return;
        }
    }
    public void setState (GameState state) {
        this.state = state;
            // Update Arena Sign
    }
    public void setTeam (Player player, Team team) {
        removeTeam(player);
        teams.put(player.getUniqueId(), team);
        NametagManager.newTag(player, team);


        player.sendMessage(ChatColor.YELLOW + "You have been placed on the " + team.getDisplay() + ChatColor.YELLOW + " team!");
    }
    public void removeTeam(Player player) {
        if (teams.containsKey(player.getUniqueId())) {
            teams.remove(player.getUniqueId());
        }
        NametagManager.removeTag(player);
    }
    public int getTeamCount (Team team) {
        int amount = 0;
        for (Team t : teams.values()) {
            if (t == team) {
                amount ++;
            }
        }
        return amount;
    }
    public Team getTeam(Player p) {
        return teams.get(p.getUniqueId());
    }
    public void setKit(UUID uuid, CustomKit type) {
        removeKit(uuid);

        PlayerDataFile.getConfig().set(uuid + ".kit", type.getName());
        PlayerDataFile.saveFile();
        kits.put(uuid, type);
    }
    public void removeKit(UUID uuid) {
        if (kits.containsKey(uuid)) {
            kits.get(uuid).remove();
            kits.remove(uuid);
        }
    }
    public HashMap<UUID, CustomKit> getKits() {
        return kits;
    }
    public CustomKit getKit(Player player) {
        return kits.get(player.getUniqueId());
    }
    public void respawnPlayer(UUID uuid) {

        if (ConfigManager.getGameType(id).equals("LASTSTANDING") && this.state == GameState.LIVE) {
            return;
        }

        Player p = Bukkit.getPlayer(uuid);
        Arena a = duels.getArenaManager().getArena(p);

        p.teleport(ConfigManager.getTeamSpawn(duels.getArenaManager().getArena(p).getId(), a.getTeam(p)));
        a.getKits().get(p.getUniqueId()).giveKit(p);
        p.setFoodLevel(20);
        p.setHealth(p.getMaxHealth());
        p.setGameMode(GameMode.SURVIVAL);
    }

    public String getName () {return ConfigManager.getArenaName(id);}
    public void endGame() {
        game.endGame();
        }
}
