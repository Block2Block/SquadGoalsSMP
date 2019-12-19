package me.block2block.squadgoalssmp.listeners;

import me.block2block.squadgoalssmp.CacheManager;
import me.block2block.squadgoalssmp.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MoveListener implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (e.getTo() == null) {
            return;
        }
        if (!e.getFrom().getBlock().equals(e.getTo().getBlock())) {
            if (CacheManager.getTeleports().containsKey(e.getPlayer())) {
                e.getPlayer().sendMessage(Main.c("Teams","You moved! Teleportation cancelled."));
                CacheManager.getTeleports().get(e.getPlayer()).cancel();
                CacheManager.getTeleports().remove(e.getPlayer());
            }
        }
    }

}
