package net.larskrs.plugins.duels.instances;

import com.google.common.collect.TreeMultimap;
import net.larskrs.plugins.duels.Duels;
import net.larskrs.plugins.duels.Files.PlayerDataFile;
import net.larskrs.plugins.duels.Games.Deathmatch;
import net.larskrs.plugins.duels.Games.Game;
import net.larskrs.plugins.duels.Games.LastStanding;
import net.larskrs.plugins.duels.Kits.ArcherKit;
import net.larskrs.plugins.duels.Kits.KnightKit;
import net.larskrs.plugins.duels.Kits.PearlerKit;
import net.larskrs.plugins.duels.Kits.PranksterKit;
import net.larskrs.plugins.duels.enums.GameState;
import net.larskrs.plugins.duels.enums.KitType;
import net.larskrs.plugins.duels.listener.RespawnCountdown;
import net.larskrs.plugins.duels.managers.ConfigManager;
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
    private Location spawn, sign;

    private List<UUID> players;
    private HashMap<UUID, Kit> kits;
    private HashMap<UUID, Team> teams;
    private GameState state;
    private Countdown countdown;
    private Game game;
    private Scoreboard scoreboard;
    private ArenaOptions options;

    public Arena(Duels duels, int id, Location spawn) {
        this.duels = duels;
        this.id = id;
        this.spawn = spawn;
        this.sign = ConfigManager.getLocation("arenas." + id + ".sign");
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
        }
    }

    private ArenaOptions loadOptions() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "loading... arena-options");
        ArenaOptions o = new ArenaOptions();
        o.GameType = duels.getConfig().getString("arenas." + getId() + ".options.game-type");
        o.winAmount = duels.getConfig().getInt("arenas." + getId() + ".options.points-to-win");
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "arena info: " + o.GameType + " " + o.winAmount);

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
            }
            kits.clear();
            sendTitle("", ""); //Resets the title to instantly hide.
            countdown.cancel();
            countdown = new Countdown(duels, this);
            game.unregister();

            if (ConfigManager.getGameType(id).contains("DEATHMATCH")) {
                this.game = new Deathmatch(duels, this);
            } else if (ConfigManager.getGameType(id).contains("LASTSTANDING")) {
                this.game = new LastStanding(duels, this);
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
    public void updateSign(String line1, String line2, String line3, String line4) {
        Sign signBlock = (Sign) sign.getBlock().getState();
        signBlock.setLine(0, line1);
        signBlock.setLine(1, line2);
        signBlock.setLine(2, line3);
        signBlock.setLine(3, line4);
        signBlock.update();
    }

/* Player */
    public void addPlayer(Player player) {
        KitType kitType = PlayerDataFile.getLastSavedKit(player.getUniqueId());
        if (kitType == null) { kitType = KitType.KNIGHT; }
        setKit(player.getUniqueId(), kitType);


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

        updateSign(ChatColor.GOLD + "Arena " + id, "", state.name(),  "Players: " + players.size());

        //if (state == GameState.RECRUITING && players.size() >= ConfigManager.getRequiredPlayers() ) {
        if (state == GameState.RECRUITING && players.size() % 2 == 0 || players.size() >= ConfigManager.getRequiredPlayers()) {
            countdown.start();
        }
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

        if (state == GameState.COUNTDOWN && players.size() < ConfigManager.getRequiredPlayers()) {
            sendMessage(ChatColor.RED + "Not enough players for game to start. :(");
            this.reset(false);
        }
        if (state == GameState.LIVE && players.size() < ConfigManager.getRequiredPlayers()) {
            sendMessage(ChatColor.RED + "The game has ended as too many players have left. :(");
            this.reset(true);
            return;
        }
        updateSign(ChatColor.GOLD + "Arena " + id, "", state.name(), state == GameState.LIVE ? "Players: " + players.size() : "");
    }
    public void setState (GameState state) {
        this.state = state;
            // Update Arena Sign
        updateSign(ChatColor.GOLD + "Arena " + id, "", state.name(), state == GameState.LIVE ? "Players: " + players.size() : "");
    }
    public void setTeam (Player player, Team team) {
        removeTeam(player);
        teams.put(player.getUniqueId(), team);


        player.sendMessage(ChatColor.YELLOW + "You have been placed on the " + team.getDisplay() + ChatColor.YELLOW + " team!");
    }
    public void removeTeam(Player player) {
        if (teams.containsKey(player.getUniqueId())) {
            teams.remove(player.getUniqueId());
        }
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
    public void setKit(UUID uuid, KitType type) {
        removeKit(uuid);
        if (type == KitType.KNIGHT) {
            kits.put(uuid, new KnightKit(type, uuid));
        } else if (type == KitType.ARCHER){
            kits.put(uuid, new ArcherKit(type, uuid));
        } else if (type == KitType.PEARLER) {
            kits.put(uuid, new PearlerKit(type, uuid));
        } else if (type == KitType.PRANKSTER) {
            kits.put(uuid, new PranksterKit(type, uuid));
        }

        PlayerDataFile.getConfig().set(Bukkit.getPlayer(uuid).getName() + ".kit", type.name());
        PlayerDataFile.saveFile();
    }
    public void removeKit(UUID uuid) {
        if (kits.containsKey(uuid)) {
            kits.get(uuid).remove();
            kits.remove(uuid);
        }
    }
    public HashMap<UUID, Kit> getKits() {
        return kits;
    }
    public KitType getKit(Player player) {
        return kits.containsKey(player.getUniqueId()) ? kits.get(player.getUniqueId()).getType() : null;
    }
    public void respawnPlayer(UUID uuid) {

        Player p = Bukkit.getPlayer(uuid);
        Arena a = duels.getArenaManager().getArena(p);

        p.teleport(ConfigManager.getTeamSpawn(duels.getArenaManager().getArena(p).getId(), a.getTeam(p)));
        a.getKits().get(p.getUniqueId()).onStart(p);
        p.setFoodLevel(20);
        p.setHealth(p.getMaxHealth());
        p.setGameMode(GameMode.SURVIVAL);
    }

    public Location getSignLocation() {
        return sign;
    }
}
