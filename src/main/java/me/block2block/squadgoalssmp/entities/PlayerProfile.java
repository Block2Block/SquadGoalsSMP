package me.block2block.squadgoalssmp.entities;

import me.block2block.squadgoalssmp.CacheManager;
import me.block2block.squadgoalssmp.Main;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

public class PlayerProfile {

    private long balance;
    private Player player;
    private Team team;
    private Map<String, Location> homes;

    public PlayerProfile(Player p) {
        p.sendMessage(Main.c("Stats Manager", "Loading profile..."));
        player = p;
        new BukkitRunnable() {
            @Override
            public void run() {
                balance = Main.getDbManager().getBalance(p.getUniqueId());
                int teamId = Main.getDbManager().getTeam(p.getUniqueId());
                if (teamId == -1) {
                    team = null;
                } else {
                    if (CacheManager.getTeams().containsKey(teamId)) {
                        team = CacheManager.getTeams().get(teamId);
                    } else {
                        team = Main.getDbManager().getTeam(teamId);
                        CacheManager.addTeam(team);
                    }
                }

                homes = Main.getDbManager().getPlayerHomes(p.getUniqueId());

                p.sendMessage(Main.c("Stats Manager", "Profile successfully loaded."));
            }
        }.runTaskAsynchronously(Main.getInstance());

    }

    public long getBalance() {
        return balance;
    }

    public void removeBalance(long amount) {
        balance -= amount;
        player.sendMessage(Main.c("Economy", "&d" + amount + " Coins&r have been removed from your account."));
        new BukkitRunnable() {
            @Override
            public void run() {
                Main.getDbManager().removeBalance(player.getUniqueId(), amount);
            }
        }.runTaskAsynchronously(Main.getInstance());
    }

    public void addBalance(long amount) {
        balance += amount;
        player.sendMessage(Main.c("Economy", "&d" + amount + " Coins&r have been added to your account."));
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

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Map<String, Location> getHomes() {
        return homes;
    }
}
