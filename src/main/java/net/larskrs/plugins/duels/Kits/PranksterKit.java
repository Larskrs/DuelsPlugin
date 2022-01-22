package net.larskrs.plugins.duels.Kits;

import com.cryptomorin.xseries.XMaterial;
import net.larskrs.plugins.duels.enums.KitType;
import net.larskrs.plugins.duels.instances.Kit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class PranksterKit extends Kit {
    public PranksterKit(KitType type, UUID uuid) {
        super(KitType.PRANKSTER, uuid);
    }

    @Override
    public void onStart(Player p) {
        p.getInventory().setHelmet(new ItemStack(Material.LEATHER_HELMET));
        p.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
        p.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
        p.getInventory().setBoots(new ItemStack(Material.LEATHER_BOOTS));

        p.getInventory().setItem(0, new ItemStack(Material.STONE_SWORD));
        p.getInventory().setItem(1, new ItemStack(Material.TNT, 3));
        ItemStack potion = new ItemStack(Material.POTION, 2);

        PotionMeta potionmeta = (PotionMeta) potion.getItemMeta();
        potionmeta.setMainEffect(PotionEffectType.SLOW);
        PotionEffect speed = new PotionEffect(PotionEffectType.SLOW, 2, 1);
        potionmeta.addCustomEffect(speed, true);
        potionmeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Slug Potion");
        potion.setItemMeta(potionmeta);

        p.getInventory().setItem(2, potion);
    }
}