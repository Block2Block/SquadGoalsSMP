package me.block2block.squadgoalssmp.listeners;

import me.block2block.squadgoalssmp.CacheManager;
import me.block2block.squadgoalssmp.Main;
import me.block2block.squadgoalssmp.utils.UUIDFetcher;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class DiscordMessageEvent extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        if (e.isFromType(ChannelType.TEXT)) {
            if (e.getGuild().getId().equals("476121430786834432")) {
                if (e.getChannel().getId().equals("501300319347539973")) {
                    if (e.getMessage().getContentStripped().startsWith("!whitelist")) {
                        if (e.getMessage().getContentStripped().split(" ").length == 2) {
                            String[] args = e.getMessage().getContentStripped().split(" ");
                            if (args[1].matches("[0-9A-Za-z_]{3,16}")) {
                                if (!Main.isReady()) {
                                    e.getMessage().getChannel().sendMessage("The server is still loading, please wait.").queue();
                                    return;
                                }
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        UUID uuid = UUIDFetcher.getUUID(args[1]);
                                        UUID previous = Main.getDbManager().whitelist(uuid, e.getAuthor().getId());
                                        if (previous != null) {
                                            CacheManager.removeWhitelist(previous);
                                        }
                                        CacheManager.addWhitelist(uuid);
                                        e.getMessage().getChannel().sendMessage("Player whitelisted. If you had whitelisted an account before, it is no longer whitelisted.").queue();

                                    }
                                }.runTaskAsynchronously(Main.getInstance());
                            } else {
                                e.getMessage().getChannel().sendMessage("Invalid syntax. Correct syntax: **!whitelist [username]**").queue();
                            }
                        } else {
                            e.getMessage().getChannel().sendMessage("Invalid syntax. Correct syntax: **!whitelist [username]**").queue();
                        }
                    }
                } else if (e.getChannel().getId().equals("666013172444037178")) {
                    if (!e.getAuthor().getId().equalsIgnoreCase("498915400000471060")) {
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            p.sendMessage(Main.c( null, "&3[Discord] &r" + e.getAuthor().getName() + ": " + e.getMessage().getContentRaw()));
                        }
                    }
                }
            }
        } else {
            e.getMessage().getChannel().sendMessage("You must use #whitelist-request in order to use this bot.").queue();
        }
    }

}
