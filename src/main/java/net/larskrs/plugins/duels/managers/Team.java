package net.larskrs.plugins.duels.managers;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public enum Team {

    RED(ChatColor.RED + "[RED]", Material.RED_WOOL, ChatColor.RED + "Red like roses."),
    BLUE(ChatColor.BLUE + "[BLUE]", Material.BLUE_WOOL, ChatColor.BLUE + "Blue like blue flowers.");

    private String display;
    private Material icon;
    private String description;

    Team(String display, Material icon, String description) {
        this.display = display;
        this.icon = icon;
        this.description = description;
    }

    public String getDisplay() {
        return display;
    }

    public Material getIcon() {
        return icon;
    }

    public String getDescription() {
        return description;
    }
}
