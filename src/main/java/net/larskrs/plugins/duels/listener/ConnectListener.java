package net.larskrs.plugins.duels.listener;

import net.larskrs.plugins.duels.Duels;
import net.larskrs.plugins.duels.instances.Arena;
import net.larskrs.plugins.duels.managers.ConfigManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectListener implements Listener {

    private Duels duels;

    public ConnectListener(Duels duels) {
        this.duels = duels;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        e.getPlayer().teleport(ConfigManager.getLobbySpawnLocation());
    }
    @EventHandler
    public void onQuit (PlayerQuitEvent e) {
        Player p = e.getPlayer();
        Arena a = duels.getArenaManager().getArena(p);
        if (a != null) {
            a.removePlayer(p);
        }
    }
}
