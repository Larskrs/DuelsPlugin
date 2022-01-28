package net.larskrs.plugins.duels.GUI;

import net.larskrs.plugins.duels.Duels;
import net.larskrs.plugins.duels.instances.CustomKit;
import net.larskrs.plugins.duels.Files.KitsFile;
import net.larskrs.plugins.duels.Files.PlayerDataFile;
import net.larskrs.plugins.duels.instances.Arena;
import net.larskrs.plugins.duels.managers.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;

public class ArenaGUI {
    public ArenaGUI (Player p) {
        Inventory gui = Bukkit.createInventory(null, 9, ChatColor.DARK_GRAY + "Kit Selection");

        for (Arena a : Duels.getInstance().getArenaManager().getArenas()) {
            ItemStack is = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
            ItemMeta isMeta = is.getItemMeta();
            isMeta.setDisplayName(ConfigManager.getArenaName(a.getId()));

            ArrayList<String> islore = new ArrayList<>();

            isMeta.setLocalizedName(a.getId() + "");
            isMeta.setLore(islore);
            is.setItemMeta(isMeta);
            gui.addItem(is);
        }

        p.openInventory(gui);
    }
}
