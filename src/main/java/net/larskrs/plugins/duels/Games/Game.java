package net.larskrs.plugins.duels.Games;

import com.sun.org.apache.xpath.internal.operations.Bool;
import jdk.internal.icu.impl.BMPSet;
import net.larskrs.plugins.duels.Duels;
import net.larskrs.plugins.duels.enums.GameState;
import net.larskrs.plugins.duels.instances.Arena;
import net.larskrs.plugins.duels.instances.LiveGameTimer;
import net.larskrs.plugins.duels.managers.ConfigManager;
import net.larskrs.plugins.duels.managers.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public abstract class Game implements Listener {

    protected Arena arena;
    protected Boolean hasEnded;
    protected LiveGameTimer liveGameTimer;
    protected List<Block> placed;


    public Game(Duels duels, Arena arena) {
        this.arena = arena;
        this.hasEnded = false;
        this.placed = new ArrayList<>();
        Bukkit.getPluginManager().registerEvents(this, duels);
    }

    public void start() {


        for (UUID uuid : arena.getPlayers()) {
            Player p = Bukkit.getPlayer(uuid);
            arena.getKit(p).giveKit(p);
            p.closeInventory();
            p.teleport(ConfigManager.getTeamSpawn(arena.getId(), arena.getTeam(p)));
            this.liveGameTimer = new LiveGameTimer(Duels.getInstance(), arena,240);
        }



        onStart();
    }
    public abstract void onNewRoundBegin();
    public abstract void onStart();
    public abstract void endGame();

    public void unregister() {
        HandlerList.unregisterAll(this);
    }
    public abstract void onCustomRespawn(Player hurt, Player killer);
    public abstract void addPoint(Team team);


    public abstract void onScoreboardUpdate();

    public void clearPlacedBlocks() {
        for(Block b : placed) {
            b.setType(Material.AIR);
        }
    }
    public void removeBlock(Block b) {
        if (placed.contains(b)) {
            placed.remove(b);
        }
    }

    public void addBlock(Block b) {
        if (!placed.contains(b)) {
            placed.add(b);
        }
    }
    public Boolean isBreakable (Block b) {
        return placed.contains(b);
    }
}
