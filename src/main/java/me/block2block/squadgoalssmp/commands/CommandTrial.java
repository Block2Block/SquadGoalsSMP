package me.block2block.squadgoalssmp.commands;

import me.block2block.squadgoalssmp.CacheManager;
import me.block2block.squadgoalssmp.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class CommandTrial implements CommandExecutor {


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length > 0) {
                switch (args[0].toLowerCase()) {
                    case "live":
                        if (CacheManager.isTrial()) {
                            if (CacheManager.isVoting()) {
                                if (!CacheManager.hasVoted(p)) {
                                    CacheManager.addVote(p, true);
                                    p.sendMessage(Main.c("Trial","You voted for: &dlive&r."));
                                } else {
                                    p.sendMessage(Main.c("Trial","You have already voted."));
                                }
                            } else {
                                p.sendMessage(Main.c("Trial","The vote is not currently active."));
                            }
                        } else {
                            p.sendMessage(Main.c("Trial","There is not currently a trial in progress."));
                        }
                        break;
                    case "die":
                        if (CacheManager.isTrial()) {
                            if (CacheManager.isVoting()) {
                                if (!CacheManager.hasVoted(p)) {
                                    CacheManager.addVote(p, false);
                                    p.sendMessage(Main.c("Trial","You voted for: &ddie&r."));
                                } else {
                                    p.sendMessage(Main.c("Trial","You have already voted."));
                                }
                            } else {
                                p.sendMessage(Main.c("Trial","The vote is not currently active."));
                            }
                        } else {
                            p.sendMessage(Main.c("Trial","There is not currently a trial in progress."));
                        }
                        break;
                    case "teleport":
                        if (CacheManager.isTrial()) {
                            p.sendMessage(Main.c("Trial","You have been teleported to the Courthouse."));
                            p.teleport(new Location(Bukkit.getWorld("world"),0,64,0));
                        } else {
                            p.sendMessage(Main.c("Trial","There is not currently a trial in progress."));
                        }
                        break;
                    case "startvote":
                        if (CacheManager.isTrial()) {
                            if (p.isOp()) {
                                if (!CacheManager.isVoting()) {
                                    CacheManager.startVoting();
                                    for (Player player : Bukkit.getOnlinePlayers()) {
                                        player.sendMessage(Main.c("Trial","A vote has just started! Use &d/trial live&r or &d/trial die&r to vote! The vote lasts 30 seconds!"));
                                    }
                                    p.sendMessage(Main.c("Trial","You started a vote."));
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            if (CacheManager.isVoting() && CacheManager.isTrial()) {
                                                for (Player player : Bukkit.getOnlinePlayers()) {
                                                    player.sendMessage(Main.c("Trial","The vote has ended."));
                                                }
                                                int points = 0;
                                                for (boolean b : CacheManager.getVotes()) {
                                                    points += ((b)?0:1);
                                                }

                                                if (points > (CacheManager.getVotes().size() / 2)) {
                                                    for (Player player : Bukkit.getOnlinePlayers()) {
                                                        player.sendMessage(Main.c("Trial","The votes have been counted, and the people have voted... &d&lDIE&r! &c&lBURN BABY, BURN."));
                                                    }
                                                    Player defendant = CacheManager.getDefendant();
                                                    ((Chest) (new Location(Bukkit.getWorld("world"),0, 64, 0)).getBlock()).getInventory().addItem(defendant.getInventory().getContents());
                                                    defendant.setFireTicks(600);
                                                    CacheManager.endVote();
                                                } else {
                                                    for (Player player : Bukkit.getOnlinePlayers()) {
                                                        player.sendMessage(Main.c("Trial","The votes have been counted, and the people have voted... &d&lLIVE"));
                                                    }
                                                    CacheManager.endVote();
                                                }
                                            }
                                        }
                                    }.runTaskLater(Main.getInstance(), 600);
                                } else {
                                    p.sendMessage(Main.c("Trial","There is already a vote in progress."));
                                }
                            } else {
                                p.sendMessage(Main.c("Trial","You do not have permission to use this command."));
                            }
                        } else {
                            p.sendMessage(Main.c("Trial","There is not currently a trial in progress."));
                        }
                        break;
                    case "end":
                        if (CacheManager.isTrial()) {
                            if (p.isOp()) {
                                for (Player player : Bukkit.getOnlinePlayers()) {
                                    player.sendMessage(Main.c("Trial", "The trial has been forced to end."));
                                }
                                if (!CacheManager.isVoting()) {
                                    for (Player player : Bukkit.getOnlinePlayers()) {
                                        player.sendMessage(Main.c("Trial", "The vote has ended."));
                                    }
                                    int points = 0;
                                    for (boolean b : CacheManager.getVotes()) {
                                        points += ((b) ? 0 : 1);
                                    }

                                    if (points > (CacheManager.getVotes().size() / 2)) {
                                        for (Player player : Bukkit.getOnlinePlayers()) {
                                            player.sendMessage(Main.c("Trial", "The votes have been counted, and the people have voted... &d&lDIE&r! &c&lBURN BABY, BURN."));
                                        }
                                        Player defendant = CacheManager.getDefendant();
                                        defendant.setFireTicks(600);
                                        CacheManager.endVote();
                                    } else {
                                        for (Player player : Bukkit.getOnlinePlayers()) {
                                            player.sendMessage(Main.c("Trial", "The votes have been counted, and the people have voted... &d&lLIVE"));
                                        }
                                        CacheManager.endVote();
                                    }
                                } else {
                                    CacheManager.endVote();
                                }
                            } else {
                                p.sendMessage(Main.c("Trial","You do not have permission to use this command."));
                            }
                        } else {
                            p.sendMessage(Main.c("Trial","There is not currently a trial in progress."));
                        }
                        break;
                    default:
                        if (!args[0].matches("[A-Za-z0-9_]{1,16}")) {
                            p.sendMessage(Main.c("Trial","Trial Related Sub-commands:\n" +
                                    "&d/trial live&r - Vote for the defendant to live.\n" +
                                    "&d/trial die&r - Vote for the defendant to die.\n" +
                                    "&d/trial teleport&r - Teleports you to the courthouse.\n" +
                                    "&d/trial [player]&r - Send a player to trial. Server admin only.\n" +
                                    "&d/trial startvote&r - Starts a trial vote. Server admin only.\n" +
                                    "&d/trial end&r - Forcefully ends a trial. Server admin only."));
                            return true;
                        }

                        if (!p.isOp()) {
                            p.sendMessage(Main.c("Trial","You do not have permission to use this command."));
                            return true;
                        }
                        if (Bukkit.getPlayer(args[0]) != null) {
                            Player defendant = Bukkit.getPlayer(args[0]);
                            ((Chest) (new Location(Bukkit.getWorld("world"),0, 64, 0)).getBlock()).getInventory().addItem(defendant.getInventory().getContents());
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                if (!player.getUniqueId().equals(defendant.getUniqueId())) {
                                    p.sendMessage(Main.c("Trial","A trial has just begun! Defendant: &d" + defendant.getName() + "&r."));
                                } else {
                                    p.sendMessage(Main.c("Trial","You have been sent to trial!"));
                                }
                            }
                            defendant.teleport(new Location(Bukkit.getWorld("world"),0,64,0));
                            CacheManager.startTrial(defendant);
                        } else {
                            p.sendMessage(Main.c("Trial","That player is not online."));
                        }
                        break;
                }
            } else {
                p.sendMessage(Main.c("Trial","Trial Related Sub-commands:\n" +
                        "&d/trial live&r - Vote for the defendant to live.\n" +
                        "&d/trial die&r - Vote for the defendant to die.\n" +
                        "&d/trial teleport&r - Teleports you to the courthouse.\n" +
                        "&d/trial [player]&r - Send a player to trial. Server admin only.\n" +
                        "&d/trial startvote&r - Starts a trial vote. Server admin only.\n" +
                        "&d/trial end&r - Forcefully ends a trial. Server admin only."));
            }
        }
        return true;
    }
}
