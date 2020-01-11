package me.block2block.squadgoalssmp.commands;

import me.block2block.squadgoalssmp.CacheManager;
import me.block2block.squadgoalssmp.Main;
import me.block2block.squadgoalssmp.entities.SMPPlayer;
import me.block2block.squadgoalssmp.entities.Team;
import me.block2block.squadgoalssmp.utils.DiscordUtil;
import me.block2block.squadgoalssmp.utils.UUIDFetcher;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

public class CommandTeam implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length > 0) {
                switch (args[0]) {
                    case "help":
                        p.sendMessage(Main.c("Teams", "Teams related commands:\n" +
                                "&d/team help&r - Displays this help menu.\n" +
                                "&d/team accept&r - Accept a team invite.\n" +
                                "&d/team decline&r - Decline a team invite.\n" +
                                "&d/team leave&r - Leave your current team.\n" +
                                "&d/team home&r  - Teleport to the team home.\n" +
                                "&d/team chat&r- Toggle team chat.\n" +
                                "&d/team sethome&r - Sets your teams home. Restricted to Team Leader only.\n" +
                                "&d/team disband&r - Disband your team. Restricted to Team Leader only.\n" +
                                "&d/team create [name]&r - Create a team with the specified name.\n" +
                                "&d/team invite [player]&r - Invite a player to a team.\n" +
                                "&d/team deposit [amount]&r - Deposit money into the team bank.\n" +
                                "&d/team withdraw [amount]&r - Withdraw money from the team bank.\n" +
                                "&d/team balance&r - View the teams current bank balance.\n" +
                                "&d/team prefix [prefix]&r - Set the prefix of the team.\n" +
                                "&d/team transfer [player]&r - Transfer leadership of the team to another player.\n" +
                                "&d/team kick [player]&r - Kicks a player from the team.\n" +
                                "&d/team color [color]&r - Sets the teams color."));
                        break;
                    case "invite":
                        if (CacheManager.getProfile(p.getUniqueId()).getTeam() != null) {
                            if (args.length == 2) {
                                if (!args[1].matches("[A-Za-z0-9_]{0,16}")) {
                                    p.sendMessage(Main.c("Teams","Invalid syntax. Correct syntax: &d/team kick [player]"));
                                    return true;
                                }
                                if (args[1].toLowerCase().equals(p.getName().toLowerCase())) {
                                    p.sendMessage(Main.c("Teams","You cannot invite yourself to the team."));
                                    return true;
                                }

                                if (Bukkit.getPlayer(args[1]) != null) {
                                    Player target = Bukkit.getPlayer(args[1]);
                                    if (!CacheManager.isInvited(target.getUniqueId())) {
                                        Team team = CacheManager.getProfile(p.getUniqueId()).getTeam();
                                        if (team.getMember(target) != null) {
                                            p.sendMessage(Main.c("Teams","That user is already in your team."));
                                            return true;
                                        }
                                        p.sendMessage(Main.c("Teams", "You have invited &d" + target.getName() + " &rto the team. The invitation expires in 60 seconds."));
                                        target.sendMessage(Main.c("Teams", "You have been invited to join the team &d" + team.getName() + " &r. Type &d/team accept&r in order to accept your invitation. If you are already in a team, accepting will result in you leaving the team. The invitation expires in 60 seconds."));
                                        for (SMPPlayer member : team.getMembers()) {
                                            if (!member.getUuid().equals(p.getUniqueId())) {
                                                if (member.getPlayer().isOnline()) {
                                                    Player onlineMember = (Player) member.getPlayer().getPlayer();
                                                    onlineMember.sendMessage(Main.c("Team", "&d" + target.getName() + " &rhas been invited to your team."));
                                                }
                                            }
                                        }
                                        CacheManager.addInvite(target.getUniqueId(), team.getId());
                                        new BukkitRunnable() {
                                            @Override
                                            public void run() {
                                                if (CacheManager.isInvited(target.getUniqueId()) && CacheManager.getInvites().get(target.getUniqueId()) == team.getId()) {
                                                    CacheManager.getInvites().remove(target.getUniqueId());
                                                    target.sendMessage(Main.c("Teams","The invitation expired."));
                                                    p.sendMessage(Main.c("Teams","The invitation expired."));
                                                }
                                            }
                                        }.runTaskLater(Main.getInstance(), 1200);
                                        DiscordUtil.commandUse("/team invite " + target.getName(), p);
                                    } else {
                                        p.sendMessage(Main.c("Teams", "That player already has an outstanding team invite. You must wait until it expires or is answered before you are able to send one."));
                                    }

                                } else {
                                    p.sendMessage(Main.c("Teams", "That is not a valid player. Players must be online in order to invite them."));
                                }
                            } else {
                                p.sendMessage(Main.c("Teams", "Invalid syntax. Correct syntax: &d/team invite [player]"));
                            }
                        } else {
                            p.sendMessage(Main.c("Teams", "You must be in a team to execute this command."));
                        }
                        break;
                    case "accept":
                        if (CacheManager.getInvites().containsKey(p.getUniqueId())) {
                            Team team = CacheManager.getTeams().get(CacheManager.getInvites().get(p.getUniqueId()));
                            CacheManager.getInvites().remove(p.getUniqueId());
                            if (CacheManager.getProfile(p.getUniqueId()).getTeam() != null) {
                                Team previous = CacheManager.getProfile(p.getUniqueId()).getTeam();
                                previous.removeMember(p);
                            }
                            CacheManager.getProfile(p.getUniqueId()).setTeam(team);
                            team.addMember(new SMPPlayer(p));
                            DiscordUtil.commandUse("/team accept", p);
                        } else {
                            p.sendMessage(Main.c("Teams", "You do not have an outstanding Team invite."));
                        }
                        break;
                    case "decline":
                        if (CacheManager.getInvites().containsKey(p.getUniqueId())) {
                            CacheManager.getInvites().remove(p.getUniqueId());
                            p.sendMessage(Main.c("Teams", "You declined the invitation."));
                            DiscordUtil.commandUse("/team decline", p);
                        } else {
                            p.sendMessage(Main.c("Teams", "You do not have an outstanding Team invite."));
                        }
                        break;
                    case "leave":
                        if (CacheManager.getProfile(p.getUniqueId()).getTeam() != null) {
                            Team team = CacheManager.getProfile(p.getUniqueId()).getTeam();
                            if (team.getLeader().getUuid().equals(p.getUniqueId())) {
                                p.sendMessage(Main.c("Teams","You cannot leave a team you are leader of."));
                                return true;
                            }
                            team.removeMember(p);
                            CacheManager.getProfile(p.getUniqueId()).setTeam(null);
                            p.sendMessage(Main.c("Teams", "You left your team."));
                            DiscordUtil.commandUse("/team leave", p);
                        } else {
                            p.sendMessage(Main.c("Teams", "You must be in a team to execute this command."));
                        }
                        break;
                    case "home":
                        if (CacheManager.getProfile(p.getUniqueId()).getTeam() != null) {
                            Team team = CacheManager.getProfile(p.getUniqueId()).getTeam();
                            if (team.getHome() != null) {
                                if (CacheManager.isTrial() && CacheManager.getDefendant().getUniqueId().equals(p.getUniqueId())) {
                                    p.sendMessage(Main.c("Trial","You cannot teleport, you are on trial!"));
                                    return true;
                                }
                                if (!CacheManager.getTeleports().containsKey(p)) {
                                    p.sendMessage(Main.c("Teams", "Please do not move. You will be teleported in &d10 seconds&r."));
                                    CacheManager.getTeleports().put(p, new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                                p.teleport(team.getHome());
                                                p.sendMessage(Main.c("Teams", "You have been teleported to your teams home."));
                                                CacheManager.getTeleports().remove(p);
                                        }
                                    }.runTaskLater(Main.getInstance(), 200));
                                    DiscordUtil.commandUse("/team home", p);
                                } else {
                                    p.sendMessage(Main.c("Teams", "You already have teleportation pending."));
                                }
                            } else {
                                p.sendMessage(Main.c("Teams", "Your team does not have a home set. Get your Team Leader to do &d/team sethome&r in order to set your team home!"));
                            }
                        } else {
                            p.sendMessage(Main.c("Teams", "You must be in a team to execute this command."));
                        }
                        break;
                    case "sethome":
                        if (CacheManager.getProfile(p.getUniqueId()).getTeam() != null) {
                            Team team = CacheManager.getProfile(p.getUniqueId()).getTeam();
                            if (team.getLeader().getUuid().equals(p.getUniqueId())) {
                                if (!p.getLocation().getBlock().isLiquid()) {
                                    Location l = p.getLocation().clone();
                                    l.setY(l.getY() - 1d);
                                    if (!l.getBlock().isEmpty() && !l.getBlock().isLiquid() && l.getBlock().getType() != Material.AIR && l.getBlock().getType() != Material.CAVE_AIR && l.getBlock().getType() != Material.VOID_AIR) {
                                        team.setHome(p.getLocation().getBlock().getLocation());
                                        p.sendMessage(Main.c("Teams", "You have set your teams home to your current location."));
                                        DiscordUtil.commandUse("/team sethome", p);
                                    } else {
                                        p.sendMessage(Main.c("Teams", "You must have a solid block below your current location in order to set your teams home."));
                                    }
                                } else {
                                    p.sendMessage(Main.c("Teams", "You cannot set your teams home location in a liquid."));
                                }
                            } else {
                                p.sendMessage(Main.c("Teams", "Only the team leader can execute this command."));
                            }
                        } else {
                            p.sendMessage(Main.c("Teams", "You must be in a team to execute this command."));
                        }
                        break;
                    case "disband":
                        if (CacheManager.getProfile(p.getUniqueId()).getTeam() != null) {
                            Team team = CacheManager.getProfile(p.getUniqueId()).getTeam();
                            if (team.getLeader().getUuid().equals(p.getUniqueId())) {
                                for (SMPPlayer smpPlayer : team.getMembers()) {
                                    if (!smpPlayer.getUuid().equals(team.getLeader().getUuid())) {
                                        if (smpPlayer.getPlayer().isOnline()) {
                                            ((Player) smpPlayer.getPlayer()).sendMessage(Main.c("Teams", "Your team has been disbanded. You are no longer on a team."));
                                            CacheManager.getProfile(smpPlayer.getUuid()).setTeam(null);
                                        }
                                    }
                                }
                                team.disband();
                                p.sendMessage(Main.c("Teams", "You have disbanded your team. Any bank funds have been deposited in your account."));
                                DiscordUtil.commandUse("/team disband", p);
                            } else {
                                p.sendMessage(Main.c("Teams", "Only the team leader can execute this command."));
                            }
                        } else {
                            p.sendMessage(Main.c("Teams", "You must be in a team to execute this command."));
                        }
                        break;
                    case "create":
                        if (CacheManager.getProfile(p.getUniqueId()).getTeam() == null) {
                            if (args.length == 2) {
                                if (!args[1].matches("[A-Za-z0-9_]+")) {
                                    p.sendMessage(Main.c("Teams", "Team names can only contain Alphanumeric characters and underscores."));
                                    return true;
                                }
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        if (!Main.getDbManager().teamExists(args[1])) {
                                            int id = Main.getDbManager().createTeam(args[1], p.getUniqueId().toString());
                                            SMPPlayer smpPlayer = new SMPPlayer(p);
                                            Team team = new Team(id, args[1], "", smpPlayer, new ArrayList<>(Collections.singletonList(smpPlayer)), 0, null, "GRAY");
                                            CacheManager.addTeam(team);
                                            CacheManager.getProfile(p.getUniqueId()).setTeam(team);
                                            p.sendMessage(Main.c("Teams", "Team created! You can now invite players and use Team homes!"));
                                            DiscordUtil.commandUse("/team create " + args[1], p);
                                        } else {
                                            p.sendMessage(Main.c("Teams", "A team by that name already exists."));
                                        }
                                    }
                                }.runTaskAsynchronously(Main.getInstance());
                            } else {
                                p.sendMessage(Main.c("Teams", "Invalid syntax. Correct syntax: &d/team create [name]"));
                            }
                        } else {
                            p.sendMessage(Main.c("Teams", "You must leave your current team in order to create one."));
                        }
                        break;
                    case "deposit":
                        if (CacheManager.getProfile(p.getUniqueId()).getTeam() != null) {
                            if (args.length == 2) {
                                if (!args[1].matches("[0-9]+")) {
                                    p.sendMessage(Main.c("Teams", "Invalid syntax. Correct syntax: &d/team deposit [amount]"));
                                    return true;
                                }
                                int amount = Integer.parseInt(args[1]);
                                if (amount <= 0) {
                                    p.sendMessage(Main.c("Teams", "You cannot put nothing or negative values into the bank."));
                                    return true;
                                }
                                if (amount > CacheManager.getProfile(p.getUniqueId()).getBalance()) {
                                    p.sendMessage(Main.c("Teams", "You do not have enough money to deposit that much."));
                                    return true;
                                }

                                Team team = CacheManager.getProfile(p.getUniqueId()).getTeam();
                                team.addBalance(amount);
                                CacheManager.getProfile(p.getUniqueId()).removeBalance(amount);
                                DiscordUtil.commandUse("/team deposit " + amount, p);
                            } else {
                                p.sendMessage(Main.c("Teams", "Invalid syntax. Correct syntax: &d/team deposit [amount]"));
                            }
                        } else {
                            p.sendMessage(Main.c("Teams", "You must be in a team to execute this command."));
                        }
                        break;
                    case "withdraw":
                        if (CacheManager.getProfile(p.getUniqueId()).getTeam() != null) {
                            if (args.length == 2) {
                                if (!args[1].matches("[0-9]+")) {
                                    p.sendMessage(Main.c("Teams", "Invalid syntax. Correct syntax: &d/team withdraw [amount]"));
                                    return true;
                                }
                                int amount = Integer.parseInt(args[1]);
                                if (amount <= 0) {
                                    p.sendMessage(Main.c("Teams", "You cannot take nothing or negative values from the bank."));
                                    return true;
                                }
                                if (amount > CacheManager.getProfile(p.getUniqueId()).getTeam().getBalance()) {
                                    p.sendMessage(Main.c("Teams", "The team does not have enough money to withdraw that much."));
                                    return true;
                                }

                                Team team = CacheManager.getProfile(p.getUniqueId()).getTeam();
                                team.removeBalance(amount);
                                CacheManager.getProfile(p.getUniqueId()).addBalance(amount);
                                DiscordUtil.commandUse("/team withdraw " + amount, p);
                            } else {
                                p.sendMessage(Main.c("Teams", "Invalid syntax. Correct syntax: &d/team withdraw [amount]"));
                            }
                        } else {
                            p.sendMessage(Main.c("Teams", "You must be in a team to execute this command."));
                        }
                        break;
                    case "balance":
                        if (CacheManager.getProfile(p.getUniqueId()).getTeam() != null) {
                            p.sendMessage(Main.c("Teams", "Current Team Balance: &d" + CacheManager.getProfile(p.getUniqueId()).getTeam().getBalance()));
                        } else {
                            p.sendMessage(Main.c("Teams", "You must be in a team to execute this command."));
                        }
                        break;
                    case "prefix":
                        if (CacheManager.getProfile(p.getUniqueId()).getTeam() != null) {
                            if (args.length == 2) {
                                if (!args[1].matches("[A-Za-z0-9_&\\[\\](){}]*")) {
                                    p.sendMessage(Main.c("Teams", "Team names can only contain Alphanumeric characters and underscores."));
                                    return true;
                                }

                                String prefix = args[1];

                                CacheManager.getProfile(p.getUniqueId()).getTeam().setPrefix(prefix);
                                p.sendMessage(Main.c("Teams","You have updated the prefix for your team."));
                                DiscordUtil.commandUse("/team prefix " + args[1], p);
                            } else {
                                p.sendMessage(Main.c("Teams","Invalid syntax. Correct syntax: &d/team prefix [prefix]"));
                            }
                        } else {
                            p.sendMessage(Main.c("Teams", "You must be in a team to execute this command."));
                        }
                        break;
                    case "transfer":
                        if (CacheManager.getProfile(p.getUniqueId()).getTeam() != null) {
                            Team team = CacheManager.getProfile(p.getUniqueId()).getTeam();
                            if (team.getLeader().getUuid().equals(p.getUniqueId())) {
                                if (!args[1].matches("[A-Za-z0-9_]+")) {
                                    p.sendMessage(Main.c("Teams","Invalid syntax. Correct syntax: &d/team transfer [player]"));
                                    return true;
                                }
                                if (args[1].toLowerCase().equals(p.getName().toLowerCase())) {
                                    p.sendMessage(Main.c("Teams","You cannot transfer the team to yourself."));
                                    return true;
                                }
                                if (CacheManager.getProfile(p.getUniqueId()).getTeam().getMember(Bukkit.getOfflinePlayer(args[1])) != null) {
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            team.setLeader(team.getMember(Bukkit.getOfflinePlayer(UUIDFetcher.getUUID(args[1]))));
                                            p.sendMessage(Main.c("Teams","You have transfered your team to &d" + args[1] + "&r."));
                                        }
                                    }.runTaskAsynchronously(Main.getInstance());
                                    DiscordUtil.commandUse("/team transfer " + args[1], p);
                                } else {
                                    p.sendMessage(Main.c("Teams","That user is not in your team."));
                                }
                            } else {
                                p.sendMessage(Main.c("Teams", "Only the team leader can execute this command."));
                            }
                        } else {
                            p.sendMessage(Main.c("Teams", "You must be in a team to execute this command."));
                        }
                        break;
                    case "kick":
                        if (CacheManager.getProfile(p.getUniqueId()).getTeam() != null) {
                            if (args.length == 2) {
                                if (!args[1].matches("[A-Za-z0-9_]+")) {
                                    p.sendMessage(Main.c("Teams","Invalid syntax. Correct syntax: &d/team kick [player]"));
                                    return true;
                                }
                                if (args[1].toLowerCase().equals(p.getName().toLowerCase())) {
                                    p.sendMessage(Main.c("Teams","You cannot kick yourself."));
                                    return true;
                                }
                                if (args[1].toLowerCase().equals(CacheManager.getProfile(p.getUniqueId()).getTeam().getLeader().getName().toLowerCase())) {
                                    p.sendMessage(Main.c("Teams","You cannot kick the leader."));
                                    return true;
                                }
                                if (CacheManager.getProfile(p.getUniqueId()).getTeam().getMember(Bukkit.getOfflinePlayer(args[1])) != null) {
                                    Team team = CacheManager.getProfile(p.getUniqueId()).getTeam();
                                    CacheManager.getProfile(p.getUniqueId()).setTeam(null);
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            team.removeMember(Bukkit.getOfflinePlayer(UUIDFetcher.getUUID(args[1])));
                                            p.sendMessage(Main.c("Teams","You have kicked &d" + args[1] + " &rfrom the team."));
                                        }
                                    }.runTaskAsynchronously(Main.getInstance());
                                    DiscordUtil.commandUse("/team kick " + args[1], p);
                                } else {
                                    p.sendMessage(Main.c("Teams","That user is not in your team."));
                                }

                            } else {
                                p.sendMessage(Main.c("Teams","Invalid syntax. Correct syntax: &d/team kick [player]"));
                            }
                        } else {
                            p.sendMessage(Main.c("Teams", "You must be in a team to execute this command."));
                        }
                        break;
                    case "color":
                        if (CacheManager.getProfile(p.getUniqueId()).getTeam() != null) {
                            if (args.length == 2) {
                                ChatColor color;
                                try {
                                    color = ChatColor.valueOf(args[1].toUpperCase());
                                } catch (IllegalArgumentException e) {
                                    p.sendMessage(Main.c("Teams","That is not a valid color."));
                                    return true;
                                }
                                if (!color.isColor()) {
                                    p.sendMessage(Main.c("Teams","That is not a valid color."));
                                    return true;
                                }

                                CacheManager.getProfile(p.getUniqueId()).getTeam().setColor(color);
                                p.sendMessage(Main.c("Teams","You have updated the color for your team."));
                                DiscordUtil.commandUse("/team color " + args[1], p);
                            } else {
                                p.sendMessage(Main.c("Teams","Invalid syntax. Correct syntax: &d/team color [color]"));
                            }
                        } else {
                            p.sendMessage(Main.c("Teams", "You must be in a team to execute this command."));
                        }
                        break;
                    case "chat":
                        if (CacheManager.getProfile(p.getUniqueId()).getTeam() != null) {
                            if (CacheManager.toggleChat(p)) {
                                p.sendMessage(Main.c("Teams","Team chat: &aenabled&r."));
                            } else {
                                p.sendMessage(Main.c("Teams","Team chat: &cdisabled&r."));
                            }
                        } else {
                            p.sendMessage(Main.c("Teams", "You must be in a team to execute this command."));
                        }
                        break;
                    default:
                        p.sendMessage(Main.c("Teams","Unknown sub-command. Use &d/team help &rfor help."));
                }
            } else {
                if (CacheManager.getProfile(p.getUniqueId()).getTeam()!= null) {
                    Team team = CacheManager.getProfile(p.getUniqueId()).getTeam();
                    StringBuilder sb = new StringBuilder();
                    sb.append("Your team info:\n" + "Name: &d")
                            .append(team.getName())
                            .append("&r\n")
                            .append("Leader: &d")
                            .append(team.getLeader().getName())
                            .append("&r\n").append("Balance: &d")
                            .append(team.getBalance())
                            .append("&r\n").append("Prefix: ")
                            .append(Main.c(null, team.getPrefix()))
                            .append("&r\n")
                            .append("&r\n").append("Color: ")
                            .append(Main.c(null, "&" + team.getColor().getChar() + "" + team.getColor().name().toLowerCase()))
                            .append("&r\n")
                            .append("Members: &d");
                    boolean first = true;
                    for (SMPPlayer player : team.getMembers()) {
                        if (!first) {
                            sb.append("&r, &d");
                        }
                        sb.append(player.getName());
                        first = false;
                    }
                    p.sendMessage(Main.c("Teams",sb.toString()));
                } else {
                    p.sendMessage(Main.c("Teams","You are not a part of a team. Either create a team or ask to be invited to one! If you need help, do /team help!"));
                }
            }

        }
        return true;
    }
}
