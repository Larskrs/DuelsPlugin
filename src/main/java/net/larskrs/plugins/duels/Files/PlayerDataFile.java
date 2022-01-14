package net.larskrs.plugins.duels.Files;

import jdk.jfr.internal.LogLevel;
import net.larskrs.plugins.duels.Duels;
import net.larskrs.plugins.duels.enums.KitType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
    public static KitType getLastSavedKit(UUID uuid) {
        if (PlayerDataFile.getConfig().contains(Bukkit.getPlayer(uuid).getName() + ".kit")) {
            return KitType.valueOf(PlayerDataFile.getConfig().getString(Bukkit.getPlayer(uuid).getName() + ".kit"));
        }
        return null;
    }





}

