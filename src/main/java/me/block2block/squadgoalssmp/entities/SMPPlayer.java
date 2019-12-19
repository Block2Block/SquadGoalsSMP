package me.block2block.squadgoalssmp.entities;

import me.block2block.squadgoalssmp.Main;
import me.block2block.squadgoalssmp.utils.NameFetcher;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class SMPPlayer {

    private OfflinePlayer player;
    private UUID uuid;
    private String name;

    public SMPPlayer(OfflinePlayer player) {
        this.uuid = player.getUniqueId();
        this.player = player;
        if (player.isOnline()) {
            this.name = player.getName();
        } else {
            new BukkitRunnable() {
                @Override
                public void run() {
                    name = NameFetcher.getName(uuid);
                }
            }.runTaskAsynchronously(Main.getInstance());
        }
    }

    public String getName() {
        if (player.isOnline()) {
            return player.getName();
        }
        return name;
    }

    public OfflinePlayer getPlayer() {
        return player;
    }

    public UUID getUuid() {
        return uuid;
    }
}
