package me.block2block.squadgoalssmp.listeners;

import me.block2block.squadgoalssmp.CacheManager;
import me.block2block.squadgoalssmp.Main;
import me.block2block.squadgoalssmp.entities.EconomyItem;
import me.block2block.squadgoalssmp.entities.Transaction;
import me.block2block.squadgoalssmp.utils.DiscordUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
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
            if (CacheManager.getTransaction(e.getPlayer().getUniqueId()).getStage() == 1) {
                if (e.getMessage().matches("[A-Za-z_]+")) {
                    if (e.getMessage().equalsIgnoreCase("cancel")) {
                        CacheManager.transactionComplete(e.getPlayer().getUniqueId());
                        e.getPlayer().sendMessage(Main.c("Economy","You have cancelled buying an item."));
                        e.setCancelled(true);
                        return;
                    }
                    Material material = Material.matchMaterial(e.getMessage());
                    if (material == null) {
                        e.getPlayer().sendMessage(Main.c("Economy","That is not a valid item. Please try again. If you wish to cancel, type 'cancel'."));
                        e.setCancelled(true);
                        return;
                    }

                    EconomyItem item = CacheManager.getItems().get(material);
                    if (item == null) {
                        e.getPlayer().sendMessage(Main.c("Economy","That item is not buy-able. Please try another item. If you wish to cancel, type 'cancel'."));
                        e.setCancelled(true);
                        return;
                    }

                    if (CacheManager.getProfile(e.getPlayer().getUniqueId()).getBalance() >= item.getBuyPrice()) {
                        CacheManager.getTransaction(e.getPlayer().getUniqueId()).setItem(item);
                        e.getPlayer().sendMessage(Main.c("Economy", "How many would you like to buy? Type 'cancel' to cancel."));
                        CacheManager.getTransaction(e.getPlayer().getUniqueId()).nextStage();
                    } else {
                        e.getPlayer().sendMessage(Main.c("Economy", "You have insufficient funds to buy any " + item.getMaterialName() + "s. You need at least &d" + item.getBuyPrice() + " Squad Bucks&r. Please try another item. If you wish to cancel, type 'cancel'."));
                        e.setCancelled(true);
                        return;
                    }
                } else {
                    e.getPlayer().sendMessage(Main.c("Economy","That is not a valid material."));
                    e.setCancelled(true);
                    return;
                }
            } else {
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
                    return;

                } else if (e.getMessage().toLowerCase().equals("cancel")) {
                    CacheManager.transactionComplete(e.getPlayer().getUniqueId());
                    e.getPlayer().sendMessage(Main.c("Economy","You have cancelled buying an item."));
                    e.setCancelled(true);
                    return;
                }
            }
        }

        e.setCancelled(true);
        if (CacheManager.isTeamChat(e.getPlayer())) {
            CacheManager.getProfile(e.getPlayer().getUniqueId()).getTeam().chat(e.getPlayer(), e.getMessage());
            Bukkit.getLogger().info("[Team Chat] " + ((e.getPlayer().isOp())?"&d&l✦ ":"") + e.getPlayer().getName() + ": " + e.getMessage());
            DiscordUtil.teamChat(e.getMessage(), e.getPlayer());
        } else {
            if (CacheManager.getProfile(e.getPlayer().getUniqueId()).getTeam() == null) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendMessage(Main.c(null,  ((e.getPlayer().isOp())?"&d&l✦ ":"") + "&7" + e.getPlayer().getName() + "&r: " + e.getMessage()));
                }
                Bukkit.getLogger().info(e.getPlayer().getName() + ": " + e.getMessage());
                DiscordUtil.chat(e.getMessage(), e.getPlayer());
            } else {
                if (ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&',CacheManager.getProfile(e.getPlayer().getUniqueId()).getTeam().getPrefix())).equalsIgnoreCase("")) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.sendMessage(Main.c(null,  ((e.getPlayer().isOp())?"&d&l✦ ":"") + "&" + CacheManager.getProfile(e.getPlayer().getUniqueId()).getTeam().getColor().getChar() + e.getPlayer().getName() + "&r: " + e.getMessage()));
                    }
                    Bukkit.getLogger().info(e.getPlayer().getName() + ": " + e.getMessage());
                    DiscordUtil.chat(e.getMessage(), e.getPlayer());
                } else {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.sendMessage(Main.c(null, ((e.getPlayer().isOp())?"&d&l✦ ":"") + CacheManager.getProfile(e.getPlayer().getUniqueId()).getTeam().getPrefix() + " &" + CacheManager.getProfile(e.getPlayer().getUniqueId()).getTeam().getColor().getChar() + e.getPlayer().getName() + "&r: " + e.getMessage()));
                    }
                    Bukkit.getLogger().info(e.getPlayer().getName() + ": " + e.getMessage());
                    DiscordUtil.chat(e.getMessage(), e.getPlayer());
                }
            }
        }
    }


}
