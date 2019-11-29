package me.block2block.squadgoalssmp;

import me.block2block.squadgoalssmp.entities.*;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.*;

public class CacheManager {

    private static Purge purge;
    private static Map<UUID, PlayerProfile> profiles = new HashMap<>();
    private static List<UUID> whitelist = new ArrayList<>();
    private static List<Location> beacons = new ArrayList<>();
    private static Map<Location, EconomySign> signs = new HashMap<>();
    private static Map<Material, EconomyItem> items = new HashMap<>();
    private static Map<UUID, Transaction> transactions = new HashMap<>();

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

    public static void setItems(Map<Material, EconomyItem> items) {
        CacheManager.items = items;
    }

    public static Map<Material, EconomyItem> getItems() {
        return items;
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
}
