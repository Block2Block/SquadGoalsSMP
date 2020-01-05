package me.block2block.squadgoalssmp.entities;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.bukkit.Material.*;

public enum PurgeMode {

    NORMAL(1, "Normal", null, null, null, null, null, null, null, null, "This is a normal purge. All weapons, armor, enchantments and potions are enabled!", true),
    BLOODMOON(2, "Blood Moon", null, null, null, null, null, null, null, null, "This is a Blood Moon purge. All hostile mob spawning has been turned up, spawned mobs are stronger, and it will remain night-time until the purge ends!", true),
    ZOMBIEAPOCALYPSE(3, "Zombie Apocalypse", null, null, null, null, null, null, null, null, "This is a Zombie Apocalypse purge. Uh oh! Walkers have been released into the wild! Zombie spawn-rates have been turned up, and zombies are stronger! It will remain night-time until the purge ends!", true),
    BASIC(4, "Basic", null, new ArrayList<>(Arrays.asList(WOODEN_SWORD, BOW)), null, new ArrayList<>(Arrays.asList(LEATHER_BOOTS, LEATHER_CHESTPLATE, LEATHER_LEGGINGS, LEATHER_HELMET)), new ArrayList<>(), null, new ArrayList<>(), null, "This is a basic purge, all armor (except leather), weapons (except wooden swords & bows), enchantments, potion effects and Gapples have been disabled!", false),
    NOENCHANT(5, "No Enchant", null, null, null, null, null, null, new ArrayList<>(), null, "This is a No Enchant purge. All enchantments have been disabled!", true),
    NOPOTION(6, "No Potions", null, null, null, null, new ArrayList<>(), null, null, null, "This is a No Potion purge. All potions and potion effects have been disabled!", true),
    DIAMONDONLY(7, "All The Diamonds", null, new ArrayList<>(Arrays.asList(DIAMOND_SWORD, BOW)), null, new ArrayList<>(Arrays.asList(DIAMOND_CHESTPLATE, DIAMOND_HELMET, DIAMOND_LEGGINGS, DIAMOND_BOOTS)), null, null, null, null, "This is an All The Diamonds purge. You can only use Diamond Armor and a Diamond Sword! All potions and Enchantments are still enabled!", true),
    NOARMOR(8, "No Armor", null, null, null, new ArrayList<>(), null, null, null, null, "This is an No Armor purge. All armor is disabled! You must fight unprotected!", true),
    FISTFIGHT(9, "Fist Fight", null, new ArrayList<>(), null, new ArrayList<>(), null, null, null, null, "This is a Fist Fight purge! All weapons and armor are disabled!", true);

    private int id;
    private String name;
    private String description;
    private List<Material> weaponBlacklist;
    private List<Material> weaponWhitelist;
    private List<PotionEffectType> potionWhitelist;
    private List<PotionEffectType> potionBlacklist;
    private List<Material> armorWhitelist;
    private List<Material> armorBlacklist;
    private List<Enchantment> enchantmentWhitelist;
    private List<Enchantment> enchantmentBlacklist;
    private boolean gapplesEnabled;

    PurgeMode(int id, String name, List<Material> weaponBlacklist, List<Material> weaponWhitelist, List<Material> armorBlacklist, List<Material> armorWhitelist, List<PotionEffectType> potionWhitelist, List<PotionEffectType> potionBlacklist, List<Enchantment> enchantmentWhitelist, List<Enchantment> enchantmentBlacklist, String description, boolean gapplesEnabled) {
        this.id = id;
        this.name = name;
        this.weaponBlacklist = weaponBlacklist;
        this.weaponWhitelist = weaponWhitelist;
        this.armorBlacklist = armorBlacklist;
        this.armorWhitelist = armorWhitelist;
        this.potionBlacklist = potionBlacklist;
        this.potionWhitelist = potionWhitelist;
        this.enchantmentBlacklist = enchantmentBlacklist;
        this.enchantmentWhitelist = enchantmentWhitelist;

        this.description = description;
        this.gapplesEnabled = gapplesEnabled;
    }

    public static PurgeMode getByID(int id) {
        for (PurgeMode p : PurgeMode.values()) {
            if (p.id == id) {
                return p;
            }
        }
        return null;
    }

    public static PurgeMode getByName(String name) {
        for (PurgeMode p : PurgeMode.values()) {
            if (p.name().toLowerCase().equals(name.toLowerCase())) {
                return p;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public List<Enchantment> getEnchantmentBlacklist() {
        return enchantmentBlacklist;
    }

    public List<Enchantment> getEnchantmentWhitelist() {
        return enchantmentWhitelist;
    }

    public List<Material> getArmorBlacklist() {
        return armorBlacklist;
    }

    public List<Material> getArmorWhitelist() {
        return armorWhitelist;
    }

    public List<Material> getWeaponBlacklist() {
        return weaponBlacklist;
    }

    public List<Material> getWeaponWhitelist() {
        return weaponWhitelist;
    }

    public List<PotionEffectType> getPotionBlacklist() {
        return potionBlacklist;
    }

    public List<PotionEffectType> getPotionWhitelist() {
        return potionWhitelist;
    }

    public boolean areGapplesEnabled() {
        return gapplesEnabled;
    }
}
