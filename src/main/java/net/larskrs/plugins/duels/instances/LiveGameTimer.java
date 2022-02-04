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
    private String output;

    public LiveGameTimer (Duels duels, Arena arena, int gameTime) {
        this.duels = duels;
        this.arena = arena;
        this.gameTime = gameTime;

    }

    public void start () {

        runTaskTimer(duels, 0, 20);

    }

    @Override
    public void run() {
        if (gameTime == 10) {
            
            arena.getGame().endGame();
        } else if (gameTime <= 0) {
          this.cancel();
            arena.reset(true);
        return;
        }

        int i = gameTime;

        int hours = i / 3600;
        int minutes = (i % 3600) / 60;
        int seconds = i % 60;

        output = String.format("%02dm%02ds", minutes, seconds);
        gameTime--;
        arena.getGame().onScoreboardUpdate();
    }

    public int getGameTime () {return gameTime;}

    public void endGameTime() {

        this.gameTime = 9;
        arena.getGame().onScoreboardUpdate();
    }

    public String getOutput() {
        return output;
    }
}
