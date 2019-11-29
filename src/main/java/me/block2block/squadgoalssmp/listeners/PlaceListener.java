package me.block2block.squadgoalssmp.listeners;

import me.block2block.squadgoalssmp.CacheManager;
import me.block2block.squadgoalssmp.Main;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlaceListener implements Listener {

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        if (e.getBlock().getType() == Material.BEACON) {
            if (CacheManager.isPurge()) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(Main.c("Purge","You cannot place beacons during a purge."));
            } else {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Main.getDbManager().addBeacon(e.getBlock().getLocation());
                    }
                }.runTaskAsynchronously(Main.getInstance());
                CacheManager.addBeacon(e.getBlock().getLocation());
            }
        }
    }

}
