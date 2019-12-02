package me.block2block.squadgoalssmp.commands;

import me.block2block.squadgoalssmp.CacheManager;
import me.block2block.squadgoalssmp.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandBalance implements CommandExecutor {


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            p.sendMessage(Main.c("Economy", "Balance: &d" + CacheManager.getProfile(p.getUniqueId()).getBalance() + " Squad Bucks&r."));
        }
        return true;
    }
}
