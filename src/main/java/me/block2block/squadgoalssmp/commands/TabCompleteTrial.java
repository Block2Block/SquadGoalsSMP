package me.block2block.squadgoalssmp.commands;

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

public class TabCompleteTrial implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (command.getName().equals("trial")) {
            if (args.length == 1) {
                List<String> subcommands = Arrays.asList("live", "die", "teleport", "startvote", "end");
                List<String> list = new ArrayList<>();

                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                        list.add(p.getName());
                    }
                }

                for (String subcommand : subcommands) {
                    if (subcommand.startsWith(args[0].toLowerCase())) {
                        list.add(subcommand);
                    }
                }

                Collections.sort(list);
                return list;
            }
        }
        return null;
    }
}
