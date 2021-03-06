package net.larskrs.plugins.duels.Kits;

import net.larskrs.plugins.duels.Duels;
import net.larskrs.plugins.duels.enums.KitType;
import net.larskrs.plugins.duels.instances.Kit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.UUID;

public class ArcherKit extends Kit {
    public ArcherKit(KitType type, UUID uuid) {
        super(KitType.ARCHER, uuid);
    }

    @Override
    public void onStart(Player p) {
        p.getInventory().clear();
        ItemStack lhelm = new ItemStack(Material.LEATHER_HELMET, 1);
        LeatherArmorMeta lhe = (LeatherArmorMeta)lhelm.getItemMeta();
        lhe.setColor(Duels.getInstance().getArenaManager().getArena(p).getTeam(p).getColor());
        lhelm.setItemMeta(lhe);
        p.getEquipment().setHelmet(lhelm);

        p.getInventory().setChestplate(new ItemStack(Material.NETHERITE_CHESTPLATE));
        p.getInventory().setLeggings(new ItemStack(Material.NETHERITE_LEGGINGS));
        ItemStack lboots = new ItemStack(Material.LEATHER_BOOTS, 1);
        LeatherArmorMeta lbo = (LeatherArmorMeta)lboots.getItemMeta();
        lbo.setColor(Duels.getInstance().getArenaManager().getArena(p).getTeam(p).getColor());
        lboots.setItemMeta(lbo);
        p.getEquipment().setBoots(lboots);

        p.getInventory().setItem(0, new ItemStack(Material.STONE_SWORD));
        p.getInventory().setItem(4, new ItemStack(Material.COOKED_BEEF, 4));
        p.getInventory().setItem(1, new ItemStack(Material.BOW));
        p.getInventory().setItem(8, new ItemStack(Material.ARROW, 20));

    }
}