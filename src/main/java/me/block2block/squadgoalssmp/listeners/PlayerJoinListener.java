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
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.potion.PotionEffect;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onConnection(AsyncPlayerPreLoginEvent e) {
        if (!Main.isReady()) {
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "The server is still loading. Please try again in a few minutes.");
        }
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

            p.sendMessage(Main.c("Purge", "There is currently a purge active. " + CacheManager.getPurge().getPm().getDescription() + " Any illegal potion effects have been removed."));
        }

        CacheManager.addProfile(e.getPlayer().getUniqueId(), new PlayerProfile(e.getPlayer()));

        e.setJoinMessage(Main.c(null,"&d&l" + e.getPlayer().getName() + " joined the Squad."));
        if (!e.getPlayer().hasPlayedBefore()) {
            e.getPlayer().sendMessage(Main.c("Squad Goals SMP",
                    "&d&l&nWelcome to the Squad Goals SMP S3!\n&rWe hope you enjoy your time here! The rules " +
                            "are relatively simple. Vaults are off limits when " +
                            "not in a purge, no abusing/exploiting commands, try " +
                            "not to break the server, pranks are fine but total base " +
                            "destroying is a no go, stealing is a big no go (unless it is a purge), and have fun! \n" +
                            "If you are playing with friends, we recommend creating a team! " +
                            "Do &d/team help&r for more info!"));
        }

    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        if (CacheManager.getProfile(e.getPlayer().getUniqueId()) == null) {
            return;
        }
        e.setQuitMessage(Main.c(null,"&d&l" + e.getPlayer().getName() + " left the Squad."));
        if (CacheManager.getProfile(e.getPlayer().getUniqueId()).getTeam() != null) {
            if (CacheManager.getProfile(e.getPlayer().getUniqueId()).getTeam().isOffline()) {
                CacheManager.getTeams().remove(CacheManager.getProfile(e.getPlayer().getUniqueId()).getTeam().getId());
            }
        }
        CacheManager.removeProfile(e.getPlayer().getUniqueId());
        CacheManager.transactionComplete(e.getPlayer().getUniqueId());
        if (CacheManager.isTeamChat(e.getPlayer())) {
            CacheManager.toggleChat(e.getPlayer());
        }
        CacheManager.getTeleports().remove(e.getPlayer());
        CacheManager.getInvites().remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onReady(ServerLoadEvent e) {
        if (e.getType() == ServerLoadEvent.LoadType.STARTUP) {
            Main.ready();
        }
    }

}
