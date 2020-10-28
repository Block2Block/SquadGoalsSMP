package me.block2block.squadgoalssmp.entities;

import me.block2block.squadgoalssmp.CacheManager;
import me.block2block.squadgoalssmp.Main;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class TPARequest {

    private final Player requester;
    private final Player requestee;
    private final BukkitTask expiry;

    public TPARequest(Player requester, Player requestee) {
        this.requestee = requestee;
        this.requester = requester;
        TPARequest request = this;
        requestee.sendMessage(Main.c("Teleport", "&d" + requester.getName() + "&r has requested to teleport to you!\n" +
                "&d/tpaccept&r to accept.\n" +
                "&d/tpdeny&r to deny.\n" +
                "The request expires in &d2 minutes&r."));
        requester.sendMessage(Main.c("Teleport", "You have sent a TPA request to &d" + requestee.getName() + "&r."));
        this.expiry = new BukkitRunnable(){
            @Override
            public void run() {
                requestee.sendMessage(Main.c("Teleport", "&d" + requester.getName() + "'s&r TPA request expired."));
                requester.sendMessage(Main.c("Teleport", "Your TPA request to &d" + requestee.getName() + "&r expired."));
                CacheManager.getTpaRequests().remove(request);
            }
        }.runTaskLater(Main.getInstance(), 2400);
    }

    public void accepted() {
        CacheManager.getTpaRequests().remove(this);
        requester.sendMessage(Main.c("Teleport", "Your TPA request was accepted. Please do not move. You will be teleported in &d5 seconds&r."));
        requestee.sendMessage(Main.c("Teleport", "You accepted &d" + requester.getName() + "'s&r TPA request. They will be teleported in 5 seconds."));
        CacheManager.getTeleports().put(requester, new BukkitRunnable() {
            @Override
            public void run() {
                requester.teleport(requestee.getLocation());
                requester.sendMessage(Main.c("Teleport", "You have been teleported to &d" + requestee.getName() + "&r."));
                requestee.sendMessage(Main.c("Teleport", "&d" + requester.getName() + "&r was teleported to you."));
                CacheManager.getTeleports().remove(requester);
            }
        }.runTaskLater(Main.getInstance(), 100));
        if (expiry != null) {
            expiry.cancel();
        }
    }

    public void denied() {
        CacheManager.getTpaRequests().remove(this);
        requester.sendMessage(Main.c("Teleport", "Your TPA request was denied."));
        requestee.sendMessage(Main.c("Teleport", "You denied &d" + requester.getName() + "'s&r TPA request."));
        if (expiry != null) {
            expiry.cancel();
        }
    }

    public void left() {
        CacheManager.getTpaRequests().remove(this);
        if (expiry != null) {
            expiry.cancel();
        }
    }

    public Player getRequester() {
        return requester;
    }

    public Player getRequestee() {
        return requestee;
    }
}
