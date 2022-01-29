package net.larskrs.plugins.duels.Kits;

import net.larskrs.plugins.duels.Duels;
import net.larskrs.plugins.duels.enums.KitType;
import net.larskrs.plugins.duels.instances.Kit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.UUID;

public class CountdownItems{

    public static void onStart(Player p) {
        p.getInventory().clear();
        ItemStack leaveItem = new ItemStack(Material.RED_BED, 1);
        ItemMeta leaveItemMeta = leaveItem.getItemMeta();
        leaveItemMeta.setDisplayName(ChatColor.RED + "Leave");
        leaveItem.setItemMeta(leaveItemMeta);
        p.getInventory().setItem(8, leaveItem);

    }
}
