package net.larskrs.plugins.duels.commands;

import net.larskrs.plugins.duels.Duels;
import net.larskrs.plugins.duels.GUI.KitGUI;
import net.larskrs.plugins.duels.GUI.TeamGUI;
import net.larskrs.plugins.duels.enums.GameState;
import net.larskrs.plugins.duels.instances.Arena;
import net.larskrs.plugins.duels.managers.ArenaManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
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

            if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
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
            } else if (args.length == 1 && args[0].equalsIgnoreCase("leave")) {
                if (duels.getArenaManager().getArena(p) != null) {
                    duels.getArenaManager().getArena(p).removePlayer(p);
                    p.sendMessage(ChatColor.GREEN + "You left the match!");
                } else {
                    p.sendMessage(ChatColor.RED + "You are not in a match!");
                }
            } else if (args.length >= 1 && args[0].equalsIgnoreCase("reload")) {
                duels.reloadConfig();
                p.sendMessage(ChatColor.GREEN + "Reloaded config file :)");
            } else if (args.length >= 1 && args[0].equalsIgnoreCase("join")) {
                if (duels.getArenaManager().getArena(p) != null) {
                    p.sendMessage(ChatColor.RED + "You are already in a game, do '/duel leave' first!");
                    return false;
                }


                if (args.length == 1) {
                    p.sendMessage(ChatColor.RED + "Invalid Usage! You need to add an arena! /duel join <arena>");
                } else if (args.length == 2) {

                    int id;
                    try {
                        id = Integer.parseInt(args[1]);
                    } catch (NumberFormatException e) {
                        p.sendMessage(ChatColor.RED + "You specified an invalid arena ID.");
                        return false; // stop the command from checking anything else!
                    }

                    Arena arena = duels.getArenaManager().getArena(id);
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
            }
        } else {
            sender.sendMessage(ChatColor.RED + "This command can only be run by players :(");
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        List<String> options = new ArrayList<>();

        if (args.length == 1) {
            options.add("join");
            options.add("team");
            options.add("kit");
            options.add("leave");
            if (sender.hasPermission("simpleduels.command.reload")) {
                options.add("reload");
            }

            return StringUtil.copyPartialMatches(args[0], options, new ArrayList<>());
        } else if (args.length == 2 && args[0].equalsIgnoreCase("join")) {
            for (Arena a : duels.getArenaManager().getArenas()) {
                if (!a.getState().equals(GameState.LIVE)) {
                    options.add(a.getId() + "");
                }
            }
            return options;
        }

        return null;
    }
}
