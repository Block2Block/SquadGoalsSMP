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
                Material material = Material.matchMaterial(e.getLines()[1]);
                if (material == null && !e.getLines()[0].equals("[Sell]")) {
                    sign.setLine(0, Main.c(null,"&4&lInvalid"));
                    sign.setLine(1, Main.c(null,"&4&lMaterial"));
                    sign.update(true);
                } else if (material == null) {
                    EconomySign economySign = new EconomySign(e.getBlock().getLocation(), null, 2);
                    CacheManager.addSign(economySign, e.getBlock().getLocation());
                    economySign.update();
                    new BukkitRunnable(){
                        @Override
                        public void run() {
                            Main.getDbManager().addSign(e.getBlock().getLocation(), 2, null);
                        }
                    }.runTaskAsynchronously(Main.getInstance());
                } else {
                    if (CacheManager.getItems().containsKey(material)) {
                        int type = ((e.getLines()[0].equals("[Buy]"))?1:2);
                        EconomyItem item = CacheManager.getItems().get(material);
                        EconomySign economySign = new EconomySign(e.getBlock().getLocation(), item, type);
                        economySign.update();
                        CacheManager.addSign(economySign, e.getBlock().getLocation());
                        new BukkitRunnable(){
                            @Override
                            public void run() {
                                Main.getDbManager().addSign(e.getBlock().getLocation(), type, material);
                            }
                        }.runTaskAsynchronously(Main.getInstance());
                    } else {
                        sign.setLine(0, Main.c(null, "&4&lInvalid"));
                        sign.setLine(1, Main.c(null, "&4&lMaterial"));
                        sign.update(true);
                    }
                }

            }
        }
    }

}
