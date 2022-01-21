package net.larskrs.plugins.duels;

import org.bukkit.scheduler.BukkitRunnable;

public class UpdateLoop extends BukkitRunnable {

    private Duels duels;

    public UpdateLoop(Duels duels) {
        this.duels = duels;
        this.runTaskTimer(duels, 0, 20);
    }
    public void stop() {
        this.cancel();
    }


    @Override
    public void run() {
        //for (Player p : duels.getA)
    }
}
