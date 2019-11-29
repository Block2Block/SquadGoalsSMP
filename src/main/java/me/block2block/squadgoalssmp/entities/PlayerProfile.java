package me.block2block.squadgoalssmp.entities;

import me.block2block.squadgoalssmp.Main;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerProfile {

    private long balance;
    private Player player;

    public PlayerProfile(Player p) {
        p.sendMessage(Main.c("Stats Manager","Loading profile..."));
        player = p;
        new BukkitRunnable() {
            @Override
            public void run() {
                balance = Main.getDbManager().getBalance(p.getUniqueId());
                p.sendMessage(Main.c("Stats Manager","Profile successfully loaded."));
            }
        }.runTaskAsynchronously(Main.getInstance());

    }

    public long getBalance() {
        return balance;
    }

    public void removeBalance(long amount) {
        balance -= amount;
        player.sendMessage(Main.c("Money","&d" + amount + " Squad Bucks&r has been removed from your account."));
        new BukkitRunnable() {
            @Override
            public void run() {
                Main.getDbManager().removeBalance(player.getUniqueId(), amount);
            }
        }.runTaskAsynchronously(Main.getInstance());
    }

    public void addBalance(long amount) {
        balance += amount;
        player.sendMessage(Main.c("Money","&d$" + amount + " Squad Bucks&r has been added to your account."));
        new BukkitRunnable() {
            @Override
            public void run() {
                Main.getDbManager().addBalance(player.getUniqueId(), amount);
            }
        }.runTaskAsynchronously(Main.getInstance());
    }

    public Player getPlayer() {
        return player;
    }
}
