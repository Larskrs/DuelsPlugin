package net.larskrs.plugins.duels.Files;

import net.larskrs.plugins.duels.Duels;
import net.larskrs.plugins.duels.instances.CustomKit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PlayerDataFile {

    private Duels duels;
    private static File file;
    private static YamlConfiguration modifyFile;

    public PlayerDataFile (Duels duels) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "loading... data.yml");
        this.duels = duels;

        file = new File(duels.getDataFolder(), "data.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } // Create file if it does not excist yet.

        modifyFile = YamlConfiguration.loadConfiguration(file);


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
    public static void reloadFile () {
        try {
            modifyFile.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
    public static YamlConfiguration getConfig() {
        return modifyFile;
    }
    public static CustomKit getLastSavedKit(UUID uuid) {
        if (PlayerDataFile.getConfig().contains(uuid + ".kit")) {
            CustomKit kit = KitsFile.getKit(modifyFile.getString(uuid + ".kit"));
            return kit;
        } else {
        System.out.println("Failed to get info from: " + modifyFile.getString(uuid.toString()));
        }
        return null;
    }





}

