package net.larskrs.plugins.duels.listener;

import net.larskrs.plugins.duels.Duels;
import net.larskrs.plugins.duels.Files.PlayerDataFile;
import net.larskrs.plugins.duels.GUI.TeamGUI;
import net.larskrs.plugins.duels.enums.GameState;
import net.larskrs.plugins.duels.enums.KitType;
import net.larskrs.plugins.duels.instances.Arena;
import net.larskrs.plugins.duels.managers.ConfigManager;
import net.larskrs.plugins.duels.managers.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

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
        } else {

            e.setRespawnLocation(ConfigManager.getLobbySpawnLocation());
        }
    }

    @EventHandler
    public void onPlayerHunger(FoodLevelChangeEvent e) {
        if (e.getEntity() instanceof Player) {
            Arena a = duels.getArenaManager().getArena((Player) e.getEntity());
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
        Player killer = null;

        if (e.getCause().equals(EntityDamageEvent.DamageCause.VOID)) {
            p.teleport(ConfigManager.getLobbySpawnLocation());
            p.sendMessage("void = death. bruh");
            e.setCancelled(true);
            return;
        }

        if (e.getDamager() instanceof Snowball || e.getDamager() instanceof Egg || e.getDamager() instanceof Arrow || e.getDamager() instanceof Trident || e.getDamager() instanceof SpectralArrow || e.getDamager() instanceof EnderPearl) {
            Projectile pj = (Projectile) e.getDamager();
            if (pj.getShooter() instanceof Player) {
                killer = (Player) pj.getShooter();
            }
        } else if (e.getDamager() instanceof Player) {
            killer = (Player) e.getDamager();
        } else {
            return;
        }

        if (a == null) {
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


                    // player is in arena.

                    if (killer == null || killer == p)  {
                        p.setGameMode(GameMode.SPECTATOR);
                        new RespawnCountdown(duels, p, 10).start();
                        e.setCancelled(true);

                    } else {

                        a.getGame().onCustomRespawn(p, killer);
                        new RespawnCountdown(duels, p, 10).start();

                        p.setGameMode(GameMode.SPECTATOR);

                    }


            } else {

                p.teleport(ConfigManager.getLobbySpawnLocation());
                p.setHealth(p.getMaxHealth());
            }
        }
        }

    }



    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (!e.getPlayer().hasPermission("simpleduels.bypass.build") || duels.getArenaManager().getArena(e.getPlayer()) != null) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onBlockBreak(BlockPlaceEvent e) {
        if (!e.getPlayer().hasPermission("simpleduels.bypass.build") || duels.getArenaManager().getArena(e.getPlayer()) != null) {
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
                    if (a.getTeamCount(team) == 1) {
                        p.sendMessage(ChatColor.RED + "You can not leave the team, it will be empty!");
                    }
                    a.setTeam(p, team);
                    new TeamGUI(a, p);
                }


            }
            e.setCancelled(true);
        } else if (e.getClickedInventory() != null && e.getCurrentItem() != null && e.getView().getTitle().contains("Kit Selection")) {
            KitType type = KitType.valueOf(e.getCurrentItem().getItemMeta().getLocalizedName());
            Player p = (Player) e.getWhoClicked();
            Arena a = Duels.getInstance().getArenaManager().getArena(p);


                KitType currentKit = PlayerDataFile.getLastSavedKit(p.getUniqueId());
                if (currentKit != null && currentKit == type) {
                    p.sendMessage(ChatColor.RED + "You already have this kit equipped.");
                } else {
                    if (a != null) {
                        p.sendMessage(ChatColor.YELLOW + "You have " + ChatColor.GREEN + "equipped " + ChatColor.YELLOW + "the " + type.getDisplay() + " kit!");
                        a.setKit(p.getUniqueId(), type);
                    } else {
                        PlayerDataFile.getConfig().set(Bukkit.getPlayer(p.getUniqueId()).getName() + ".kit", type.name());
                        PlayerDataFile.saveFile();
                        p.sendMessage(ChatColor.YELLOW + "You have " + ChatColor.GREEN + "equipped " + ChatColor.YELLOW + "the " + type.getDisplay() + " kit!");
                    }
                }
                p.closeInventory();


            e.setCancelled(true);
        }
    }
}
