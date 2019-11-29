package me.block2block.squadgoalssmp.listeners;

import me.block2block.squadgoalssmp.CacheManager;
import me.block2block.squadgoalssmp.Main;
import me.block2block.squadgoalssmp.entities.Purge;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

public class DamageListener implements Listener {

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
            if (CacheManager.isPurge()) {
                Purge purge = CacheManager.getPurge();
                Player attacker = (Player) e.getDamager();
                Player player = (Player) e.getEntity();
                if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                    if (purge.getPm().getWeaponBlacklist() != null) {
                        if (purge.getPm().getWeaponBlacklist().contains(attacker.getInventory().getItemInMainHand().getType()) && attacker.getInventory().getItemInMainHand().getType() != Material.AIR) {
                            e.setCancelled(true);
                            attacker.sendMessage(Main.c("Purge", "That item is disabled in this purge!"));
                            return;
                        }
                    } else if (purge.getPm().getWeaponWhitelist() != null) {
                        if (!purge.getPm().getWeaponWhitelist().contains(attacker.getInventory().getItemInMainHand().getType()) && attacker.getInventory().getItemInMainHand().getType() != Material.AIR) {
                            e.setCancelled(true);
                            attacker.sendMessage(Main.c("Purge", "That item is disabled in this purge!"));
                            return;
                        }
                    }
                    for (Enchantment en : attacker.getInventory().getItemInMainHand().getEnchantments().keySet()) {
                        if (purge.getPm().getEnchantmentBlacklist() != null) {
                            if (purge.getPm().getEnchantmentBlacklist().contains(en)) {
                                e.setCancelled(true);
                                attacker.sendMessage(Main.c("Purge", "That item has an enchantment that is disabled during this purge!"));
                                return;
                            }
                        } else if (purge.getPm().getEnchantmentWhitelist() != null) {
                            if (!purge.getPm().getEnchantmentWhitelist().contains(en)) {
                                e.setCancelled(true);
                                attacker.sendMessage(Main.c("Purge", "That item has an enchantment that is disabled during this purge!"));
                                return;
                            }
                        }
                    }
                    if (attacker.getInventory().getItemInMainHand() != null) {
                        if (attacker.getInventory().getItemInMainHand().getItemMeta() != null) {
                            if (attacker.getInventory().getItemInMainHand().getItemMeta().hasLore()) {
                                e.setCancelled(true);
                                player.sendMessage(Main.c("Purge","Your weapon has an Emerald Enchantment, which are disabled during purges!"));
                                return;
                            }
                        }
                    }

                    if (player.getInventory().getArmorContents() == null) {
                        return;
                    }
                    if (purge.getPm().getArmorBlacklist() != null) {
                        for (ItemStack i : player.getInventory().getArmorContents()) {
                            if (i != null) {
                                if (purge.getPm().getArmorBlacklist().contains(i.getType())) {
                                    player.sendMessage(Main.c("Purge", "Your armor is illegal! You will take FULL DAMAGE and your armor will decrease normally in durability until you put on valid armor!"));
                                    e.setDamage(EntityDamageEvent.DamageModifier.ARMOR, 0.0);
                                    e.setDamage(e.getDamage());
                                    return;
                                }
                            }
                        }
                    } else if (purge.getPm().getArmorWhitelist() != null) {
                        for (ItemStack i : player.getInventory().getArmorContents()) {
                            if (i != null) {
                                if (!purge.getPm().getArmorWhitelist().contains(i.getType())) {
                                    player.sendMessage(Main.c("Purge", "Your armor is illegal! You will take FULL DAMAGE and your armor will decrease normally in durability until you put on valid armor!"));
                                    e.setDamage(EntityDamageEvent.DamageModifier.ARMOR, 0.0);
                                    e.setDamage(e.getDamage());
                                    return;
                                }
                            }
                        }
                    }
                    for (ItemStack i : player.getInventory().getArmorContents()) {
                        if (i == null) {
                            continue;
                        }
                        if (i.getItemMeta() != null){
                            if (i.getItemMeta().hasLore()) {
                                e.setDamage(e.getDamage());
                                player.sendMessage(Main.c("Purge","Your armor is illegal! You will take FULL DAMAGE and your armor will decrease normally in durability until you put on valid armor!"));
                                return;
                            }
                        }
                        for (Enchantment en : i.getEnchantments().keySet()) {
                            if (purge.getPm().getEnchantmentBlacklist() != null) {
                                if (purge.getPm().getEnchantmentBlacklist().contains(en)) {
                                    e.setDamage(e.getDamage());
                                    player.sendMessage(Main.c("Purge","Your armor is illegal! You will take FULL DAMAGE and your armor will decrease normally in durability until you put on valid armor!"));
                                }
                            } else if (purge.getPm().getEnchantmentWhitelist() != null) {
                                if (!purge.getPm().getEnchantmentWhitelist().contains(en)) {
                                    e.setDamage(e.getDamage());
                                    player.sendMessage(Main.c("Purge","Your armor is illegal! You will take FULL DAMAGE and your armor will decrease normally in durability until you put on valid armor!"));
                                }
                            }
                        }
                    }

                } else if (e.getCause() == EntityDamageEvent.DamageCause.THORNS) {
                    if (purge.getPm().getEnchantmentWhitelist() != null) {
                        if (!purge.getPm().getEnchantmentWhitelist().contains(Enchantment.THORNS)) {
                            e.setCancelled(true);
                            attacker.sendMessage(Main.c("Purge","Your armor has the Thorns enchantment, which is disabled during this purge. The Purge enchantment will not work until the purge is over."));
                        }
                    } else if (purge.getPm().getEnchantmentBlacklist() != null) {
                        if (purge.getPm().getEnchantmentBlacklist().contains(Enchantment.THORNS)) {
                            e.setCancelled(true);
                            attacker.sendMessage(Main.c("Purge","Your armor has the Thorns enchantment, which is disabled during this purge. The Purge enchantment will not work until the purge is over."));
                        }
                    }
                }
            }
        }
    }

}
