package me.block2block.squadgoalssmp;

import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    public static JDA jda;

    @Override
    public void onEnable() {
        try {
            jda = new JDABuilder(AccountType.BOT).setToken("NDk4OTE1NDAwMDAwNDcxMDYw.Xdwl3w.kvzy3oeVe9FAjrCNw2XPbMbDmC0").setActivity(Activity.playing("Squad Goals SMP")).build();


        } catch (Exception e) {

        }
    }

    @Override
    public void onDisable() {

    }

    public static String c(String prefix, String message) {
        return ChatColor.translateAlternateColorCodes('&', ((prefix==null)?"&r":"&d"+prefix+">> &r") + message);
    }

}
