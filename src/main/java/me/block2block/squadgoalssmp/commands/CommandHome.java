package me.block2block.squadgoalssmp.commands;

import me.block2block.squadgoalssmp.CacheManager;
import me.block2block.squadgoalssmp.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class CommandHome implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String aliasUsed, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length > 0) {
                switch (args[0].toLowerCase()) {
                    case "set":
                        if (args.length != 2) {
                            p.sendMessage(Main.c("Home", "Invalid syntax. Correct syntax: &d/home set [name]"));
                            return true;
                        }

                        if (CacheManager.getProfile(p.getUniqueId()).getHomes().containsKey(args[1].toLowerCase())) {
                            p.sendMessage(Main.c("Home", "You already have a home called &d" + args[1].toLowerCase() + "&r."));
                            return true;
                        }

                        if (CacheManager.getProfile(p.getUniqueId()).getHomes().size() >= 5) {
                            p.sendMessage(Main.c("Home", "You already have 5 homes. To delete a home, use &d/home delete [name]&r."));
                            return true;
                        }

                        if (args[1].equalsIgnoreCase("delete") || args[1].equalsIgnoreCase("list") || args[1].equalsIgnoreCase("set")) {
                            p.sendMessage(Main.c("Home", "You cannot call your home that."));
                            return true;
                        }

                        CacheManager.getProfile(p.getUniqueId()).getHomes().put(args[1].toLowerCase(), p.getLocation().clone());
                        new BukkitRunnable(){
                            @Override
                            public void run() {
                                Main.getDbManager().setPlayerHome(p.getUniqueId(), args[1].toLowerCase(), p.getLocation().clone());
                            }
                        }.runTaskAsynchronously(Main.getInstance());
                        p.sendMessage(Main.c("Home", "Home &d" + args[1].toLowerCase() + "&r successfully set."));
                        break;
                    case "delete":
                        if (args.length != 2) {
                            p.sendMessage(Main.c("Home", "Invalid syntax. Correct syntax: &d/home delete [name]"));
                            return true;
                        }

                        if (!CacheManager.getProfile(p.getUniqueId()).getHomes().containsKey(args[1].toLowerCase())) {
                            p.sendMessage(Main.c("Home", "You do not have a home called &d" + args[1].toLowerCase() + "&r."));
                            return true;
                        }

                        CacheManager.getProfile(p.getUniqueId()).getHomes().remove(args[1].toLowerCase());
                        new BukkitRunnable(){
                            @Override
                            public void run() {
                                Main.getDbManager().deleteHome(p.getUniqueId(), args[1].toLowerCase());
                            }
                        }.runTaskAsynchronously(Main.getInstance());
                        p.sendMessage(Main.c("Home", "Home &d" + args[1].toLowerCase() + "&r successfully deleted."));
                        break;
                    case "list":
                        if (CacheManager.getProfile(p.getUniqueId()).getHomes().size() == 0) {
                            p.sendMessage(Main.c("Home", "You currently do not have any homes set."));
                        } else {
                            p.sendMessage(Main.c("Home", "You currently have &d" + CacheManager.getProfile(p.getUniqueId()).getHomes().size() + "&r/&d5&r homes set:\n" +
                                    "&d" + String.join("&r, &d", CacheManager.getProfile(p.getUniqueId()).getHomes().keySet()) + "&r."));
                        }
                        break;
                    default:
                        if (args.length > 1) {
                            p.sendMessage(Main.c("Home", "Available sub-commands:\n" +
                                    "&d/home set [name]&r - Set a home.\n" +
                                    "&d/home delete [name]&r - Delete a home.\n" +
                                    "&d/home list&r - list your homes.\n" +
                                    "&d/home [name]&r - Teleport to a home."));
                            return true;
                        }
                        if (CacheManager.getTeleports().containsKey(p)) {
                            p.sendMessage(Main.c("Home", "You already have a pending teleport request, please wait till it expires before teleporting again!"));
                            return true;
                        }
                        if (!CacheManager.getProfile(p.getUniqueId()).getHomes().containsKey(args[0].toLowerCase())) {
                            p.sendMessage(Main.c("Home", "You do not have a home called &d" + args[0].toLowerCase() + "&r."));
                            return true;
                        }
                        p.sendMessage(Main.c("Home", "Please do not move. You will be teleported in &d5 seconds&r."));
                        CacheManager.getTeleports().put(p, new BukkitRunnable() {
                            @Override
                            public void run() {
                                p.teleport(CacheManager.getProfile(p.getUniqueId()).getHomes().get(args[0].toLowerCase()));
                                p.sendMessage(Main.c("Home", "You have been teleported to your &d" + args[0].toLowerCase() + "&r home."));
                                CacheManager.getTeleports().remove(p);
                            }
                        }.runTaskLater(Main.getInstance(), 100));
                }
            } else {
                p.sendMessage(Main.c("Home", "Available sub-commands:\n" +
                        "&d/home set [name]&r - Set a home.\n" +
                        "&d/home delete [name]&r - Delete a home.\n" +
                        "&d/home list&r - list your homes.\n" +
                        "&d/home [name]&r - Teleport to a home."));
            }
        }
        return true;
    }
}
