package me.block2block.squadgoalssmp.listeners;

import me.block2block.squadgoalssmp.CacheManager;
import me.block2block.squadgoalssmp.Main;
import me.block2block.squadgoalssmp.entities.EconomyItem;
import me.block2block.squadgoalssmp.entities.EconomySign;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class SignPlaceListener implements Listener {

    @EventHandler
    public void onPlace(SignChangeEvent e) {
        if (e.getPlayer().isOp()) {
            if (e.getLines()[0].equals("[Buy]") || e.getLines()[0].equals("[Sell]")) {
                e.setCancelled(true);
                Sign sign = (Sign) e.getBlock().getState();
                if (e.getLines()[0].equals("[Sell]")) {
                    EconomySign economySign = new EconomySign(e.getBlock().getLocation(), null, 2);
                    CacheManager.addSign(economySign, e.getBlock().getLocation());
                    economySign.update();
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            Main.getDbManager().addSign(e.getBlock().getLocation(), 2, null);
                        }
                    }.runTaskAsynchronously(Main.getInstance());
                } else {
                        int type = ((e.getLines()[0].equals("[Buy]")) ? 1 : 2);
                        EconomySign economySign = new EconomySign(e.getBlock().getLocation(), null, type);
                        economySign.update();
                        CacheManager.addSign(economySign, e.getBlock().getLocation());
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                Main.getDbManager().addSign(e.getBlock().getLocation(), type, null);
                            }
                        }.runTaskAsynchronously(Main.getInstance());
                }

            }
        }
    }

}
