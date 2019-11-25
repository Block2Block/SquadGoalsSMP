package me.block2block.squadgoalssmp.entities;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public enum PurgeMode {

    NORMAL(1, "Normal", null,null, null, null, null, null, null, null, "This is a normal purge. All weapons, armor, enchantments and potions are enabled!", true);

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

    PurgeMode(int id, String name, List<Material> weaponBlacklist, List<Material> weaponWhitelist, List<Material> armorBlacklist, List<Material> armorWhitelist, List<PotionEffectType> potionWhitelist, List<PotionEffectType> potionBlacklist, List<Enchantment> enchantmentWhitelist,List<Enchantment> enchantmentBlacklist, String description, boolean gapplesEnabled) {
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
