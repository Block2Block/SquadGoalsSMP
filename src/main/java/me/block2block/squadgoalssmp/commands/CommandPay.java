package me.block2block.squadgoalssmp.commands;

import me.block2block.squadgoalssmp.CacheManager;
import me.block2block.squadgoalssmp.Main;
import me.block2block.squadgoalssmp.utils.UUIDFetcher;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CommandPay implements CommandExecutor {


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length == 2) {
                if (args[0].matches("[A-Za-z0-9_]{3,16}") && args[1].matches("[0-9]+")) {
                    long amount = Long.parseLong(args[1]);
                    if (amount > CacheManager.getProfile(p.getUniqueId()).getBalance()) {
                        p.sendMessage(Main.c("Economy", "You have insufficient funds to give that."));
                        return true;
                    }
                    if (args[0].toLowerCase().equals(p.getName().toLowerCase())) {
                        p.sendMessage(Main.c("Economy", "You cannot send money to yourself."));
                        return true;
                    }
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            UUID uuid;
                            try {
                                uuid = UUIDFetcher.getUUID(args[0]);
                            } catch (Exception e) {
                                p.sendMessage(Main.c("Economy", "That player does not exist. Please ensure you are using the most up to date username."));
                                return;
                            }
                            if (Bukkit.getOfflinePlayer(uuid).hasPlayedBefore()) {
                                if (Bukkit.getOfflinePlayer(uuid).isOnline()) {
                                    CacheManager.getProfile(uuid).addBalance(amount);
                                    Bukkit.getPlayer(uuid).sendMessage(Main.c("Economy", "You have recieved &d" + amount + " coins&r from &d" + p.getName() + "&r."));
                                    CacheManager.getProfile(p.getUniqueId()).removeBalance(amount);
                                    p.sendMessage(Main.c("Economy", "You sent &d" + amount + " coins&r to &d" + args[0] + "&r."));
                                } else {
                                    Main.getDbManager().addBalance(uuid, amount);
                                    CacheManager.getProfile(p.getUniqueId()).removeBalance(amount);
                                    p.sendMessage(Main.c("Economy", "You sent &d" + amount + " coins&r to &d" + args[0] + "&r."));
                                }
                            } else {
                                p.sendMessage(Main.c("Economy", "That player hasn't joined the server yet, so you cannot give money to them."));
                            }
                        }
                    }.runTaskAsynchronously(Main.getInstance());
                }
            } else {
                p.sendMessage(Main.c("Economy", "Invalid syntax. Correct syntax: &d/pay [user] [amount]"));
            }
        }
        return true;
    }
}
