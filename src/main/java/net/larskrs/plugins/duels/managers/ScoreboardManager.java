package net.larskrs.plugins.duels.managers;

import dev.jcsoftware.jscoreboards.JPerPlayerScoreboard;
import dev.jcsoftware.jscoreboards.JScoreboardOptions;
import dev.jcsoftware.jscoreboards.JScoreboardTabHealthStyle;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ScoreboardManager {

    public static void setHubScoreboard(Player p) {
        JPerPlayerScoreboard scoreboard = new JPerPlayerScoreboard(
                (player) -> {
                    return "&e&lDUELS";
                },
                (player) -> {
                    return Arrays.asList(
                            ""
                    );
                }

        );
        scoreboard.addPlayer(p);
        scoreboard.updateScoreboard();
    }

}
