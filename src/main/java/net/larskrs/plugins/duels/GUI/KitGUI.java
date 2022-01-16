package net.larskrs.plugins.duels.GUI;

import net.larskrs.plugins.duels.Files.PlayerDataFile;
import net.larskrs.plugins.duels.enums.KitType;
import net.larskrs.plugins.duels.instances.Arena;
import net.larskrs.plugins.duels.managers.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class KitGUI {
    public KitGUI (Arena a, Player p) {
        Inventory gui = Bukkit.createInventory(null, 9, ChatColor.DARK_GRAY + "Kit Selection");

        for (KitType type : KitType.values()) {
            ItemStack is = new ItemStack(type.getIcon());
            ItemMeta isMeta = is.getItemMeta();
            isMeta.setDisplayName(type.getDisplay());

            ArrayList<String> islore = new ArrayList<>();
            islore.add(type.getDescription());
            if (PlayerDataFile.getLastSavedKit(p.getUniqueId()) != null) {
                if (PlayerDataFile.getLastSavedKit(p.getUniqueId()) == type) {
                    isMeta.addEnchant(Enchantment.DURABILITY, 1, true);
                    islore.add(ChatColor.YELLOW + "(Equipped)");
                }
            }
            isMeta.setLocalizedName(type.name());
            isMeta.setLore(islore);
            is.setItemMeta(isMeta);
            gui.addItem(is);
        }

        p.openInventory(gui);
    }
}
