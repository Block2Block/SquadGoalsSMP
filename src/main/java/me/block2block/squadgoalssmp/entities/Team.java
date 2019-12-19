package me.block2block.squadgoalssmp.entities;

import me.block2block.squadgoalssmp.CacheManager;
import me.block2block.squadgoalssmp.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class Team {

    private int id;
    private String name;
    private String prefix;
    private SMPPlayer leader;
    private List<SMPPlayer> members;
    private long bank;
    private org.bukkit.scoreboard.Team team;
    private Location home;
    private ChatColor color;

    public Team(int id, String name, String prefix, SMPPlayer leader, List<SMPPlayer> players, long bank, Location home, String color) {
        this.id = id;
        this.name = name;
        this.prefix = prefix;
        this.leader = leader;
        this.members = players;
        this.bank = bank;
        this.home = home;
        this.color = ChatColor.valueOf(color);

        assert Bukkit.getScoreboardManager() != null;
        if (Bukkit.getScoreboardManager().getMainScoreboard().getTeam(name) == null) {
            team = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam(name);
            team.addPlayer(leader.getPlayer());
            for (SMPPlayer member : members) {
                team.addPlayer(member.getPlayer());
            }
        } else {
            team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(name);
        }

        if (!prefix.equals("")) {
            team.setPrefix(ChatColor.translateAlternateColorCodes('&', prefix + " "));
        }

        team.setColor(this.color);
    }

    public long getBalance() {
        return bank;
    }

    public void addBalance(int i) {
        bank += i;
        for (SMPPlayer member : members) {
            if (member.getPlayer().isOnline()) {
                Player p = Bukkit.getPlayer(member.getUuid());
                assert p != null;
                p.sendMessage(Main.c("Teams","&d" + i + " Squad Bucks&r have been deposited into the team bank."));
            }
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                Main.getDbManager().addTeamBalance(id, i);
            }
        }.runTaskAsynchronously(Main.getInstance());
    }

    public void removeBalance(int i) {
        bank -= i;
        for (SMPPlayer member : members) {
            if (member.getPlayer().isOnline()) {
                Player p = Bukkit.getPlayer(member.getUuid());
                assert p != null;
                p.sendMessage(Main.c("Teams","&d" + i + " Squad Bucks&r have been withdrawn from the team bank."));
            }
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                Main.getDbManager().removeTeamBalance(id, i);
            }
        }.runTaskAsynchronously(Main.getInstance());
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        team.setPrefix(ChatColor.translateAlternateColorCodes('&',prefix + " "));
        this.prefix = prefix;

        new BukkitRunnable() {
            @Override
            public void run() {
                Main.getDbManager().setPrefix(id, prefix);
            }
        }.runTaskAsynchronously(Main.getInstance());
    }

    public SMPPlayer getLeader() {
        return leader;
    }

    public void setLeader(SMPPlayer leader) {
        this.leader = leader;
        for (SMPPlayer player : members) {
            if (player.getPlayer().isOnline()) {
                Player p = Bukkit.getPlayer(player.getUuid());
                assert p != null;
                p.sendMessage(Main.c("Teams","Team Leadership has been transfered to &d" + leader.getName() + "&r."));
            }
        }
        Main.getDbManager().setLeader(id, leader);

    }

    public boolean isMember(Player p) {
        if (CacheManager.getProfile(p.getUniqueId()).getTeam().getId() == id) {
            return true;
        }
        return false;
    }

    public SMPPlayer getMember(OfflinePlayer p) {
        for (SMPPlayer member : members) {
            if (member.getUuid().equals(p.getUniqueId())) {
                return member;
            }
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public void addMember(SMPPlayer p) {
        members.add(p);
        for (SMPPlayer player : members) {
            if (player.getPlayer().isOnline()) {
                Player pl = Bukkit.getPlayer(player.getUuid());
                assert pl != null;
                pl.sendMessage(Main.c("Teams","&d" + p.getName() + "&r has joined the team."));
            }
        }
        team.addPlayer(p.getPlayer());
        new BukkitRunnable() {
            @Override
            public void run() {
                Main.getDbManager().addMemeber(id, p);
            }
        }.runTaskAsynchronously(Main.getInstance());
    }

    public void removeMember(OfflinePlayer p) {
        if (getMember(p) == null) {
            return;
        }
        for (SMPPlayer player : members) {
            if (player.getPlayer().isOnline()) {
                Player pl = Bukkit.getPlayer(player.getUuid());
                assert pl != null;
                pl.sendMessage(Main.c("Teams","&d" + getMember(p).getName() + "&r has left the team."));
            }
        }

        members.remove(getMember(p));
        team.removePlayer(p);
        new BukkitRunnable() {
            @Override
            public void run() {
                Main.getDbManager().removeMember(id, p);
            }
        }.runTaskAsynchronously(Main.getInstance());
    }

    public String getName() {
        return name;
    }

    public List<SMPPlayer> getMembers() {
        return members;
    }

    public Location getHome() {
        return home;
    }

    public void setHome(Location home) {
        this.home = home;
        new BukkitRunnable() {
            @Override
            public void run() {
                Main.getDbManager().setHome(id, home);
            }
        }.runTaskAsynchronously(Main.getInstance());
    }

    public void disband() {
        CacheManager.getTeams().remove(id);
        new BukkitRunnable() {
            @Override
            public void run() {
                Main.getDbManager().disband(id);
            }
        }.runTaskAsynchronously(Main.getInstance());
        team.unregister();
        CacheManager.getProfile(leader.getUuid()).addBalance(bank);
    }

    public boolean isOffline() {
        for (SMPPlayer member : members) {
            if (member.getPlayer().isOnline()) {
                return false;
            }
        }
        return true;
    }

    public void setColor(ChatColor color) {
        this.color = color;
        team.setColor(color);
        new BukkitRunnable() {
            @Override
            public void run() {
                Main.getDbManager().setColor(id, color);
            }
        }.runTaskAsynchronously(Main.getInstance());
    }

    public ChatColor getColor() {
        return color;
    }

    public void chat(Player p, String message) {
        for (SMPPlayer player : members) {
            if (player.getPlayer().isOnline()) {
                Player pl = Bukkit.getPlayer(player.getUuid());
                assert pl != null;
                pl.sendMessage(Main.c(null,"&d[Team Chat] " + prefix + " &" + color.getChar() + p.getName() + "&r: " + message));
            }
        }
    }
}
