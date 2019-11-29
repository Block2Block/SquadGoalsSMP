package me.block2block.squadgoalssmp.commands;

import me.block2block.squadgoalssmp.CacheManager;
import me.block2block.squadgoalssmp.Main;
import me.block2block.squadgoalssmp.entities.Purge;
import me.block2block.squadgoalssmp.entities.PurgeMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandPurge implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (p.isOp()) {
                if (args.length > 0) {
                    switch (args[0].toLowerCase()) {
                        case "start":
                            if (args.length == 2) {
                                if (!CacheManager.isPurge()) {
                                    if (args[1].matches("[0-9]+")) {
                                        int id = Integer.parseInt(args[1]);
                                        PurgeMode pm = PurgeMode.getByID(id);
                                        if (pm != null) {
                                            CacheManager.setPurge(new Purge(pm, -1));
                                        } else {
                                            p.sendMessage(Main.c("Purge","That is not a valid purge id."));
                                        }
                                    } else {
                                        p.sendMessage(Main.c("Purge","Invalid syntax. Correct syntax: /supersecretadmincommand start [purge id]"));
                                    }
                                } else {
                                    p.sendMessage(Main.c("Purge","There is already a purge active. Please end it in order to start a new one."));
                                }
                            } else {
                                p.sendMessage(Main.c("Purge","Invalid syntax. Correct syntax: /supersecretadmincommand start [purge id]"));
                            }
                            break;
                        case "end":
                            if (CacheManager.isPurge()) {
                                Purge purge = CacheManager.getPurge();
                                p.sendMessage(Main.c("Purge","You ended the purge."));
                                purge.end();
                            } else {
                                p.sendMessage(Main.c("Purge","There is no purge currently in progress."));
                            }
                            break;
                        default:
                            p.sendMessage(Main.c("Purge","Available Sub-commands:\n" +
                                    "&d/supersecretadmincommand start [purge id]&r - Start a purge with the specified id.\n" +
                                    "&d/supersecretadmincommand end&r - End a purge in progress."));
                    }
                } else {
                    p.sendMessage(Main.c("Purge","Available Sub-commands:\n" +
                            "&d/supersecretadmincommand start [purge id]&r - Start a purge with the specified id.\n" +
                            "&d/supersecretadmincommand end&r - End a purge in progress."));
                }
            } else {
                p.sendMessage(Main.c("Command Manager","You do not have permission to execute this command."));
            }
        }
        return true;
    }
}
