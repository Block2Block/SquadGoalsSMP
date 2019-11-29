package me.block2block.squadgoalssmp;

import me.block2block.squadgoalssmp.commands.CommandBalance;
import me.block2block.squadgoalssmp.commands.CommandLoadPrice;
import me.block2block.squadgoalssmp.commands.CommandPay;
import me.block2block.squadgoalssmp.commands.CommandPurge;
import me.block2block.squadgoalssmp.database.DatabaseManager;
import me.block2block.squadgoalssmp.entities.EconomyItem;
import me.block2block.squadgoalssmp.listeners.*;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

public class Main extends JavaPlugin {

    private static JDA jda;
    private static DatabaseManager dbManager;
    private static Main i;

    private static File configFile;
    private static FileConfiguration config;

    @Override
    public void onEnable() {

        //Generating/Loading Config File
        if (!getDataFolder().exists()) getDataFolder().mkdir();

        configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            copy(getResource("config.yml"), configFile);
        }

        config = new YamlConfiguration();
        try {
            config.load(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        config.options().copyHeader(true);

        Map<Material, EconomyItem> items = new HashMap<>();
        for (String s : config.getKeys(false)) {
            Material material = Material.matchMaterial(s);
            getLogger().info(s + " is not a valid material, values were skipped.");
            if (material != null) {
                EconomyItem ei = new EconomyItem(material);
                items.put(material, ei);
            }
        }

        CacheManager.setItems(items);

        i = this;

        Bukkit.setWhitelist(false);

        try {
            jda = new JDABuilder(AccountType.BOT).setToken("NDk4OTE1NDAwMDAwNDcxMDYw.Xdwl3w.kvzy3oeVe9FAjrCNw2XPbMbDmC0").setActivity(Activity.playing("Squad Goals SMP")).build();
            jda.addEventListener(new DiscordMessageEvent());
            dbManager = new DatabaseManager();
            dbManager.setup();
        } catch (Exception e) {
            e.printStackTrace();
        }

        registerListeners(new DamageListener(), new DrinkPotionEvent(), new PlayerJoinListener(), new ShotBowEvent(), new PotionThrownListener(), new PlaceListener(), new BreakListener(), new SignPlaceListener(), new SignClickListener(), new ChatListener(), new InvCloseListener());

        getCommand("supersecretadmincommand").setExecutor(new CommandPurge());
        getCommand("pay").setExecutor(new CommandPay());
        getCommand("balance").setExecutor(new CommandBalance());
        getCommand("updateecon").setExecutor(new CommandLoadPrice());

        if (!CacheManager.isPurge()) {
            //Copying scoreboard to a new dummy one, so it cannot be modified.
            if (Bukkit.getScoreboardManager().getMainScoreboard().getObjective("purge_final") != null) {
                Bukkit.getScoreboardManager().getMainScoreboard().getObjective("purge_final").unregister();
            }
            Objective objective = Bukkit.getScoreboardManager().getMainScoreboard().getObjective("purge_kills");
            if (objective != null) {
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
            }
        }
    }

    @Override
    public void onDisable() {
        jda.shutdownNow();
        dbManager.close();
    }

    public static String c(String prefix, String message) {
        return ChatColor.translateAlternateColorCodes('&', ((prefix==null)?"&r":"&5"+prefix+">> &r") + message);
    }

    private void registerListeners(Listener... listeners) {
        Arrays.stream(listeners).forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));
    }

    public static DatabaseManager getDbManager() {
        return dbManager;
    }

    public static Main getInstance() {
        return i;
    }

    private void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=in.read(buf))>0){
                out.write(buf,0,len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @NotNull
    public FileConfiguration getConfig() {
        return config;
    }

    public void reloadConfig() {
        try {
            config.load(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        config.options().copyHeader(true);
    }
}
