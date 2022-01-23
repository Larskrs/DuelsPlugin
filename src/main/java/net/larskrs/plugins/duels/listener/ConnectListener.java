package net.larskrs.plugins.duels.listener;

import net.larskrs.plugins.duels.Duels;
import net.larskrs.plugins.duels.instances.Arena;
import net.larskrs.plugins.duels.managers.ConfigManager;
import net.larskrs.plugins.duels.managers.NametagManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Scoreboard;

public class ConnectListener implements Listener {

    private Duels duels;

    public ConnectListener(Duels duels) {
        this.duels = duels;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        e.getPlayer().setScoreboard(board);

        e.getPlayer().teleport(ConfigManager.getLobbySpawnLocation());
        NametagManager.setNameTags(e.getPlayer());
        e.getPlayer().getInventory().clear();
    }
    @EventHandler
    public void onQuit (PlayerQuitEvent e) {
        Player p = e.getPlayer();
        Arena a = duels.getArenaManager().getArena(p);
        if (a != null) {
            a.removePlayer(p);
        }
        NametagManager.removeTag(p);
    }
}
