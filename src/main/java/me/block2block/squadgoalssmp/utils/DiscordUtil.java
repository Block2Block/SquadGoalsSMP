package me.block2block.squadgoalssmp.utils;

import me.block2block.squadgoalssmp.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import org.bukkit.entity.Player;

import java.awt.*;

public class DiscordUtil {

    public static void commandUse(String command, Player player) {
        EmbedBuilder eb = new EmbedBuilder();

        eb.setAuthor("Squad Goals SMP", null, Main.getJda().getGuildById("476121430786834432").getIconUrl());
        eb.setColor(Color.ORANGE);
        eb.setTitle(player.getName() + " has used a command");
        eb.setDescription(command);

        Main.getJda().getTextChannelById("655038222811791370").sendMessage(eb.build()).queue();
    }

}
