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




}
