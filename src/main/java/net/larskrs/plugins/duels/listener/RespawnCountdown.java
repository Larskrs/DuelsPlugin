package net.larskrs.plugins.duels.listener;

import net.larskrs.plugins.duels.Duels;
import net.larskrs.plugins.duels.enums.GameState;
import net.larskrs.plugins.duels.instances.Arena;
import net.larskrs.plugins.duels.managers.ConfigManager;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class RespawnCountdown extends BukkitRunnable {

    private Duels duels;
    private Arena arena;
    private Player player;
    private int startupTime;

    public RespawnCountdown(Duels duels, Player p, int lenght) {

        this.duels = duels;
        this.player = p;
        this.arena = duels.getArenaManager().getArena(p);
        this.startupTime = lenght;

    }

    public void start () {
        runTaskTimer(duels, 0, 20);

    }

    @Override
    public void run() {
        if (arena.getState() != null || !arena.getState().equals(GameState.RECRUITING)) {
            if (startupTime == 0) {
                arena.respawnPlayer(player.getUniqueId());
                this.cancel();
            }
        } else {

                player.teleport(ConfigManager.getLobbySpawnLocation());
                player.setGameMode(GameMode.SURVIVAL);
                player.setHealth(player.getMaxHealth());
                player.closeInventory();
                player.sendTitle("", ""); //Resets the title to instantly hide.
                player.getInventory().clear();
                player.setFoodLevel(20);
                this.cancel();
                return;

        }
        player.sendTitle(ChatColor.RED + "" + startupTime, ChatColor.GRAY + "respawning...", 0, 20, 10);
        startupTime--;
    }
}
