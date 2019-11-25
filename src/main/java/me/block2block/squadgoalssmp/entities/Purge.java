package me.block2block.squadgoalssmp.entities;

import me.block2block.squadgoalssmp.Main;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class Purge {

    private PurgeMode pm;
    private Scoreboard scoreboard;
    private long endTime;

    public Purge(PurgeMode pm) {
        this.pm = pm;
        this.endTime = System.currentTimeMillis() + 7200000L;

        ScoreboardManager sm = Bukkit.getScoreboardManager();
        scoreboard = sm.getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("Purge","playerKillCount","Purge");
        for (Player p : Bukkit.getOnlinePlayers() ) {
            p.sendMessage(Main.c("Purge","A purge has just begun! " + pm.getDescription()));
            p.sendTitle(Main.c(null, "&d&lPURGE"),Main.c(null, "A purge has just begun!"), 20, 100, 20);
            p.setScoreboard(scoreboard);
            p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 100, 1);
        }
    }

}
