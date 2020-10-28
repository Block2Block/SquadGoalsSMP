package me.block2block.squadgoalssmp.commands;

import me.block2block.squadgoalssmp.CacheManager;
import me.block2block.squadgoalssmp.Main;
import me.block2block.squadgoalssmp.entities.TPARequest;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandTPA implements CommandExecutor {


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String aliasUsed, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length == 1) {
                Player target = Bukkit.getPlayer(args[0]);
                if (target != null) {
                    for (TPARequest tpaRequest : CacheManager.getTpaRequests()) {
                        if (tpaRequest.getRequestee().equals(p) || tpaRequest.getRequester().equals(p)) {
                            p.sendMessage(Main.c("Teleport", "You cannot send a new TPA request while one is pending."));
                        }
                    }
                    CacheManager.getTpaRequests().add(new TPARequest(p, target));
                } else {
                    p.sendMessage(Main.c("Teleport", "No match found for [&d" + args[0] + "&r]"));
                }
            } else {
                p.sendMessage(Main.c("Teleport", "Invalid syntax. Correct syntax: &d/tpa [player]"));
            }
        }
        return true;
    }
}
