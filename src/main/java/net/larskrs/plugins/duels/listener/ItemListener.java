package net.larskrs.plugins.duels.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ItemListener implements Listener {

    @EventHandler
    public void onItemInteract(PlayerInteractEvent e) {

        if ((e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {


        if (e.hasItem() && e.getItem().getItemMeta().getDisplayName().equals(ChatColor.RED.toString() + ChatColor.BOLD + "LEAVE")) {
            Bukkit.dispatchCommand(e.getPlayer(), "duel leave");
        }
        }
    }



}
