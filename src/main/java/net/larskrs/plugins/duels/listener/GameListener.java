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
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class GameListener implements Listener {

    private Duels duels;

    public GameListener(Duels duels) {
        this.duels = duels;
    }

    @EventHandler
    public void onPlayerKill(PlayerDeathEvent e) {

        if (e.getEntity().getKiller() != null) {
            Player killer = e.getEntity().getKiller();
            Player p = e.getEntity();

            if (duels.getArenaManager().getArena(p) != null && duels.getArenaManager().getArena(killer) != null) {
                // The two players are both in arena.
                Arena pArena = duels.getArenaManager().getArena(p);
                Arena killerArena = duels.getArenaManager().getArena(killer);
                if (killerArena == pArena && killerArena.getState().equals(GameState.LIVE)) {
                    // Both players were in the live match.
                    killerArena.sendMessage(ChatColor.GOLD + "[GAME]" + ChatColor.GREEN + p.getName() + " was killed by " + killer.getName() + "!");
                    killerArena.getGame().addPoint(pArena.getTeam(killer));

                    e.getDrops().clear();
                    e.getDrops().add(new ItemStack(Material.GOLDEN_APPLE));

                }
            }



        }

    }
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        if (duels.getArenaManager().getArena(e.getPlayer()) != null) {
            // player is in arena.
            Player p = (Player) e.getPlayer();
            Arena a = Duels.getInstance().getArenaManager().getArena(p);
            e.setRespawnLocation(ConfigManager.getArenaSpawn(duels.getArenaManager().getArena(p).getId()));
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
    public void onPlayerHunger(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Arena a = duels.getArenaManager().getArena((Player) e.getEntity());
                if (a.getState() != GameState.LIVE) {
                    e.setCancelled(true);
                }

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
