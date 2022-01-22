package net.larskrs.plugins.duels.Kits;

import net.larskrs.plugins.duels.Duels;
import net.larskrs.plugins.duels.enums.KitType;
import net.larskrs.plugins.duels.instances.Kit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.UUID;

public class KnightKit extends Kit {
    public KnightKit(KitType type, UUID uuid) {
        super(KitType.KNIGHT, uuid);
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
        p.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));

        p.getInventory().setItem(0, new ItemStack(Material.IRON_SWORD));
    }
}
