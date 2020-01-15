package me.block2block.squadgoalssmp.listeners;

import me.block2block.squadgoalssmp.CacheManager;
import me.block2block.squadgoalssmp.Main;
import me.block2block.squadgoalssmp.entities.EconomySign;
import me.block2block.squadgoalssmp.entities.Transaction;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

public class SignClickListener implements Listener {

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        if (e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (e.getClickedBlock() == null) {
                return;
            }
            if (e.getClickedBlock().getType().name().toLowerCase().contains("sign")) {
                Sign sign = (Sign) e.getClickedBlock().getState();
                if (CacheManager.getSigns().containsKey(sign.getLocation())) {
                    if (e.getPlayer().getGameMode() == GameMode.CREATIVE && e.getAction() == Action.LEFT_CLICK_BLOCK) {
                        return;
                    }
                    EconomySign economySign = CacheManager.getSigns().get(sign.getLocation());
                    Player p = e.getPlayer();
                    switch (economySign.getType()) {
                        case 1:
                            CacheManager.addTransaction(p.getUniqueId(), new Transaction(economySign.getType(), null, p));
                            p.sendMessage(Main.c("Economy", "What item would you like to buy? Please use the Spigot API formatting. If you do not know this please Google 'Material Spigot API' and use the first link."));
                            break;
                        case 2:
                            Inventory inv = Bukkit.createInventory(null, 54, Main.c(null, "&d&lPlease put any items you wish to sell here!"));
                            p.openInventory(inv);
                            break;
                    }

                }
            }
        }
    }

}
