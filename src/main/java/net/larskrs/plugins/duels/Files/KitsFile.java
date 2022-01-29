package net.larskrs.plugins.duels.Files;

import net.larskrs.plugins.duels.Duels;
import net.larskrs.plugins.duels.instances.CustomKit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LingeringPotion;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.*;

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
        Bukkit.getConsoleSender().sendMessage("saving kit.yml");
    }
    public static void serializeItemStack(ItemStack item, String url) {

        modifyFile.set(url + ".type", item.getType().name());
        modifyFile.set(url + ".amount", item.getAmount());
        if (item.getItemMeta() != null) {modifyFile.set(url + ".name", item.getItemMeta().getDisplayName());}

        List<String> enchants = new ArrayList<>();
        for (Enchantment e : item.getEnchantments().keySet()) {
                enchants.add(e.getName() + "=" + item.getEnchantmentLevel(e));
             }
        modifyFile.set(url + ".enchantments", enchants);
        
        saveFile();
    }
    public static void serializePotionItem(ItemStack item, String url) {
        modifyFile.set(url + ".type", item.getType().name());
        modifyFile.set(url + ".amount", item.getAmount());
        if (item.getItemMeta() != null) {modifyFile.set(url + ".name", item.getItemMeta().getDisplayName());}

        PotionMeta meta = (PotionMeta) item.getItemMeta();
        PotionData pd = meta.getBasePotionData();
        PotionType type = pd.getType();

        List<String> effects = new ArrayList<>();

            effects.add(type.name() + "=" + pd.isUpgraded() + "=" + pd.isExtended());

        modifyFile.set(url + ".potion-effects", effects);

        saveFile();
    }
    public static ItemStack getSerializedPotionItem(String url) {
        ItemStack i = new ItemStack(Material.getMaterial(modifyFile.getString(url + ".type")));
        i.setAmount(modifyFile.getInt(url + ".amount"));


        PotionMeta m = (PotionMeta) i.getItemMeta();
        if (m != null) {
            m.setDisplayName(modifyFile.getString(url + ".name"));


            List<String> effects = modifyFile.getStringList(url + ".potion-effects");
            for (String s : effects) {
                String[] split = s.split("=", 3);
                m.setBasePotionData(new PotionData(PotionType.valueOf(split[0]), Boolean.parseBoolean(split[2]), Boolean.parseBoolean(split[1])));
            }
            i.setItemMeta(m);

        }
        return i;

    }
    public static ItemStack getSerializedItemStack(String url) {
        ItemStack i = new ItemStack(Material.getMaterial(modifyFile.getString(url + ".type")));
        i.setAmount(modifyFile.getInt(url + ".amount"));

        ItemMeta m = i.getItemMeta();
        if (m != null) {
            m.setDisplayName(modifyFile.getString(url + ".name"));


            List<String> enchants = modifyFile.getStringList(url + ".enchantments");
            for (String s : enchants) {
                String[] split = s.split("=", 2);
                m.addEnchant(Enchantment.getByName(split[0]), Integer.parseInt(split[1]), true);
            }
            i.setItemMeta(m);

        }
        return i;
        } 
    public static void registerKit(String name, String description, String display, PlayerInventory inv, Material icon) {

                modifyFile.set("kits." + name, null);

        int i = 0;
        for (ItemStack content : inv.getContents()) {
            if (content != null) {

                if (content.getItemMeta() instanceof PotionMeta) {
                    serializePotionItem(content, "kits." + name + "." + i);
                } else {
                    serializeItemStack(content, "kits." + name + "." + i);
                }

            } else {
                serializeItemStack(new ItemStack(Material.AIR), "kits." + name + "." + i);
            }
                i++;
        }
        modifyFile.set("kits." + name + ".options.description", description);
        modifyFile.set("kits." + name + ".options.display", display);
        modifyFile.set("kits." + name + ".options.icon", icon.name());

        saveFile();
        
    }
    public static CustomKit getKit(String name) {
        CustomKit kit = new CustomKit(name, Material.getMaterial(modifyFile.getString("kits." + name + ".options.icon")));

        List<ItemStack> contents = new ArrayList<>();
        for (String s : modifyFile.getConfigurationSection("kits." + name).getKeys(false)) {
            if (!s.equalsIgnoreCase("options")) {

                System.out.println(modifyFile.getString("kits." + name + "." + s + ".type"));
                if (modifyFile.getString("kits." + name + "." + s + ".type").contains("POTION")) {
                    contents.add(getSerializedPotionItem("kits." + name + "." + s));
                } else {
                contents.add(getSerializedItemStack("kits." + name + "." + s));
                }

            }
        }
        ItemStack[] contentsArray = contents.toArray(new ItemStack[0]);
        kit.setContents(contentsArray);
        kit.setDescription(ChatColor.translateAlternateColorCodes('&', modifyFile.getString("kits." + name + ".options.description")));
        kit.setDisplay(modifyFile.getString("kits." + name + ".options.display"));
        return kit;
    }

    public static List<CustomKit> getKits() {
        List<CustomKit> kits = new ArrayList<>();

        for (String s : modifyFile.getConfigurationSection("kits").getKeys(false)) {
            CustomKit kit = getKit(s);
            if (kit != null) {
            kits.add(kit);
            }
        }

        return kits;
    }
    public static void removeKit(String name) {
        modifyFile.set("kits." + name, null);
        Bukkit.getConsoleSender().sendMessage("§cremoving kit, " + name);
        PlayerDataFile.clearData("kit");
        saveFile();
    }

}
