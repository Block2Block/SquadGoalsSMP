package me.block2block.squadgoalssmp.commands;

import me.block2block.squadgoalssmp.CacheManager;
import me.block2block.squadgoalssmp.Main;
import me.block2block.squadgoalssmp.utils.UUIDFetcher;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CommandWhitelist implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (p.isOp()) {
                if (args.length == 2) {
                    switch (args[0].toLowerCase()) {
                        case "add":
                            if (args[1].matches("[0-9A-Za-z_]{3,16}")) {
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        UUID uuid = UUIDFetcher.getUUID(args[1]);
                                        Main.getDbManager().whitelist(uuid, null);
                                        CacheManager.addWhitelist(uuid);
                                        p.sendMessage(Main.c("Whitelist","Player whitelisted."));

                                    }
                                }.runTaskAsynchronously(Main.getInstance());
                            } else {
                                p.sendMessage(Main.c("Whitelist","Invalid syntax. Correct syntax: &d/whitelist add [username]"));
                            }
                            break;
                        case "remove":
                            if (args[1].matches("[0-9A-Za-z_]{3,16}")) {
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        UUID uuid = UUIDFetcher.getUUID(args[1]);
                                        Main.getDbManager().removeWhitelist(uuid);
                                        CacheManager.removeWhitelist(uuid);
                                        p.sendMessage(Main.c("Whitelist","Player remove from whitelist."));

                                    }
                                }.runTaskAsynchronously(Main.getInstance());
                            } else {
                                p.sendMessage(Main.c("Whitelist","Invalid syntax. Correct syntax: &d/whitelist remove [username]"));
                            }
                            break;
                        default:

                            break;
                    }
                } else {
                    p.sendMessage(Main.c("Whitelist","Whitelist related sub-commands:\n" +
                            "&d/whitelist add [player]&r - Add a player to the whitelist.\n" +
                            "&d/whitelist remove [player]&r - Remove a player from the whitelist."));
                }
            } else {
                p.sendMessage(Main.c("Whitelist","You do not have permission to execute this command."));
            }
        }
        return true;
    }
}
