package me.block2block.squadgoalssmp.utils;

import me.block2block.squadgoalssmp.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import org.bukkit.entity.Player;

import java.awt.*;

public class DiscordUtil {

    public static void commandUse(String command, Player player) {
        EmbedBuilder eb = new EmbedBuilder();

        eb.setAuthor("Coelum SMP", null, Main.getJda().getGuildById("767817191104970772").getIconUrl());
        eb.setColor(Color.ORANGE);
        eb.setTitle(player.getName() + " has used a command");
        eb.setDescription(command);

        Main.getJda().getTextChannelById("768092763489042442").sendMessage(eb.build()).queue();
    }

    public static void chat(String message, Player player) {
        Main.getJda().getTextChannelById("667161928157757458").sendMessage("**" + player.getName() + ":** " + message).queue();
    }

    public static void teamChat(String message, Player player) {
        Main.getJda().getTextChannelById("666013172444037178").sendMessage("**[Team Chat] " + player.getName() + ":** " + message).queue();
    }

    public static void leave(Player player) {
        Main.getJda().getTextChannelById("768092871097581578").sendMessage("🔴 **" + player.getName() + " left the server.** ").queue();
    }

    public static void join(Player player) {
        Main.getJda().getTextChannelById("768092871097581578").sendMessage("🟢 **" + player.getName() + " joined the server.** ").queue();
    }

}
