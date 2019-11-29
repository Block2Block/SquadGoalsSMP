package me.block2block.squadgoalssmp.entities;

import me.block2block.squadgoalssmp.CacheManager;
import me.block2block.squadgoalssmp.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;

public class Purge {

    private PurgeMode pm;
    private Scoreboard scoreboard;
    private long endTime;
    private BukkitTask timer;

    public Purge(PurgeMode pm, long end) {
        this.pm = pm;
        if (end != -1 && end != -2) {
            this.endTime = end;
            timer = new BukkitRunnable() {
                @Override
                public void run() {
                    end();
                }
            }.runTaskLater(Main.getInstance(), (endTime - System.currentTimeMillis())/50);
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
            Objective objective = scoreboard.registerNewObjective("purge_kills","playerKillCount", Main.c(null, "&d&lPurge Kills"));
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            for (Player p : Bukkit.getOnlinePlayers() ) {
                p.sendMessage(Main.c("Purge","A purge has just begun! " + pm.getDescription() + " All illegal potion effects have been removed."));
                p.sendTitle(Main.c(null, "&d&lPURGE"),Main.c(null, "A purge has just begun!"), 20, 100, 20);
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
            }.runTaskLater(Main.getInstance(), 600);
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

        for (Player p : Bukkit.getOnlinePlayers() ) {
            p.sendMessage(Main.c("Purge","The purge has ended! All weapons, armor, enchantments and potion effects are now enabled! Rewards will be given shortly."));
            p.sendTitle(Main.c(null, "&d&lPURGE"),Main.c(null, "The purge has ended!"), 20, 100, 20);
            p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, 100, 1);
        }

        Main.getDbManager().endPurge(endTime);

    }
}
