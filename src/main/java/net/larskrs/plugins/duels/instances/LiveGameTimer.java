package net.larskrs.plugins.duels.instances;

import net.larskrs.plugins.duels.Duels;
import net.larskrs.plugins.duels.enums.GameState;
import net.larskrs.plugins.duels.managers.ConfigManager;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class LiveGameTimer extends BukkitRunnable {

    private Duels duels;
    private Arena arena;
    private int gameTime;

    public LiveGameTimer (Duels duels, Arena arena) {
        this.duels = duels;
        this.arena = arena;
        this.gameTime = ConfigManager.getStartupTime();
        
    }

    public void start () {
        

        runTaskTimer(duels, 0, 20);

    }

    @Override
    public void run() {
        if (gameTime == 10) {
            
            arena.getGame().endGame();
        } else if (gameTime <= 0) {
          arena.reset(true);
        }   
        gameTime--;
    }
}
