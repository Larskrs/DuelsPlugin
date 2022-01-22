package net.larskrs.plugins.duels.managers;

import net.larskrs.plugins.duels.Duels;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.entity.Player;

public class NametagManager {

    public static void setNameTags(Player player) {

        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());

        for (Team teamA : Team.values()) {
            org.bukkit.scoreboard.Team teamB = player.getScoreboard().registerNewTeam(teamA.name());
            teamB.setPrefix(teamA.getDisplay());
            teamB.setDisplayName(teamA.getDisplay());
        }

    }
    public static void newTag(Player player, Team team) {
        for (Player target : Bukkit.getOnlinePlayers()) {
            target.getScoreboard().getTeam(team.name()).addEntry(player.getName());
        }
    }
    public static void removeTag(Player player) {
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (target.getScoreboard().getEntryTeam(player.getName()) != null) {
            target.getScoreboard().getEntryTeam(player.getName()).removeEntry(player.getName());
            }
        }
    }


}
