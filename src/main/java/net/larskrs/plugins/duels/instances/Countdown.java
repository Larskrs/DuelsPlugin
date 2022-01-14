package net.larskrs.plugins.duels.instances;

import net.larskrs.plugins.duels.Duels;
import net.larskrs.plugins.duels.enums.GameState;
import net.larskrs.plugins.duels.managers.ConfigManager;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class Countdown extends BukkitRunnable {

    private Duels duels;
    private Arena arena;
    private int startupTime;

    public Countdown (Duels duels, Arena arena) {
        this.duels = duels;
        this.arena = arena;
        this.startupTime = ConfigManager.getStartupTime();
    }

    public void start () {
        arena.setState(GameState.COUNTDOWN);

        runTaskTimer(duels, 0, 20);

    }

    @Override
    public void run() {
        if (startupTime == 0) {
            cancel();
            arena.start();
            return; // Stop the countdown from going any lower.
        }
        if (startupTime == 1)
            arena.sendMessage(ChatColor.YELLOW + "The game starts in " + ChatColor.GOLD + startupTime + ChatColor.YELLOW + " second!");
        else if (startupTime <= 5)
            arena.sendMessage(ChatColor.YELLOW + "The game starts in " + ChatColor.RED + startupTime + ChatColor.YELLOW + " seconds!");
        else if (startupTime == 10)
            arena.sendMessage(ChatColor.YELLOW + "The game starts in " + startupTime + " seconds!");
        else if (startupTime % 10 == 0) {
            arena.sendMessage(ChatColor.YELLOW + "The game starts in " + ChatColor.GOLD + startupTime + ChatColor.YELLOW + " seconds!");
        }
        if (startupTime % 10 == 0) {
            arena.sendTitle(ChatColor.GREEN.toString() + startupTime + " second" + (startupTime == 1 ? "" : "s"), ChatColor.GRAY + "Until game starts");
        }
        startupTime--;
    }
}
