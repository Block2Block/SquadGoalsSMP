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

public class TabCompleteTrial implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (command.getName().equals("trial")) {
            if (args.length == 1) {
                List<String> subcommands = Arrays.asList("live", "die", "teleport", "startvote", "end");
                for (Player p : Bukkit.getOnlinePlayers()) {
                    subcommands.add(p.getName());
                }
                List<String> list = new ArrayList<>();

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
