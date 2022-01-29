package net.larskrs.plugins.duels.listener;

import net.larskrs.plugins.duels.Duels;
import net.larskrs.plugins.duels.instances.CustomKit;
import net.larskrs.plugins.duels.Files.KitsFile;
import net.larskrs.plugins.duels.Files.PlayerDataFile;
import net.larskrs.plugins.duels.GUI.TeamGUI;
import net.larskrs.plugins.duels.Games.Fortress;
import net.larskrs.plugins.duels.enums.GameState;
import net.larskrs.plugins.duels.instances.Arena;
import net.larskrs.plugins.duels.managers.ConfigManager;
import net.larskrs.plugins.duels.managers.Team;
import net.larskrs.plugins.duels.tools.StorageBlockTool;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class GameListener implements Listener {

    private Duels duels;
    private HashMap<UUID, UUID> lastHit;

    public GameListener(Duels duels) {
        this.duels = duels;
        this.lastHit = new HashMap<>();
    }

    public void resetLastHit(Player player) {

        this.lastHit.remove(player.getUniqueId());

    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        if (duels.getArenaManager().getArena(e.getPlayer()) != null) {
            // player is in arena.
            Player p = (Player) e.getPlayer();
            Arena a = Duels.getInstance().getArenaManager().getArena(p);
            e.setRespawnLocation(ConfigManager.getTeamSpawn(duels.getArenaManager().getArena(p).getId(), a.getTeam(p)));
            a.getKits().get(p.getUniqueId()).giveKit(p);
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
    public void onUnwantedSpawns(EntitySpawnEvent e) {
        if (e.getEntity().getType() == EntityType.ENDERMITE) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onSignClick(PlayerInteractEvent e) {
        if (e.getHand().equals(EquipmentSlot.HAND) && e.hasBlock()) {
            if (StorageBlockTool.isStorageBlock(e.getClickedBlock())) {

                Arena a = duels.getArenaManager().getArena(e.getPlayer());

                if (a != null) {
                e.setCancelled(true);

                if (ConfigManager.getGameType(a.getId()).contains("FORTRESS") && a.getState() == GameState.LIVE) {
                    a.getGame().addPoint(a.getTeam(e.getPlayer()));
                    Fortress fortress = (Fortress) a.getGame();
                    fortress.lootStorage(e.getPlayer(), e.getClickedBlock());
                }

                return;
                }
            }else {
                if (e.getClickedBlock().getType().name().contains("DOOR")) {
                    if (duels.getArenaManager().getArena(e.getPlayer()) != null) {
                        e.setCancelled(true);
                        return;
                    }
                }
            }

        }

    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent e) {

        // Damage Blackslists




        if (e.getEntity() instanceof Player) {

            Arena a = duels.getArenaManager().getArena((Player) e.getEntity());
            Player p = (Player) e.getEntity();
            Player killer = null;

            Boolean isProjectile = false;

            if (e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)) {
                e.setDamage(e.getDamage() / 2);
                return;
            }


            if (e.getDamager() instanceof Snowball || e.getDamager() instanceof Egg || e.getDamager() instanceof Arrow || e.getDamager() instanceof Trident || e.getDamager() instanceof SpectralArrow || e.getDamager() instanceof EnderPearl) {
                isProjectile = true;
                Projectile pj = (Projectile) e.getDamager();
                if (pj.getShooter() instanceof Player) {
                    killer = (Player) pj.getShooter();
                }
                pj.remove();
            } else if (e.getDamager() instanceof Player) {
                killer = (Player) e.getDamager();
            }

            if (a == null) {
                e.setCancelled(true);
                return;
            }



            if (a.getState() != GameState.LIVE) {
                e.setCancelled(true);
            }

            if (a.getTeam(p) == a.getTeam(killer) && p != killer) {
                e.setCancelled(true);
                return;
            }


            lastHit.put(e.getEntity().getUniqueId(), killer.getUniqueId());


            System.out.println("can i die? " + (((Player) e.getEntity()).getHealth() - e.getDamage() <= 0) + " damage: " + (((Player) e.getEntity()).getHealth() - e.getDamage()));
            if (((Player) e.getEntity()).getHealth() - e.getDamage() <= 0) {
                e.setCancelled(true);
                // custom respawn logic.

                if (duels.getArenaManager().getArena(p) != null) {


                    // player is in arena.
                    if (!isProjectile) {

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
    public void onPlayerDamage(EntityDamageEvent e) {

        // Damage Blackslists
        if (e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK) ||  e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)) {
            return;
        }



        if (e.getEntity() instanceof Player) {

            Arena a = duels.getArenaManager().getArena((Player) e.getEntity());
            Player p = (Player) e.getEntity();

            if (e.getCause().equals(EntityDamageEvent.DamageCause.VOID)) {
                e.setDamage(0);
                e.setCancelled(true);
                p.teleport(ConfigManager.getLobbySpawnLocation());
                p.sendMessage("void = death. bruh");
                p.setFallDistance(0);
                p.setFoodLevel(20);
                p.setHealth(p.getMaxHealth());
                p.setGameMode(GameMode.SURVIVAL);

                if (a != null) {
                    a.respawnPlayer(p.getUniqueId());
                }
                return;
            }

            if (a == null) {
                e.setCancelled(true);
                return;
            }

            if (a.getState() != GameState.LIVE) {
                e.setCancelled(true);
            }
            System.out.println( "can i die? " + (((Player) e.getEntity()).getHealth() - e.getDamage() <= 0) + " damage: " + (((Player) e.getEntity()).getHealth() - e.getDamage()));
            if (((Player) e.getEntity()).getHealth() - e.getDamage() <= 0) {
                e.setCancelled(true);
                // custom respawn logic.



                if (duels.getArenaManager().getArena(p) != null) {


                    // player is in arena.

                    a.sendMessage(ChatColor.GOLD + "  " + ChatColor.GREEN + p.getName() + " was killed!");
                    new RespawnCountdown(duels, p, 10).start();
                    if (lastHit.get(p.getUniqueId()) == null && lastHit.get(p.getUniqueId()) != p.getUniqueId()) {
                    a.respawnPlayer(p.getUniqueId());
                    } else {
                        a.getGame().onCustomRespawn(p, Bukkit.getPlayer(lastHit.get(p.getUniqueId())));
                        Duels.getGameListener().resetLastHit(p);
                    }
                    p.setGameMode(GameMode.SPECTATOR);




                } else {

                    p.teleport(ConfigManager.getLobbySpawnLocation());
                    p.setHealth(p.getMaxHealth());
                }
                e.setCancelled(true);
            }
        }

    }

    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        if (e.getEntity().getKiller() != null && e.getEntity().getKiller() instanceof Player) {
            Player p = (Player) e.getEntity();
            if (duels.getArenaManager().getArena(p) != null) {

            }
        }

        if (e.getEntity().getType() == EntityType.ARMOR_STAND) {

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

        if (e.getBlock().getType() == Material.TNT) {
            e.getPlayer().getWorld().spawn(e.getBlockPlaced().getLocation(), EntityType.PRIMED_TNT.getEntityClass());
            e.setCancelled(true);
            e.getPlayer().getInventory().removeItem(new ItemStack(Material.TNT, 1));
            return;
        }


        if (!e.getPlayer().hasPermission("simpleduels.bypass.build") || duels.getArenaManager().getArena(e.getPlayer()) != null) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onBlockBreak(PlayerDropItemEvent e) {
        if (!e.getPlayer().hasPermission("simpleduels.bypass.drop") || duels.getArenaManager().getArena(e.getPlayer()) != null) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onTntExplode(EntityExplodeEvent e) {
            e.blockList().clear();
    }
    @EventHandler
    public void hangingBreakByEntityEvent(HangingBreakByEntityEvent e) {
        Player p = null;
        if (e.getRemover() instanceof Player) {
            p =  (Player) e.getRemover();
            if (!p.getGameMode().equals(GameMode.CREATIVE)) {
                e.setCancelled(true);
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

                if (a.getState().equals(GameState.LIVE)) {
                    p.sendMessage(ChatColor.RED + "You can not CHANGE team while playing.");
                    p.closeInventory();
                    return;
                }

                if (a.getTeam(p) == team) {
                    p.sendMessage(ChatColor.RED + "You are already on this team!");
                } else {
                    if (a.getTeamCount(a.getTeam(p)) == 1) {
                        p.sendMessage(ChatColor.RED + "You can not leave the team, it will be empty!");
                        return;

                    }
                    a.setTeam(p, team);
                    new TeamGUI(a, p);
                }


            }

            e.setCancelled(true);
        } else if (e.getClickedInventory() != null && e.getCurrentItem() != null && e.getView().getTitle().contains("Kit Selection")) {
            CustomKit type = KitsFile.getKit(e.getCurrentItem().getItemMeta().getLocalizedName());
            Player p = (Player) e.getWhoClicked();
            Arena a = Duels.getInstance().getArenaManager().getArena(p);

                CustomKit currentKit = PlayerDataFile.getLastSavedKit(p.getUniqueId());
                    e.setCancelled(true);


                if (currentKit != null && currentKit == type) {
                    p.sendMessage(ChatColor.RED + "You already have this kit equipped.");
                } else {
                    if (a != null) {
                        p.sendMessage(ChatColor.YELLOW + "You have " + ChatColor.GREEN + "equipped " + ChatColor.YELLOW + "the " + type.getDisplay() + " kit!");
                        a.setKit(p.getUniqueId(), type);
                    } else {
                        PlayerDataFile.getConfig().set(p.getUniqueId() + ".kit", type.getName());
                        PlayerDataFile.saveFile();
                        p.sendMessage(ChatColor.YELLOW + "You have " + ChatColor.GREEN + "equipped " + ChatColor.YELLOW + "the " + type.getDisplay() + " kit!");
                    }
                }
                p.closeInventory();


        }
    }
}
