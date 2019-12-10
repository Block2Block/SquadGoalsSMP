package me.block2block.squadgoalssmp.listeners;

import me.block2block.squadgoalssmp.CacheManager;
import me.block2block.squadgoalssmp.Main;
import me.block2block.squadgoalssmp.entities.EconomyItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

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
            Map<Material, Integer> items = new HashMap<>();
            StringBuilder message = new StringBuilder();
            message.append("Items sold:\n");
            for (ItemStack is : e.getInventory().getStorageContents()) {
                if (is == null) {
                    continue;
                }

                if (is.getType() == null) {
                    continue;
                }

                if (CacheManager.getItems() == null) {
                    return;
                }
                if (CacheManager.getItems().containsKey(is.getType())) {
                    int amount = is.getAmount();
                    EconomyItem ic = CacheManager.getItems().get(is.getType());
                    total += (ic.getSellPrice() * amount);
                    totalItems += amount;
                    if (items.containsKey(is.getType())) {
                        items.replace(is.getType(), items.get(is.getType()) + amount);
                    } else {
                        items.put(is.getType(), amount);
                    }
                } else {
                    Inventory playerInventory = p.getInventory();
                    p.sendMessage(Main.c("Economy", "An item you put into the inventory is not sellable."));
                    int empty = playerInventory.firstEmpty();
                    if (empty == -1) {
                        p.getWorld().dropItem(p.getLocation(), is);
                        p.sendMessage(Main.c("Economy", "There was no space in your inventory, so it was placed on the ground."));
                    } else {
                        playerInventory.setItem(empty, is);
                    }

                }
            }
            if (totalItems == 0) {
                return;
            }

            for (Material m : items.keySet()) {
                EconomyItem ic = CacheManager.getItems().get(m);
                message.append("&d" + ic.getMaterialName() + "&r - &d" + items.get(m) + "&r * &d" + ic.getSellPrice() + " Squad Bucks&r.\n");
            }
            message.append("Total Items: &d" + totalItems + "&r - Total Value: &d" + total + " Squad Bucks&r.");
            p.sendMessage(Main.c("Economy", message.toString()));
            CacheManager.getProfile(p.getUniqueId()).addBalance(total);
        }
    }

}
