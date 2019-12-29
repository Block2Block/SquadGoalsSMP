package me.block2block.squadgoalssmp.listeners;

import me.block2block.squadgoalssmp.CacheManager;
import me.block2block.squadgoalssmp.entities.PurgeMode;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

public class DieListener implements Listener {

    @EventHandler
    public void onDie(EntityDeathEvent e) {
        if (e.getEntity().getKiller() == null) {
            return;
        }
        if (CacheManager.isPurge()) {
            if (CacheManager.getPurge().getPm() == PurgeMode.BLOODMOON||CacheManager.getPurge().getPm() == PurgeMode.ZOMBIEAPOCALYPSE) {
                if (CacheManager.getPurge().getPm() == PurgeMode.BLOODMOON) {
                    switch (e.getEntityType()) {
                        case ZOMBIE:
                        case SKELETON:
                        case WOLF:
                        case SPIDER:
                            Objective objective = Bukkit.getScoreboardManager().getMainScoreboard().getObjective("purge_kills");
                            Score score = objective.getScore(e.getEntity().getKiller());
                            score.setScore(score.getScore() + 1);
                            break;
                    }
                } else {
                    if (e.getEntityType() == EntityType.ZOMBIE) {
                        Objective objective = Bukkit.getScoreboardManager().getMainScoreboard().getObjective("purge_kills");
                        Score score = objective.getScore(e.getEntity().getKiller());
                        score.setScore(score.getScore() + 1);
                    }
                }
            }
        }
    }

}
