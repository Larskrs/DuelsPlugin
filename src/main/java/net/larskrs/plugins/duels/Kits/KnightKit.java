package net.larskrs.plugins.duels.Kits;

import net.larskrs.plugins.duels.enums.KitType;
import net.larskrs.plugins.duels.instances.Kit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class KnightKit extends Kit {
    public KnightKit(KitType type, UUID uuid) {
        super(KitType.KNIGHT, uuid);
    }

    @Override
    public void onStart(Player p) {
        p.getInventory().clear();
        p.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET));
        p.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
        p.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
        p.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));

        p.getInventory().setItem(0, new ItemStack(Material.IRON_SWORD));
    }
}
