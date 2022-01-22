package net.larskrs.plugins.duels.Kits;

import net.larskrs.plugins.duels.Duels;
import net.larskrs.plugins.duels.enums.KitType;
import net.larskrs.plugins.duels.instances.Kit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.UUID;

public class PearlerKit extends Kit {
    public PearlerKit(KitType type, UUID uuid) {
        super(KitType.PEARLER, uuid);
    }

    @Override
    public void onStart(Player p) {
        p.getInventory().clear();
        ItemStack lhelm = new ItemStack(Material.LEATHER_HELMET, 1);
        LeatherArmorMeta lhe = (LeatherArmorMeta)lhelm.getItemMeta();
        lhe.setColor(Duels.getInstance().getArenaManager().getArena(p).getTeam(p).getColor());
        lhelm.setItemMeta(lhe);
        p.getEquipment().setHelmet(lhelm);

        p.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
        p.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
        ItemStack lboots = new ItemStack(Material.LEATHER_BOOTS, 1);
        LeatherArmorMeta lbo = (LeatherArmorMeta)lboots.getItemMeta();
        lbo.setColor(Duels.getInstance().getArenaManager().getArena(p).getTeam(p).getColor());
        lboots.setItemMeta(lbo);
        p.getEquipment().setBoots(lboots);

        p.getInventory().setItem(0, new ItemStack(Material.STONE_SWORD));
        p.getInventory().setItem(1, new ItemStack(Material.ENDER_PEARL, 4));

    }
}
