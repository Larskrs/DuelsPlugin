package net.larskrs.plugins.duels.enums;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public enum KitType {

    KNIGHT(ChatColor.YELLOW + "Knight", Material.IRON_SWORD, ChatColor.DARK_GRAY + "For those of you that stick to others."),
    ARCHER(ChatColor.YELLOW + "Archer", Material.BOW, ChatColor.DARK_GRAY + "For those of you that keep your eyes close");

    private String display;
    private Material icon;
    private String description;

    KitType(String display, Material icon, String description) {
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
