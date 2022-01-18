package net.larskrs.plugins.duels.tools;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Projectile;

public class XEntityType {

    public static boolean isProjectile(EntityType t) {
        return (t.equals(EntityType.ARROW) || t.equals(EntityType.SPECTRAL_ARROW));
    }

}
