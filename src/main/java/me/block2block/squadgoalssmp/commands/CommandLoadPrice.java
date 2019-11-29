package me.block2block.squadgoalssmp.commands;

import me.block2block.squadgoalssmp.CacheManager;
import me.block2block.squadgoalssmp.Main;
import me.block2block.squadgoalssmp.entities.EconomyItem;
import me.block2block.squadgoalssmp.entities.EconomySign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandLoadPrice implements CommandExecutor {


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (p.isOp()) {
                Main.getInstance().reloadConfig();
                for (EconomyItem item : CacheManager.getItems().values()) {
                    item.update();
                }
                for (EconomySign sign : CacheManager.getSigns().values()) {
                    sign.update();
                }
                p.sendMessage(Main.c("Economy","All economy signs and prices have been updated."));
            } else {
                p.sendMessage(Main.c("Command Manager","You are not allowed to use this command."));
            }
        }
        return true;
    }
}
