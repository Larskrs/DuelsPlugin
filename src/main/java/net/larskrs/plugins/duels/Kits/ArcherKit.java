package net.larskrs.plugins.duels.Kits;

import net.larskrs.plugins.duels.enums.KitType;
import net.larskrs.plugins.duels.instances.Kit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class ArcherKit extends Kit {
    public ArcherKit(KitType type, UUID uuid) {
        super(KitType.ARCHER, uuid);
    }

    @Override
    public void onStart(Player p) {
        p.getInventory().setHelmet(new ItemStack(Material.LEATHER_HELMET));
        p.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
        p.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
        p.getInventory().setBoots(new ItemStack(Material.LEATHER_BOOTS));

        p.getInventory().setItem(0, new ItemStack(Material.STONE_SWORD));
        p.getInventory().setItem(1, new ItemStack(Material.BOW));
        p.getInventory().setItem(8, new ItemStack(Material.ARROW, 20));

    }
}
