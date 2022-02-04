package net.larskrs.plugins.duels.managers;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;

public enum Team {

    RED(ChatColor.RED + "[RED]", Material.RED_WOOL, ChatColor.RED + "Red like roses.", Color.RED, ChatColor.RED),
    BLUE(ChatColor.BLUE + "[BLUE]", Material.BLUE_WOOL, ChatColor.BLUE + "Blue like blue flowers.", Color.BLUE, ChatColor.BLUE);

    private String display;
    private Material icon;
    private String description;
    private Color color;
    private ChatColor chatColor;

    Team(String display, Material icon, String description, Color color, ChatColor chatColor) {
        this.display = display;
        this.icon = icon;
        this.description = description;
        this.color = color;
        this.chatColor = chatColor;
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

    public Color getColor() {
        return color;
    }
    public ChatColor getChatColor () {return chatColor; }
}
