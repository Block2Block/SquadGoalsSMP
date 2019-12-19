package me.block2block.squadgoalssmp.commands;

import me.block2block.squadgoalssmp.CacheManager;
import me.block2block.squadgoalssmp.entities.SMPPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TabCompleteTeam implements TabCompleter {


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (command.getName().equals("team")) {
            if (args.length == 1) {
                List<String> subcommands = Arrays.asList("help", "accept", "decline", "leave", "home", "sethome", "disband", "create", "invite", "deposit", "withdraw", "balance", "prefix", "transfer", "color", "chat");
                List<String> list = new ArrayList<>();

                for (String subcommand : subcommands) {
                    if (subcommand.startsWith(args[0].toLowerCase())) {
                        list.add(subcommand);
                    }
                }

                Collections.sort(list);
                return list;
            } else if (args.length == 2) {
                List<String> list = new ArrayList<>();
                switch (args[0].toLowerCase()) {
                    case "invite":
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if (p.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                                list.add(p.getName());
                            }
                        }
                        Collections.sort(list);
                        return list;
                    case "kick":
                        if (CacheManager.getProfile(((Player)sender).getUniqueId()).getTeam() == null) {
                            return null;
                        }
                        for (SMPPlayer p : CacheManager.getProfile(((Player)sender).getUniqueId()).getTeam().getMembers()) {
                            if (p.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                                list.add(p.getName());
                            }
                        }
                        Collections.sort(list);
                        return list;
                    case "transfer":
                        if (CacheManager.getProfile(((Player)sender).getUniqueId()).getTeam() == null) {
                            return null;
                        }
                        for (SMPPlayer p : CacheManager.getProfile(((Player)sender).getUniqueId()).getTeam().getMembers()) {
                            if (p.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                                if (p.getPlayer().isOnline()) {
                                    list.add(p.getName());
                                }
                            }
                        }
                        Collections.sort(list);
                        return list;
                    case "color":
                        List<String> colors = Arrays.asList("aqua", "black", "blue", "bold", "dark_aqua", "dark_blue", "dark_gray", "dark_green", "dark_purple", "dark_red", "gold", "gray", "green", "light_purple", "red", "white", "yellow");

                        for (String color : colors) {
                            if (color.startsWith(args[1].toLowerCase())) {
                                list.add(color);
                            }
                        }
                        Collections.sort(list);
                        return list;
                    default:
                        return null;
                }
            }
        }
        return null;
    }
}
