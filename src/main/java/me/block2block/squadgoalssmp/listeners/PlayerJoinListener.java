package me.block2block.squadgoalssmp.listeners;

import me.block2block.squadgoalssmp.CacheManager;
import me.block2block.squadgoalssmp.Main;
import me.block2block.squadgoalssmp.entities.PlayerProfile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;

import java.util.UUID;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onConnection(AsyncPlayerPreLoginEvent e) {
        if (!CacheManager.isWhitelisted(e.getUniqueId())) {
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, "You are not whitelisted on this server!");
        } else {
            e.allow();
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (CacheManager.isPurge()) {
            Player p = e.getPlayer();
            for (PotionEffect pe : p.getActivePotionEffects()) {
                if (CacheManager.getPurge().getPm().getPotionWhitelist() != null) {
                    if (!CacheManager.getPurge().getPm().getPotionWhitelist().contains(pe.getType())) {
                        p.removePotionEffect(pe.getType());
                    }
                } else if (CacheManager.getPurge().getPm().getPotionBlacklist() != null) {
                    if (CacheManager.getPurge().getPm().getPotionBlacklist().contains(pe.getType())) {
                        p.removePotionEffect(pe.getType());
                    }
                }
            }

            p.sendMessage(Main.c("Purge","There is currently a purge active. " + CacheManager.getPurge().getPm().getDescription() + " Any illegal potion effects have been removed."));
        }

        CacheManager.addProfile(e.getPlayer().getUniqueId(), new PlayerProfile(e.getPlayer()));

    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        CacheManager.removeProfile(e.getPlayer().getUniqueId());
    }

}
