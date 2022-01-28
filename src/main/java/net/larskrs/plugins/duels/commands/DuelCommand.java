package net.larskrs.plugins.duels.commands;

import net.larskrs.plugins.duels.Duels;
import net.larskrs.plugins.duels.GUI.ArenaGUI;
import net.larskrs.plugins.duels.instances.CustomKit;
import net.larskrs.plugins.duels.Files.KitsFile;
import net.larskrs.plugins.duels.GUI.KitGUI;
import net.larskrs.plugins.duels.GUI.TeamGUI;
import net.larskrs.plugins.duels.enums.GameState;
import net.larskrs.plugins.duels.instances.Arena;
import net.larskrs.plugins.duels.managers.ConfigManager;
import net.larskrs.plugins.duels.managers.Team;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class DuelCommand implements CommandExecutor, TabCompleter {

    private Duels duels;

    public DuelCommand (Duels duels) {
        this.duels = duels;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            Player p = (Player) sender;

                if (args.length == 3 && args[0].equalsIgnoreCase("setArenaName")) {

                    if (!p.hasPermission("simpleduels.admin")) {
                        p.sendMessage(ChatColor.RED + "This command can only be run by staff! please try a different thang.");
                        return false;
                    }

                    Arena a = duels.getArenaManager().getArena(Integer.parseInt(args[1]));
                    ConfigManager.setArenaName(a.getId(), args[2]);
                    p.sendMessage(ChatColor.YELLOW + "You set the name of the arena to: " + args[2] + "!");
                }
            else if (args.length == 2 && args[0].equalsIgnoreCase("setarenalobby")) {

                    if (!p.hasPermission("simpleduels.admin")) {
                        p.sendMessage(ChatColor.RED + "This command can only be run by staff! please try a different thang.");
                        return false;
                    }

                Arena a = duels.getArenaManager().getArena(Integer.parseInt(args[1]));
                System.out.println(a);
                ConfigManager.setArenaLobbyLocation(a.getId(), p.getLocation());
                p.sendMessage(ChatColor.YELLOW + "You set the lobby spawn of the arena!");
            }
                else if (args.length == 3 && args[0].equalsIgnoreCase("setteamspawn")) {

                    if (!p.hasPermission("simpleduels.admin")) {
                        p.sendMessage(ChatColor.RED + "This command can only be run by staff! please try a different thang.");
                        return false;
                    }

                    Arena a = duels.getArenaManager().getArena(Integer.parseInt(args[1]));
                    Team t = Team.valueOf(args[2]);
                    System.out.println(a);
                    ConfigManager.setArenaTeamSpawn(a.getId(), t, p.getLocation());
                    p.sendMessage(ChatColor.YELLOW + "You set the " + t +" spawn of the arena!");
                }
           else if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
                p.sendMessage(ChatColor.GREEN + "Here are the available arenas!");
                for (Arena a : duels.getArenaManager().getArenas()) {
                    p.sendMessage(ChatColor.GRAY + " - " + "[" + ChatColor.RED + a.getState().name() + ChatColor.GRAY + "] " + a.getId() + ChatColor.GREEN + " /duel join " + a.getId());
                }
            } else if (args.length == 1 && args[0].equalsIgnoreCase("team")) {
                Arena a = duels.getArenaManager().getArena(p);
                if (a != null) {
                         new TeamGUI(a , p);
                } else {
                    p.sendMessage(ChatColor.RED + "You are not in a match!");
                }
            } else if (args.length == 1 && args[0].equalsIgnoreCase("kit")) {
                Arena a = duels.getArenaManager().getArena(p);
                if (a != null) {
                        new KitGUI(a , p);
                } else {
                    new KitGUI(a , p);
                }
            } else if (args.length > 3 && args[0].equalsIgnoreCase("setKit")) {

                if (p.hasPermission("simpleduels.admin")) {

                StringBuilder builder = new StringBuilder();
                for (int i = 3; i < args.length; i++) {
                    builder.append(args[i]).append(" ");
                }
                String msg = builder.toString();
                KitsFile.registerKit(ChatColor.stripColor(args[1]), msg, ChatColor.translateAlternateColorCodes('&', args[1]), p.getInventory(), Material.getMaterial(args[2]));
                } else {
                    p.sendMessage(ChatColor.RED + "This command can only be run by staff! please try a different thang.");
                }
            } else if (args.length == 2 && args[0].equalsIgnoreCase("removeKit")) {

                if (p.hasPermission("simpleduels.admin")) {

                    KitsFile.removeKit(args[1]);
                } else {
                    p.sendMessage(ChatColor.RED + "This command can only be run by staff! please try a different thang.");
                }
            } else if (args.length == 1 && args[0].equalsIgnoreCase("leave")) {
                if (duels.getArenaManager().getArena(p) != null) {
                    duels.getArenaManager().getArena(p).removePlayer(p);
                    p.sendMessage(ChatColor.GREEN + "You left the match!");
                } else {
                    p.sendMessage(ChatColor.RED + "You are not in a match!");
                }
            } else if (args.length >= 1 && args[0].equalsIgnoreCase("reload")) {
                if (p.hasPermission("simpleduels.admin")) {
                    duels.reloadConfig();
                    p.sendMessage(ChatColor.GREEN + "Reloaded config file :)");
                } else {
                    p.sendMessage(ChatColor.RED + "This command can only be run by staff! please try a different thang.");
                }
            } else if (args.length >= 1 && args[0].equalsIgnoreCase("join")) {
                if (duels.getArenaManager().getArena(p) != null) {
                    p.sendMessage(ChatColor.RED + "You are already in a game, do '/duel leave' first!");
                    return false;
                }


                if (args.length == 1) {
                    p.sendMessage(ChatColor.RED + "Invalid Usage! You need to add an arena! /duel join <arena>");
                    new ArenaGUI(p);
                } else if (args.length == 2) {


                    Arena arena = duels.getArenaManager().getArena(args[1]);
                    if (arena != null) {
                        if (arena.getState() == GameState.RECRUITING || arena.getState() == GameState.COUNTDOWN) {
                            arena.addPlayer(p);
                            p.sendMessage(ChatColor.YELLOW + "You joined the game!");
                        } else {
                            p.sendMessage(ChatColor.RED + "This game is currently live! try again later. :(");
                        }

                    } else {
                        p.sendMessage(ChatColor.RED + "Invalid Arena! " + ChatColor.GREEN + "Here are the available arenas!");
                        for (Arena a : duels.getArenaManager().getArenas()) {
                            p.sendMessage(ChatColor.GRAY + " - " + "[" + ChatColor.RED + a.getState().name() + ChatColor.GRAY + "] "+a.getId() + ChatColor.GREEN + " /duel join " + a.getId());
                        }
                    }
                }
            } else {
                // HELP
                p.sendMessage(ChatColor.RED + "Invalid Usage! these are your commands:");
                getHelp(p, p.hasPermission("simpleduels.admin"));
            }
        } else {
            sender.sendMessage(ChatColor.RED + "This command can only be run by players :(");
        }
        return false;
    }

    private void getHelp(Player p, boolean hasPermission) {
            p.sendMessage("§6§l§m|------------|§c§l D U E L S §6§l§m|------------|");
        p.sendMessage(ChatColor.GREEN +"  - /duel kit " + ChatColor.GRAY + ": let's you select a kit.");
        p.sendMessage(ChatColor.GREEN +"  - /duel join <id> " + ChatColor.GRAY + ": let's you join a game.");
        p.sendMessage(ChatColor.GREEN +"  - /duel leave " + ChatColor.GRAY + ": let's you leave a game.");
        p.sendMessage(ChatColor.GREEN +"  - /duel team " + ChatColor.GRAY + ": let's you switch team, only loosers bail on their team.");
        if (hasPermission) {
            p.sendMessage(ChatColor.GOLD +"  - /duel reload " + ChatColor.GRAY + ": let's you reload the plugin.");
            p.sendMessage(ChatColor.GOLD +"  - /duel setkit <name> <icon> <description> " + ChatColor.GRAY + ": let's you register or change kits.");
            p.sendMessage(ChatColor.GOLD +"  - /duel removekit <name> " + ChatColor.GRAY + ": let's you remove kits.");
            p.sendMessage(ChatColor.GOLD +"  - /duel setArenaName <id> " + ChatColor.GRAY + ": let's you set arena name.");
            p.sendMessage(ChatColor.GOLD +"  - /duel setArenaLobby <id> " + ChatColor.GRAY + ": let's you set arena lobby.");
            p.sendMessage(ChatColor.GOLD +"  - /duel setTeamSpawn <id> <team> " + ChatColor.GRAY + ": let's you set team spawn.");
        }
        p.sendMessage("§6§l§m|----------------------------------|");

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        List<String> options = new ArrayList<>();

        if (args.length == 1) {
            options.add("join");
            options.add("team");
            options.add("kit");
            options.add("leave");
            if (sender.hasPermission("simpleduels.admin")) {
                options.add("reload");
                options.add("setKit");
                options.add("removeKit");
                options.add("setArenaName");
                options.add("setArenaLobby");
                options.add("setteamspawn");
            }

            return StringUtil.copyPartialMatches(args[0], options, new ArrayList<>());
        } else if (args.length == 2 && args[0].equalsIgnoreCase("join")) {
            for (Arena a : duels.getArenaManager().getArenas()) {
                if (!a.getState().equals(GameState.LIVE)) {
                    options.add(a.getName() + "");
                }
            }
            return options;
        } else if (args.length == 2 && args[0].equalsIgnoreCase("setarenaname")) {
        for (Arena a : duels.getArenaManager().getArenas()) {
            if (!a.getState().equals(GameState.LIVE)) {
                options.add(a.getId() + "");
            }
        }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("setarenalobby")) {
            for (Arena a : duels.getArenaManager().getArenas()) {
                if (!a.getState().equals(GameState.LIVE)) {
                    options.add(a.getId() + "");
                }
            }
        return options;
        } else if (args.length == 2 && args[0].equalsIgnoreCase("setteamspawn")) {
            for (Arena a : duels.getArenaManager().getArenas()) {
                if (!a.getState().equals(GameState.LIVE)) {
                    options.add(a.getId() + "");
                }
            }
            return options;
        } else if (args.length == 2 && args[0].equalsIgnoreCase("removekit")) {
            for (CustomKit kit : KitsFile.getKits()) {
                options.add(kit.getName());
            }
            return options;

    } else if (args.length == 3 && args[0].equalsIgnoreCase("setteamspawn")) {
            for (Team t : Team.values()) {
                    options.add(t.name() + "");
            }
            return options;
        }

        return null;
    }
}
