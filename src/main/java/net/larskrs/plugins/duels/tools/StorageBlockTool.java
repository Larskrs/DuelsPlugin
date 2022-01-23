package net.larskrs.plugins.duels.tools;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.inventory.Inventory;

public class StorageBlockTool {


    public static boolean isStorageBlock (Block block) {
            BlockState state = block.getState();

            if (!(state instanceof Container)) {
                return false;
            }

            Container cont = (Container) state;
            Inventory inv = cont.getInventory();
            return true;
    }

}
