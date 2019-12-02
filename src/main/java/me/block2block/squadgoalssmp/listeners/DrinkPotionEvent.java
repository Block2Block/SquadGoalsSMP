package me.block2block.squadgoalssmp.listeners;

import me.block2block.squadgoalssmp.CacheManager;
import me.block2block.squadgoalssmp.Main;
import me.block2block.squadgoalssmp.entities.Purge;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.meta.PotionMeta;

public class DrinkPotionEvent implements Listener {

    @EventHandler
    public void onDrink(PlayerItemConsumeEvent e) {
        if (CacheManager.isPurge()) {
            if (e.getItem().getType() == Material.POTION || e.getItem().getType() == Material.LINGERING_POTION || e.getItem().getType() == Material.SPLASH_POTION) {
                Purge purge = CacheManager.getPurge();
                PotionMeta pm = (PotionMeta) e.getItem().getItemMeta();
                if (pm == null) {
                    return;
                }
                if (purge.getPm().getPotionBlacklist() != null) {
                    if (purge.getPm().getPotionBlacklist().contains(pm.getBasePotionData().getType().getEffectType())) {
                        e.setCancelled(true);
                        e.getPlayer().sendMessage(Main.c("Purge", "That potion effect is disabled during this purge."));
                    }
                } else if (purge.getPm().getPotionWhitelist() != null) {
                    if (!purge.getPm().getPotionWhitelist().contains(pm.getBasePotionData().getType().getEffectType())) {
                        e.setCancelled(true);
                        e.getPlayer().sendMessage(Main.c("Purge", "That potion effect is disabled during this purge."));
                    }
                }
            } if (e.getItem().getType() == Material.GOLDEN_APPLE) {
                if (!CacheManager.getPurge().getPm().areGapplesEnabled()) {
                    e.setCancelled(true);
                    e.getPlayer().sendMessage(Main.c("Purge", "Golden apples are disabled during this purge."));
                }
            }
        }
    }

}
