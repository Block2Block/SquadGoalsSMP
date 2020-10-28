package me.block2block.squadgoalssmp.commands;

import me.block2block.squadgoalssmp.CacheManager;
import me.block2block.squadgoalssmp.Main;
import me.block2block.squadgoalssmp.entities.TPARequest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandTPDeny implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            for (TPARequest tpaRequest : CacheManager.getTpaRequests()) {
                if (tpaRequest.getRequestee().equals(p)) {
                    tpaRequest.denied();
                    return true;
                }
            }
            p.sendMessage(Main.c("Teleport", "You do not have a pending TPA request."));
        }
        return true;
    }
}
