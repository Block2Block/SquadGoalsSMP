package me.block2block.squadgoalssmp.listeners;

import me.block2block.squadgoalssmp.CacheManager;
import me.block2block.squadgoalssmp.Main;
import me.block2block.squadgoalssmp.entities.Purge;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;

public class ShotBowEvent implements Listener {

    @EventHandler
    public void onBowShoot(EntityShootBowEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if (CacheManager.isPurge()) {
                Purge purge = CacheManager.getPurge();
                ItemStack i = e.getBow();
                if (purge.getPm().getWeaponWhitelist() != null) {
                    if (!purge.getPm().getWeaponWhitelist().contains(Material.BOW)) {
                        e.setCancelled(true);
                        p.sendMessage(Main.c("Purge","Bows are disabled during this purge."));
                        return;
                    }
                } else if (purge.getPm().getWeaponBlacklist() != null) {
                    if (purge.getPm().getWeaponBlacklist().contains(Material.BOW)) {
                        e.setCancelled(true);
                        p.sendMessage(Main.c("Purge","Bows are disabled during this purge."));
                        return;
                    }
                }

                if (purge.getPm().getEnchantmentBlacklist() != null) {
                    if (i == null) {
                        return;
                    }
                    for (Enchantment en : i.getEnchantments().keySet()) {
                        if (purge.getPm().getEnchantmentBlacklist().contains(en)) {
                            e.setCancelled(true);
                            p.sendMessage(Main.c("Purge","Your bow has an enchantment that has been disabled."));
                            return;
                        }
                    }
                } else if (purge.getPm().getEnchantmentWhitelist() != null) {
                    if (i == null) {
                        return;
                    }
                    for (Enchantment en : i.getEnchantments().keySet()) {
                        if (!purge.getPm().getEnchantmentWhitelist().contains(en)) {
                            e.setCancelled(true);
                            p.sendMessage(Main.c("Purge","Your bow has an enchantment that has been disabled."));
                            return;
                        }
                    }
                }
            }
        }
    }

}
