package me.block2block.squadgoalssmp;

import me.block2block.squadgoalssmp.entities.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class CacheManager {

    private static Purge purge;
    private static Map<UUID, PlayerProfile> profiles = new HashMap<>();
    private static List<UUID> whitelist = new ArrayList<>();
    private static List<Location> beacons = new ArrayList<>();
    private static Map<Location, EconomySign> signs = new HashMap<>();
    private static Map<Material, EconomyItem> items = new HashMap<>();
    private static Map<UUID, Transaction> transactions = new HashMap<>();
    private static Map<Integer, Team> teams = new HashMap<>();
    private static Map<UUID, Integer> invites = new HashMap<>();
    private static Map<Player, BukkitTask> teleports = new HashMap<>();
    private static List<Player> teamChat = new ArrayList<>();
    private static Player trial;
    private static Map<Player, Boolean> votes;
    private static List<TPARequest> tpaRequests = new ArrayList<>();

    public static boolean isPurge() {
        return (purge != null);
    }

    public static Purge getPurge() {
        return purge;
    }

    public static void setPurge(Purge p) {
        purge = p;
    }

    public static PlayerProfile getProfile(UUID uuid) {
        return profiles.getOrDefault(uuid, null);
    }

    public static void addProfile(UUID uuid, PlayerProfile playerProfile) {
        profiles.put(uuid, playerProfile);
    }

    public static void removeProfile(UUID uuid) {
        profiles.remove(uuid);
    }

    public static boolean isWhitelisted(UUID uuid) {
        return whitelist.contains(uuid);
    }

    public static void setWhitelist(List<UUID> whitelist) {
        CacheManager.whitelist = whitelist;
    }

    public static void addWhitelist(UUID uuid) {
        whitelist.add(uuid);
    }

    public static void removeWhitelist(UUID uuid) {
        whitelist.remove(uuid);
    }

    public static List<Location> getBeacons() {
        return beacons;
    }

    public static void setBeacons(List<Location> beacons) {
        CacheManager.beacons = beacons;
    }

    public static void addBeacon(Location l) {
        beacons.add(l);
    }

    public static void removeBeacon(Location l) {
        beacons.remove(l);
    }


    public static Map<Location, EconomySign> getSigns() {
        return signs;
    }

    public static void addSign(EconomySign sign, Location l) {
        if (signs == null) {
            signs = new HashMap<>();
        }
        signs.put(l, sign);
    }

    public static Map<Material, EconomyItem> getItems() {
        return items;
    }

    public static void setItems(Map<Material, EconomyItem> items) {
        CacheManager.items = items;
    }

    public static boolean inTransaction(UUID uuid) {
        return transactions.containsKey(uuid);
    }

    public static void addTransaction(UUID uuid, Transaction transaction) {
        transactions.put(uuid, transaction);
    }

    public static Transaction getTransaction(UUID uuid) {
        return transactions.getOrDefault(uuid, null);
    }

    public static void transactionComplete(UUID uuid) {
        transactions.remove(uuid);
    }

    public static Map<Integer, Team> getTeams() {
        return teams;
    }

    public static void addTeam(Team team) {
        teams.put(team.getId(), team);
    }

    public static Map<UUID, Integer> getInvites() {
        return invites;
    }

    public static void addInvite(UUID uuid, int id) {
        invites.put(uuid, id);
    }

    public static boolean isInvited(UUID uuid) {
        return invites.containsKey(uuid);
    }

    public static Map<Player, BukkitTask> getTeleports() {
        return teleports;
    }

    public static void addTeleport(Player p, BukkitTask task) {
        teleports.put(p, task);
    }

    public static  boolean toggleChat(Player player) {
        if (teamChat.contains(player)) {
            teamChat.remove(player);
            return false;
        } else {
            teamChat.add(player);
            return true;
        }
    }

    public static boolean isTeamChat(Player p) {
        return teamChat.contains(p);
    }

    public static boolean isTrial() {
        return trial != null;
    }

    public static boolean isTrial(Player player)  {
        return trial.equals(player);
    }

    public static void startTrial(Player p) {
        votes = null;
        trial = p;
    }

    public static void startVoting()  {
        votes = new HashMap<>();
    }

    public static Player getDefendant() {
        return trial;
    }

    public static void addVote(Player p, boolean live) {
        votes.put(p, live);
    }

    public static Collection<Boolean> getVotes() {
        return votes.values();
    }

    public static boolean hasVoted(Player p) {
        return votes.containsKey(p);
    }

    public static boolean isVoting() {
        return votes != null;
    }

    public static void endVote() {
        votes = null;
        trial = null;
    }

    public static List<TPARequest> getTpaRequests() {
        return tpaRequests;
    }
}
