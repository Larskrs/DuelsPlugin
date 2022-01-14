package net.larskrs.plugins.duels.instances;

import net.larskrs.plugins.duels.Duels;
import net.larskrs.plugins.duels.enums.KitType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public abstract class Kit implements Listener {

    protected KitType type;
    protected UUID uuid;

    public Kit(KitType type, UUID uuid) {
        this.type = type;
        this.uuid = uuid;

        Bukkit.getPluginManager().registerEvents(this, Duels.getInstance());
    }


    public KitType getType() {
        return type;
    }

    public UUID getUuid() {
        return uuid;
    }

    public abstract void onStart(Player p);
    public void remove () {
        HandlerList.unregisterAll(this);
    }

}
