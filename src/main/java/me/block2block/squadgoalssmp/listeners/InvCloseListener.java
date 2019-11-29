package me.block2block.squadgoalssmp.listeners;

import com.sun.org.apache.xpath.internal.operations.String;
import me.block2block.squadgoalssmp.CacheManager;
import me.block2block.squadgoalssmp.Main;
import me.block2block.squadgoalssmp.entities.EconomyItem;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InvCloseListener implements Listener {

    @EventHandler
    public void onInvClose(InventoryCloseEvent e) {
        if (e.getPlayer() instanceof Player) {
            Player p = (Player) e.getPlayer();
            if (e.getInventory() == null) {
                return;
            }
            if (e.getView().getTitle() == null || e.getView().getTitle().equals("")) {
                return;
            }
            if (!ChatColor.stripColor(e.getView().getTitle()).equals("Please put any items you wish to sell here!") && e.getInventory().getHolder() != null) {
                return;
            }

            long total = 0;
            long totalItems = 0;
            StringBuilder message = new StringBuilder();
            message.append("Items sold:\n");
            for (ItemStack is : e.getInventory().getStorageContents()) {
                if (CacheManager.getItems().containsKey(is.getType())) {
                    int amount = is.getAmount();
                    EconomyItem ic = CacheManager.getItems().get(is.getType());
                    total += (ic.getSellPrice() * amount);
                    totalItems += amount;
                    message.append("&d" + ic.getMaterialName() + "&r - &d" + amount + "&r * &d" + ic.getSellPrice() + " Squad Bucks&r.\n");
                } else {
                    Inventory playerInventory = p.getInventory();
                    p.sendMessage(Main.c("Economy","An item you put into the inventory is not sellable."));
                    int empty = playerInventory.firstEmpty();
                    if (empty == -1) {
                        p.getWorld().dropItem(p.getLocation(), is);
                        p.sendMessage(Main.c("Economy","There was no space in your inventory, so it was placed on the ground."));
                    } else {
                        playerInventory.setItem(empty, is);
                    }

                }
            }
            message.append("Total Items: &d" + totalItems + "&r - Total Value: &d" + total + " Squad Bucks&r.");
            p.sendMessage(Main.c("Economy",message.toString()));
            CacheManager.getProfile(p.getUniqueId()).addBalance(total);
        }
    }

}
