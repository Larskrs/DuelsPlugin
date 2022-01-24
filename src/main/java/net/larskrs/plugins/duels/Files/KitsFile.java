package net.larskrs.plugins.duels.Files;

import net.larskrs.plugins.duels.Duels;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

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
    public static void serializeItemStack(ItemStack item, String url) {
        modifyFile.set(url + ".type", item.getType());
        modifyFile.set(url + ".amount", item.getAmount());
        modifyFile.set(url + ".name", item.getDisplayName());
        List<String> enchants = new ArrayList<>();
        for (String s : item.getEnchantments()) { 
                enchants.add(s);
             }
        modifyFile.set(url + ".enchantments", enchants);
        
        saveFile();
    }
    public static ItemStack getSerializedItemStack(String url) {
          ItemStack i = new ItemStack(Material.getMaterial(modifyFile.get(url + ".type")));
          ItemMeta m = i.getItemMeta();
            i.setAmount(modifyFile.get(url + ".amount"));
            m.setDisplayName(modifyFile.get(url + ".name"));
        
        
        List<String> enchants = modifyFile.getStringList(url + ".enchantments");
        for (String s : enchants) {
            String[] split = s.Split("=", 2);
            m.addEnchant(Enchantment.getByName(split[0]), Integer.parseInt(split[1]));
        }
        
        return i;
        }        

}
