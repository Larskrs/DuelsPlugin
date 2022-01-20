package net.larskrs.plugins.duels.listener;

import com.cryptomorin.xseries.XMaterial;
import net.larskrs.plugins.duels.Duels;
import net.larskrs.plugins.duels.Files.PlayerDataFile;
import net.larskrs.plugins.duels.GUI.TeamGUI;
import net.larskrs.plugins.duels.enums.GameState;
import net.larskrs.plugins.duels.enums.KitType;
import net.larskrs.plugins.duels.instances.Arena;
import net.larskrs.plugins.duels.managers.ArenaManager;
import net.larskrs.plugins.duels.managers.ConfigManager;
import net.larskrs.plugins.duels.managers.Team;
import net.minecraft.network.protocol.game.PacketPlayOutCamera;
import net.minecraft.world.entity.Entity;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;

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
    public void onSignClick(PlayerInteractEvent e) {
        if (e.getHand().equals(EquipmentSlot.HAND) && e.hasBlock() && e.getClickedBlock().getType().equals(XMaterial.OAK_WALL_SIGN.parseMaterial()) && (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_BLOCK)) {
            Arena a = duels.getArenaManager().getArena(e.getClickedBlock().getLocation());
            if (a != null) {
                Bukkit.dispatchCommand(e.getPlayer(), "duel join " + a.getId());
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


            if (e.getCause().equals(EntityDamageEvent.DamageCause.VOID)) {
                p.teleport(ConfigManager.getLobbySpawnLocation());
                p.sendMessage("void = death. bruh");
                p.setHealth(p.getMaxHealth());
                p.setGameMode(GameMode.SURVIVAL);
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
            }

            if (a == null) {
                e.setCancelled(true);
                return;
            }

            if (a.getState() != GameState.LIVE) {
                e.setCancelled(true);
            }

            if (a.getTeam(p) == a.getTeam(killer)) {
                e.setCancelled(true);
                return;
            }

            System.out.println("can i die? " + (((Player) e.getEntity()).getHealth() - e.getDamage() <= 0) + " damage: " + (((Player) e.getEntity()).getHealth() - e.getDamage()));
            if (((Player) e.getEntity()).getHealth() - e.getDamage() <= 0) {
                e.setCancelled(true);
                // custom respawn logic.

                if (duels.getArenaManager().getArena(p) != null) {


                    // player is in arena.

                    a.getGame().onCustomRespawn(p, killer);
                    new RespawnCountdown(duels, p, 10).start();

                    p.setGameMode(GameMode.SPECTATOR);

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

        if (e.getEntity() instanceof Player) {

            Arena a = duels.getArenaManager().getArena((Player) e.getEntity());
            Player p = (Player) e.getEntity();

            if (e.getCause().equals(EntityDamageEvent.DamageCause.VOID)) {
                if (a != null && a.getState() == GameState.LIVE) {
                    p.teleport(ConfigManager.getTeamSpawn(duels.getArenaManager().getArena(p).getId(), duels.getArenaManager().getArena(p).getTeam(p)));
                    p.sendMessage("void = death. bruh");
                    p.setVelocity(new Vector(0, 0, 0));
                    p.setFallDistance(0);
                    e.setDamage(0);
                    a.respawnPlayer(p.getUniqueId());
                    return;
                }

                e.setCancelled(true);
                p.teleport(ConfigManager.getLobbySpawnLocation());
                p.sendMessage("void = death. bruh");
                p.setVelocity(new Vector(0, 0, 0));
                p.setFallDistance(0);
                e.setDamage(0);
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
                        p.setGameMode(GameMode.SPECTATOR);
                        new RespawnCountdown(duels, p, 10).start();
                        e.setCancelled(true);




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
