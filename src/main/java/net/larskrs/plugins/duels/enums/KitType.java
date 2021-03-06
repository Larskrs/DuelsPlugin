package net.larskrs.plugins.duels.enums;

import net.larskrs.plugins.duels.Games.Game;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public enum KitType {


    KNIGHT(ChatColor.YELLOW + "Knight", Material.IRON_SWORD, ChatColor.GRAY + "For those of you that stick to others."),
    ARCHER(ChatColor.YELLOW + "Archer", Material.CROSSBOW, ChatColor.GRAY + "For those of you that keep your eyes close. \n " + ChatColor.YELLOW + "Credit: tnrtt"),
    PEARLER(ChatColor.YELLOW + "Pearler", Material.ENDER_PEARL, ChatColor.GRAY + "For those that like the exit more than the fight."),
    PRANKSTER(ChatColor.YELLOW + "Prankster", Material.TNT, ChatColor.GRAY + "For those that play the card of the party bomb.");

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
