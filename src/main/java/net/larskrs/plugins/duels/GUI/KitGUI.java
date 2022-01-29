package net.larskrs.plugins.duels.GUI;

import net.larskrs.plugins.duels.instances.CustomKit;
import net.larskrs.plugins.duels.Files.KitsFile;
import net.larskrs.plugins.duels.Files.PlayerDataFile;
import net.larskrs.plugins.duels.instances.Arena;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;

public class KitGUI {
    public KitGUI (Arena a, Player p) {
        Inventory gui = Bukkit.createInventory(null, 45, ChatColor.DARK_GRAY + "Kit Selection");

        for (CustomKit kit : KitsFile.getKits()) {
            ItemStack is = new ItemStack(kit.getIcon());
            ItemMeta isMeta = is.getItemMeta();
            isMeta.setDisplayName(kit.getDisplay());

            ArrayList<String> islore = new ArrayList<>();

            if (PlayerDataFile.getLastSavedKit(p.getUniqueId()) != null) {
                if (PlayerDataFile.getLastSavedKit(p.getUniqueId()).getName().equalsIgnoreCase(kit.getName())) {
                    isMeta.addEnchant(Enchantment.DURABILITY, 1, true);
                   isMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
                   islore.add(ChatColor.YELLOW + "(Equipped)");
                }
            }
            islore.addAll(Arrays.asList(kit.getDescription())); // Adds all lore from description.
            isMeta.setLocalizedName(kit.getName());
            isMeta.setLore(islore);
            is.setItemMeta(isMeta);
            gui.addItem(is);
        }

        p.openInventory(gui);
    }
}
