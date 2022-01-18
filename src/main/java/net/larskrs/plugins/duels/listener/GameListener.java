package net.larskrs.plugins.duels.listener;

import net.larskrs.plugins.duels.Duels;
import net.larskrs.plugins.duels.GUI.KitGUI;
import net.larskrs.plugins.duels.GUI.TeamGUI;
import net.larskrs.plugins.duels.enums.GameState;
import net.larskrs.plugins.duels.enums.KitType;
import net.larskrs.plugins.duels.instances.Arena;
import net.larskrs.plugins.duels.managers.ConfigManager;
import net.larskrs.plugins.duels.managers.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

public class GameListener implements Listener {

    private Duels duels;

    public GameListener(Duels duels) {
        this.duels = duels;
    }
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        if (duels.getArenaManager().getArena(e.getPlayer()) != null) {
            // player is in arena.
            Player p = (Player) e.getPlayer();
            Arena a = Duels.getInstance().getArenaManager().getArena(p);
            e.setRespawnLocation(ConfigManager.getTeamSpawn(duels.getArenaManager().getArena(p).getId(), a.getTeam(p)));
            a.getKits().get(p.getUniqueId()).onStart(p);
        }
        else {

            e.setRespawnLocation(ConfigManager.getLobbySpawnLocation());
        }
    }
    @EventHandler
    public void onPlayerHunger(FoodLevelChangeEvent e) {
        if (e.getEntity() instanceof Player) {
            Arena a = duels.getArenaManager().getArena( (Player) e.getEntity());
            if (a != null) {
                if (a.getState() != GameState.LIVE) {
                e.setCancelled(true);
                }
            } else {
                e.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent e) {

        if (e.getEntity() instanceof Player) {
            Arena a = duels.getArenaManager().getArena((Player) e.getEntity());
                        Player p = (Player) e.getEntity();

                        if (a == null ) {
                            e.setCancelled(true);
                            return;
                        }

                if (a.getState() != GameState.LIVE) {
                    e.setCancelled(true);
                }
                if (((Player) e.getEntity()).getHealth() - e.getDamage() <= 0) {
                    e.setCancelled(true);
                    // custom respawn logic.

                    if (duels.getArenaManager().getArena(p) != null) {
                        if (e.getDamager() instanceof Player)
                        // player is in arena.
                        p.teleport(ConfigManager.getTeamSpawn(duels.getArenaManager().getArena(p).getId(), a.getTeam(p)));
                        a.getKits().get(p.getUniqueId()).onStart(p);
                        p.setHealth(p.getMaxHealth());

                            a.getGame().onCustomRespawn(p,(Player) e.getDamager());
                            a.getGame().onNewRoundBegin();
                    }
                    else {

                        p.teleport(ConfigManager.getLobbySpawnLocation());
                        p.setHealth(p.getMaxHealth());
                    }

                }
            } else {
                e.setCancelled(true);
            }
        }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {

        if (e.getClickedInventory() != null && e.getCurrentItem() != null && e.getView().getTitle().contains("Team Selection")) {
            Team team = Team.valueOf(e.getCurrentItem().getItemMeta().getLocalizedName());
            Player p = (Player) e.getWhoClicked();
            Arena a = Duels.getInstance().getArenaManager().getArena(p);


            if (a != null) {
                if (a.getTeam(p) == team) {
                    p.sendMessage(ChatColor.RED + "You are already on this team!");
                } else {
                    a.setTeam(p, team);
                    new TeamGUI(a, p);
                }


            }
            e.setCancelled(true);
        } else if (e.getClickedInventory() != null && e.getCurrentItem() != null && e.getView().getTitle().contains("Kit Selection")) {
            KitType type = KitType.valueOf(e.getCurrentItem().getItemMeta().getLocalizedName());
            Player p = (Player) e.getWhoClicked();
            Arena a = Duels.getInstance().getArenaManager().getArena(p);

            if (a != null) {
                KitType currentKit = a.getKit(p);
                if (currentKit != null && currentKit == type) {
                    p.sendMessage(ChatColor.RED + "You already have this kit equipped.");
                } else {
                    p.sendMessage(ChatColor.YELLOW + "You have " + ChatColor.GREEN + "equipped " + ChatColor.YELLOW + "the " + type.getDisplay() + " kit!");
                    a.setKit(p.getUniqueId(), type);

                }
                p.closeInventory();

            }
            e.setCancelled(true);
        }
    }
}
