package me.block2block.squadgoalssmp.listeners;

import me.block2block.squadgoalssmp.CacheManager;
import me.block2block.squadgoalssmp.Main;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class BreakListener implements Listener {

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (e.getBlock().getType() == Material.BEACON) {
            if (CacheManager.isPurge()) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(Main.c("Purge","You cannot break beacons during a purge."));
            } else {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Main.getDbManager().removeBeacon(e.getBlock().getLocation());
                    }
                }.runTaskAsynchronously(Main.getInstance());
                CacheManager.removeBeacon(e.getBlock().getLocation());
            }
        } else if (e.getBlock().getType() == Material.OBSIDIAN) {
            if (CacheManager.isPurge()) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(Main.c("Purge","You cannot break obsidian during a purge."));
            }
        } else if (e.getBlock().getType().name().toLowerCase().contains("sign")) {
            if (CacheManager.getSigns().containsKey(e.getBlock().getLocation())) {
                if (e.getPlayer().isOp()) {
                    CacheManager.getSigns().remove(e.getBlock().getLocation());
                    Main.getDbManager().removeSign(e.getBlock().getLocation());
                } else {
                    e.setCancelled(true);
                }
            }
        }
    }

}
