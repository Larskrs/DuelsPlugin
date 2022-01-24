package net.larskrs.plugins.duels.instances;

import net.larskrs.plugins.duels.Duels;
import net.larskrs.plugins.duels.enums.KitType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

    public class CustomKit implements Listener {

        private String display;
        private String description;
        private Material icon;
        private ItemStack[] armourContents;
        private ItemStack[] contents;
        private String name;

        public CustomKit(String name, Material icon) {
            this.icon = icon;
            this.name = name;

            Bukkit.getPluginManager().registerEvents(this, Duels.getInstance());
        }

        public void giveKit(Player p) {
            p.getInventory().setContents(this.getContents());

        }

        public void setDisplay (String s) {
        this.display = s;
        }
        public void setDescription (String s) {
            this.description = s;
        }
        public void setContents (ItemStack[] s) {
            this.contents = s;
        }
        public void setArmourContents (ItemStack[] s) {
            this.armourContents = s;
        }
        public void setIcon (Material s) {
            this.icon = s;
        }
        public void remove () {
            HandlerList.unregisterAll(this);
        }

        public String getDisplay() {
            return display;
        }

        public String getDescription() {
            return description;
        }

        public Material getIcon() {
            return icon;
        }

        public ItemStack[] getArmourContents() {
            return armourContents;
        }

        public ItemStack[] getContents() {
            return contents;
        }

        public String getName() {
            return name;
        }
    }

