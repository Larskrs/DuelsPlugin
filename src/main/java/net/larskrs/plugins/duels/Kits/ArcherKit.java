package net.larskrs.plugins.duels.Kits;

import net.larskrs.plugins.duels.enums.KitType;
import net.larskrs.plugins.duels.instances.Kit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
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
        p.getInventory().setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
        p.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
        p.getInventory().setBoots(new ItemStack(Material.CHAINMAIL_BOOTS));

        ItemStack bow = new ItemStack(Material.BOW);
        bow.addEnchantment(Enchantment.ARROW_DAMAGE, 3);
        ItemStack crossbow = new ItemStack(Material.CROSSBOW);
        crossbow.addEnchantment(Enchantment.MULTISHOT, 1);
        p.getInventory().setItem(0, bow);
        p.getInventory().setItem(1, crossbow);
        p.getInventory().setItem(8, new ItemStack(Material.TIPPED_ARROW, 32));
        p.getInventory().setItem(7, new ItemStack(Material.SPECTRAL_ARROW, 12));

    }
}
