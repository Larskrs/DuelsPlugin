package net.larskrs.plugins.duels.placeholers;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.larskrs.plugins.duels.Duels;
import net.larskrs.plugins.duels.Files.PlayerDataFile;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class WinAmountPlaceholder extends PlaceholderExpansion {
    private final Duels plugin;

    public WinAmountPlaceholder(Duels plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getAuthor() {
        return "Larskrs";
    }

    @Override
    public String getIdentifier() {
        return "simpleduels";
    }

    @Override
    public String getVersion() {
        return "'${project.version}'";
    }

    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (params.equalsIgnoreCase("win_amount")) {
            return PlayerDataFile.getPlayerWins(player.getPlayer()) + "";
        }

        if (params.equalsIgnoreCase("placeholder1")) {
            return "Placeholder Text 1";
        }

        if (params.equalsIgnoreCase("placeholder2")) {
            return "Placeholder Text 2";
        }

        return null;
    }
}