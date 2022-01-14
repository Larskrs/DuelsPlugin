package net.larskrs.plugins.duels.GUI;

import net.larskrs.plugins.duels.Duels;
import net.larskrs.plugins.duels.instances.Arena;
import net.larskrs.plugins.duels.managers.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class TeamGUI {

    public TeamGUI (Arena a, Player p) {
        Inventory gui = Bukkit.createInventory(null, 9, ChatColor.DARK_GRAY + "Team Selection");

        for (Team team : Team.values()) {
            ItemStack is = new ItemStack(team.getIcon());
            ItemMeta isMeta = is.getItemMeta();
            isMeta.setDisplayName(team.getDisplay() + ChatColor.YELLOW + " (" + ChatColor.AQUA + a.getTeamCount(team)  + ChatColor.YELLOW + ")");

            //List<String> islore = isMeta.getLore();
            //islore.add(team.getDescription());
            //islore.add(ChatColor.YELLOW + "Players: " + a.getTeamCount(team));
            isMeta.setLocalizedName(team.name());
            //isMeta.setLore(islore);
            is.setItemMeta(isMeta);
            gui.addItem(is);
        }

        p.openInventory(gui);
    }



}
