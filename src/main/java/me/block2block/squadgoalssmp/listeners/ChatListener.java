package me.block2block.squadgoalssmp.listeners;

import me.block2block.squadgoalssmp.CacheManager;
import me.block2block.squadgoalssmp.Main;
import me.block2block.squadgoalssmp.entities.Transaction;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ChatListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (CacheManager.inTransaction(e.getPlayer().getUniqueId())) {
            if (e.getMessage().matches("[0-9]+")) {
                int amount;
                try {
                    amount = Integer.parseInt(e.getMessage());
                } catch (NumberFormatException ex) {
                    e.setCancelled(true);
                    e.getPlayer().sendMessage(Main.c("Economy", "You cannot buy that many items. Please try again."));
                    return;
                }

                if (amount < 1) {
                    e.getPlayer().sendMessage(Main.c("Economy", "You cannot buy less than 1 of an item. Please try again."));
                    e.setCancelled(true);
                    return;
                }

                Transaction transaction = CacheManager.getTransaction(e.getPlayer().getUniqueId());

                if (CacheManager.getProfile(e.getPlayer().getUniqueId()).getBalance() < (transaction.getItem().getBuyPrice() * amount)) {
                    e.getPlayer().sendMessage(Main.c("Economy", "You have insufficient funds to buy" + amount + " " + transaction.getItem().getMaterialName() + "s. You need at least &d" + (transaction.getItem().getBuyPrice() * amount) + " Squad Bucks&r. Please try again."));
                    e.setCancelled(true);
                    return;
                }

                e.setCancelled(true);

                CacheManager.getProfile(e.getPlayer().getUniqueId()).removeBalance(amount * transaction.getItem().getBuyPrice());
                e.getPlayer().sendMessage(Main.c("Economy", "You purchased &d" + amount + " " + transaction.getItem().getMaterialName() + "&r."));
                Inventory inv = e.getPlayer().getInventory();
                Map<Integer, ItemStack> result = inv.addItem(new ItemStack(transaction.getItem().getMaterial(), amount));
                if (result.size() > 0) {
                    for (ItemStack is : result.values()) {
                        e.getPlayer().getWorld().dropItem(e.getPlayer().getLocation(), is);
                    }
                    e.getPlayer().sendMessage(Main.c("Economy", "There was no space left in your inventory for all of the items, so the remaining were placed on the ground."));
                }

                CacheManager.transactionComplete(e.getPlayer().getUniqueId());

            } else if (e.getMessage().toLowerCase().equals("cancel")) {
                CacheManager.transactionComplete(e.getPlayer().getUniqueId());
                e.getPlayer().sendMessage(Main.c("Economy","You have cancelled buying an item."));
                e.setCancelled(true);
            }
        }
    }


}
