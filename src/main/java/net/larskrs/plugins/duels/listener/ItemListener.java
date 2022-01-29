package net.larskrs.plugins.duels.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryInteractEvent;

public class ItemListener implements Listener {

    @EventHandler
    public void onItemInteract(InventoryInteractEvent e) {
        if (e.getWhoClicked().getItemInHand() != null && e.getWhoClicked().getItemInHand().getItemMeta().getDisplayName() == ChatColor.YELLOW + "LEAVE") {
            Bukkit.dispatchCommand(e.getWhoClicked(), "duel leave");
        }
    }

}
