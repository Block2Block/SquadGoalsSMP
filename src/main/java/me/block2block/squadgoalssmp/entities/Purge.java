package me.block2block.squadgoalssmp.entities;

import me.block2block.squadgoalssmp.CacheManager;
import me.block2block.squadgoalssmp.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Purge {

    private PurgeMode pm;
    private Scoreboard scoreboard;
    private long endTime;
    private BukkitTask timer;
    private BukkitTask bloodmoonSpawner;

    public Purge(PurgeMode pm, long end) {
        this.pm = pm;
        if (end != -1 && end != -2) {
            this.endTime = end;
            timer = new BukkitRunnable() {
                @Override
                public void run() {
                    end();
                }
            }.runTaskLater(Main.getInstance(), ((endTime - System.currentTimeMillis()) /1000) * 20);
        } else {
            if (end == -2) {
                end();
                return;
            }
            this.endTime = System.currentTimeMillis() + 7200000L;
            Main.getDbManager().addPurge(pm, endTime);
            ScoreboardManager sm = Bukkit.getScoreboardManager();
            assert sm != null;
            scoreboard = sm.getMainScoreboard();
            Objective objective = scoreboard.registerNewObjective("purge_kills", "playerKillCount", Main.c(null, "&d&lPurge Kills"));
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendMessage(Main.c("Purge", "A purge has just begun! " + pm.getDescription() + " All illegal potion effects have been removed."));
                p.sendTitle(Main.c(null, "&d&lPURGE"), Main.c(null, "A purge has just begun!"), 20, 100, 20);
                p.setScoreboard(scoreboard);
                p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 100, 1);

                for (PotionEffect pe : p.getActivePotionEffects()) {
                    if (pm.getPotionWhitelist() != null) {
                        if (!pm.getPotionWhitelist().contains(pe.getType())) {
                            p.removePotionEffect(pe.getType());
                        }
                    } else if (pm.getPotionBlacklist() != null) {
                        if (pm.getPotionBlacklist().contains(pe.getType())) {
                            p.removePotionEffect(pe.getType());
                        }
                    }
                }
                objective.getScore(p.getName()).setScore(0);
            }
            for (Location location : CacheManager.getBeacons()) {
                Location l = location.clone();
                l.setY(l.getBlockY() + 1);
                l.getBlock().setType(Material.OBSIDIAN);
            }

            timer = new BukkitRunnable() {
                @Override
                public void run() {
                    end();
                }
            }.runTaskLater(Main.getInstance(), ((endTime - System.currentTimeMillis()) /1000) * 20);

            if (pm == PurgeMode.BLOODMOON) {
                bloodmoonSpawner = new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (Player p  : Bukkit.getOnlinePlayers()) {
                            List<Zombie> zombies = new ArrayList<>();
                            int players = 1;
                            for (Entity entity : p.getNearbyEntities(25, 5, 25)) {
                                if (entity instanceof Zombie) {
                                    zombies.add((Zombie) entity);
                                } else if (entity instanceof Player) {
                                    players++;
                                }
                            }

                            if (zombies.size() < (15 * players)) {
                                boolean addX = chooseRan(0, 1) == 0;
                                boolean addZ = chooseRan(0, 1) == 0;

                                int x = (addX)?(p.getLocation().getBlockX() + chooseRan(3, 25)):(p.getLocation().getBlockX() - chooseRan(3, 25));
                                int z = (addZ)?(p.getLocation().getBlockZ() + chooseRan(3, 25)):(p.getLocation().getBlockZ() - chooseRan(3, 25));
                                int y = p.getLocation().getBlockY();

                                Location l = null;
                                if (!new Location(p.getWorld(), x, y, x).getBlock().isEmpty() & !new Location(p.getWorld(), x, y + 1, x).getBlock().isEmpty()) {
                                    l = new Location(p.getWorld(), x, y, x);
                                } else {
                                    for (int i = 1;i <=5;i++) {
                                        if (!new Location(p.getWorld(), x, y + i, x).getBlock().isEmpty() & !new Location(p.getWorld(), x, y + i + 1, x).getBlock().isEmpty()) {
                                            l = new Location(p.getWorld(), x, y + i, x);
                                            break;
                                        } else if (!new Location(p.getWorld(), x, y - i, x).getBlock().isEmpty() & !new Location(p.getWorld(), x, y - i + 1, x).getBlock().isEmpty()) {
                                            l = new Location(p.getWorld(), x, y - i, x);
                                            break;
                                        }
                                    }
                                }

                                if (l != null)  {
                                    l = new Location(p.getWorld(), x, y,z);
                                    Zombie zombie = (Zombie) p.getWorld().spawnEntity(l, EntityType.ZOMBIE);

                                    List<Entity> entities = zombie.getNearbyEntities(25, 25, 25);
                                    List<LivingEntity> targets = new ArrayList<>();
                                    for (Entity entity : entities) {
                                        if (entity instanceof Player) {
                                            targets.add((LivingEntity) entity);
                                        }
                                    }
                                    zombie.setTarget(p);
                                    zombie.setCustomName(Main.c(null,"&4&lBlood Zombie"));
                                    zombie.setCustomNameVisible(true);
                                    zombie.setMaxHealth(50);
                                    zombie.setHealth(50);
                                    zombie.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100000, 3, true, false, false), false);
                                    zombie.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100000, 1, true, false, false), false);
                                } else {
                                    Bukkit.getLogger().info("Unable to find place to spawn zombie for " + p.getName() + ". Skipping this time.");
                                }
                            }
                        }
                    }
                }.runTaskTimer(Main.getInstance(), 0, 40);
            }

        }
    }

    public PurgeMode getPm() {
        return pm;
    }

    public void end() {
        if (timer != null) {
            timer.cancel();
        }

        //Copying scoreboard to a new dummy one, so it cannot be modified.
        Objective objective = Bukkit.getScoreboardManager().getMainScoreboard().getObjective("purge_kills");
        if (Bukkit.getScoreboardManager().getMainScoreboard().getObjective("purge_final") != null) {
            Bukkit.getScoreboardManager().getMainScoreboard().getObjective("purge_final").unregister();
        }
        Objective newKills = Bukkit.getScoreboardManager().getMainScoreboard().registerNewObjective("purge_final", "dummy", Main.c(null, "&d&lFinal Purge Kills"));
        objective.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        for (String s : objective.getScoreboard().getEntries()) {
            Score score = objective.getScore(s);
            if (score.getScore() == 0) {
                continue;
            }
            Score score2 = newKills.getScore(s);
            score2.setScore(score.getScore());
        }
        newKills.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.unregister();

        CacheManager.setPurge(null);

        for (Location location : CacheManager.getBeacons()) {
            Location l = location.clone();
            l.setY(l.getBlockY() + 1);
            l.getBlock().setType(Material.AIR);
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(Main.c("Purge", "The purge has ended! All weapons, armor, enchantments and potion effects are now enabled! Rewards will be given shortly."));
            p.sendTitle(Main.c(null, "&d&lPURGE"), Main.c(null, "The purge has ended!"), 20, 100, 20);
            p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, 100, 1);
        }

        Main.getDbManager().endPurge(endTime);

        if (bloodmoonSpawner != null) {
            bloodmoonSpawner.cancel();
            bloodmoonSpawner = null;
        }

    }

    public static int chooseRan(int min, int max){
        Random rn = new Random();
        return rn.nextInt(max - min + 1) + min;
    }
}
