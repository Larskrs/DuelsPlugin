package net.larskrs.plugins.duels.Files;

import net.larskrs.plugins.duels.Duels;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class KitsFile {

    private Duels duels;
    private static File file;
    private static YamlConfiguration modifyFile;

    public KitsFile (Duels duels) {
        this.duels = duels;
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "loading... kits.yml");

        file = new File(duels.getDataFolder(), "kits.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } // Create file if it does not excist yet.

        modifyFile = YamlConfiguration.loadConfiguration(file);
        modifyFile.set("Car", "Ford");

        try {
            modifyFile.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveFile() {
        try {
            modifyFile.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static ItemStack getItem(String location) {
        ItemStack item = new ItemStack(Material.getMaterial(modifyFile.getString(location + ".material")));
        ItemMeta meta = item.getItemMeta();

        item.setAmount(Integer.parseInt(modifyFile.getString(location + ".amount")));
        List<String> ehL = modifyFile.getStringList(location + ".enchantments");
        for (String s : ehL) {
            String[] split = s.split("=", 2);
            meta.addEnchant(Enchantment.getByName(split[0]), Integer.parseInt(split[1]), true);
        }
        List<String> flaG = modifyFile.getStringList(location + ".flags");
        for (String s : flaG) {
            meta.addItemFlags(ItemFlag.valueOf(s));
        }
        item.setItemMeta(meta);
        return item;
    }
    public static void setKit(String path, PlayerInventory inventory) {
        modifyFile.set(path + "inventory.armor", inventory.getArmorContents());
        modifyFile.set(path + "inventory.content", inventory.getContents());
        saveFile();
    }
    public static void getKit(String path, Player p) {
        ItemStack[] content = ((List<ItemStack>) modifyFile.get(path + "inventory.armor")).toArray(new ItemStack[0]);
        p.getInventory().setArmorContents(content);
        content = ((List<ItemStack>) modifyFile.get(path + "inventory.content")).toArray(new ItemStack[0]);
        p.getInventory().setContents(content);
    }




}
