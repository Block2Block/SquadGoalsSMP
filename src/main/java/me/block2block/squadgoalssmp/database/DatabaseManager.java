package me.block2block.squadgoalssmp.database;

import me.block2block.squadgoalssmp.CacheManager;
import me.block2block.squadgoalssmp.entities.*;
import org.bukkit.*;

import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class DatabaseManager {

    public static DatabaseManager i;

    private static SQLite db;
    private static Connection connection;

    public DatabaseManager() {
        i = this;
    }

    public boolean setup() throws SQLException, ClassNotFoundException {
        db = new SQLite("storage.db");
        connection = db.openConnection();
        createTables();
        loadPurge();
        loadWhitelist();
        loadBeacons();
        loadSigns();
        return true;
    }

    private boolean createTables() {
        try {
            PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS player_profiles ( `id` INTEGER PRIMARY KEY AUTOINCREMENT , `uuid` TEXT NOT NULL , `balance` BIGINT NOT NULL, `team` INT)");
            boolean set = statement.execute();

            statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS purges ( `id` INTEGER PRIMARY KEY AUTOINCREMENT , `type` TEXT NOT NULL , `endtime` TEXT NOT NULL)");
            set = statement.execute();

            statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS whitelists ( `discordid` TEXT NOT NULL, `uuid` TEXT NOT NULL)");
            set = statement.execute();

            statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS beacons ( `world` TEXT NOT NULL, `x` INTEGER NOT NULL, `y` INTEGER NOT NULL, `z` INTEGER NOT NULL)");
            set = statement.execute();

            statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS signs ( `world` TEXT NOT NULL, `x` INTEGER NOT NULL, `y` INTEGER NOT NULL, `z` INTEGER NOT NULL, `type` INTEGER NOT NULL, `material` TEXT NOT NULL)");
            set = statement.execute();

            statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS teams ( `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, `name` TEXT NOT NULL, `leader` TEXT NOT NULL, `members` TEXT NOT NULL, `prefix` TEXT NOT NULL, `color` TEXT NOT NULL, `bank` BIGINT NOT NULL, `homeset` BOOLEAN NOT NULL DEFAULT FALSE,  `x` INTEGER NOT NULL, `y` INTEGER NOT NULL, `z` INTEGER NOT NULL)");
            set = statement.execute();

            return true;
        } catch (SQLException e) {
            Bukkit.getLogger().info("The connection to the database has failed. Shutting down server. Stack trace:");
            e.printStackTrace();
            Bukkit.getServer().shutdown();
            return false;
        }
    }

    public long getBalance(UUID uuid) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT balance FROM `player_profiles` WHERE `uuid` = '" + uuid.toString() + "'");
            ResultSet set = statement.executeQuery();

            if (set.next()) {
                return set.getLong(1);
            } else {
                statement = connection.prepareStatement("INSERT INTO `player_profiles`(uuid, balance) VALUES ('" + uuid.toString() + "',100)");
                boolean result = statement.execute();
                return 100;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void removeBalance(UUID uuid, long amount) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE `player_profiles` SET balance = balance - " + amount + " WHERE `uuid` = '" + uuid.toString() + "'");
            boolean set = statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addBalance(UUID uuid, long amount) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE `player_profiles` SET balance = balance + " + amount + " WHERE `uuid` = '" + uuid.toString() + "'");
            boolean set = statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadPurge() {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM purges ORDER BY id DESC");
            ResultSet set = statement.executeQuery();
            if (set.next()) {
                if (Long.parseLong(set.getString(3)) > System.currentTimeMillis()) {
                    CacheManager.setPurge(new Purge(PurgeMode.getByID(Integer.parseInt(set.getString(2))), Long.parseLong(set.getString(3))));
                } else if (Long.parseLong(set.getString(3)) != -1) {
                    new Purge(PurgeMode.getByID(Integer.parseInt(set.getString(2))), -2);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addPurge(PurgeMode pm, long endtime) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO purges(type, endtime) VALUES ('" + pm.getId() + "','" + endtime + "')");
            boolean set = statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public UUID whitelist(UUID uuid, String discordID) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM `whitelists` WHERE discordid = ('" + discordID + "')");
            ResultSet set = statement.executeQuery();

            if (set.next()) {
                statement = connection.prepareStatement("UPDATE `whitelists` SET uuid = '" + uuid.toString() + "' WHERE discordid = '" + discordID + "'");
                boolean result = statement.execute();
                return UUID.fromString(set.getString(2));
            } else {
                statement = connection.prepareStatement("INSERT INTO `whitelists`(discordid, uuid) VALUES ('" + discordID + "','" + uuid.toString() + "')");
                boolean result = statement.execute();
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void loadWhitelist() {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT uuid FROM `whitelists`");
            ResultSet set = statement.executeQuery();

            List<UUID> whitelist = new ArrayList<>();
            while (set.next()) {
                UUID uuid = UUID.fromString(set.getString(1));
                whitelist.add(uuid);
            }
            CacheManager.setWhitelist(whitelist);
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
    }

    public void removeBeacon(Location location) {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM beacons WHERE (world = '" + location.getWorld().getName() + "') AND (x = " + location.getBlockX() + ") AND (y = " + location.getBlockY() + ") AND (z = " + location.getBlockZ() + ")");
            boolean set = statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
    }

    public void addBeacon(Location location) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO beacons(world, x, y, z) VALUES ('" + location.getWorld().getName() + "'," + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ() + ")");
            boolean set = statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
    }

    public void loadBeacons() {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM beacons");
            ResultSet set = statement.executeQuery();

            List<Location> beacons = new ArrayList<>();
            while (set.next()) {
                beacons.add(new Location(Bukkit.getWorld(set.getString(1)), set.getInt(2), set.getInt(3), set.getInt(4)));
            }
            CacheManager.setBeacons(beacons);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void endPurge(long end) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE `purges` SET endtime = -1 WHERE endtime = '" + end + "'");
            boolean result = statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addSign(Location l, int type, @Nullable Material material) {
        try {
            if (material == null) {
                PreparedStatement statement = connection.prepareStatement("INSERT INTO signs(world, x, y, z, type, material) VALUES ('" + l.getWorld().getName() + "'," + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ() + ", " + type + ",'AIR')");
                boolean set = statement.execute();
            } else {
                PreparedStatement statement = connection.prepareStatement("INSERT INTO signs(world, x, y, z, type, material) VALUES ('" + l.getWorld().getName() + "'," + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ() + ", " + type + ",'" + material.name() + "')");
                boolean set = statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeSign(Location l) {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM `signs` WHERE (world = '" + l.getWorld().getName() + "') AND (x = " + l.getBlockX() + ") AND (y = " + l.getBlockY() + ") AND (z = " + l.getBlockZ() + ")");
            boolean set = statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadSigns() {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM `signs`");
            ResultSet set = statement.executeQuery();

            while (set.next()) {
                Location l = new Location(Bukkit.getWorld(set.getString(1)), set.getInt(2), set.getInt(3), set.getInt(4));
                Material material = Material.matchMaterial(set.getString(6));
                EconomyItem ic = CacheManager.getItems().get(material);
                EconomySign is = new EconomySign(l, ic, set.getInt(5));

                CacheManager.addSign(is, l);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
    }

    public int createTeam(String name, String leader) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO `teams`(name, leader, members, prefix, color, bank, x, y, z) VALUES ('" + name + "','" + leader + "','" + leader + "','','WHITE',0, 0, 0, 0)");
            boolean set = statement.execute();

            statement = connection.prepareStatement("SELECT id FROM teams WHERE name = '" + name + "'");
            ResultSet result = statement.executeQuery();

            statement = connection.prepareStatement("UPDATE player_profiles SET team = " + result.getInt(1) + " WHERE uuid = '" + leader + "'");
            set = statement.execute();

            return result.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void addMemeber(int id, SMPPlayer player) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE teams SET members = (members || '," + player.getUuid() + "') WHERE id = " + id + "");
            boolean set = statement.execute();

            statement = connection.prepareStatement("UPDATE player_profiles SET team = " + id + " WHERE uuid = '" + player.getUuid() + "'");
            set = statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getTeam(UUID uuid) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT `team` FROM `player_profiles` WHERE `uuid` = '" + uuid.toString() + "'");
            ResultSet set = statement.executeQuery();

            if (set.next()) {
                int i = set.getInt(1);
                if (!set.wasNull()) {
                    return i;
                } else {
                    return -1;
                }
            } else {
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public Team getTeam(int id) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM `teams` WHERE `id` = " + id + "");
            ResultSet set = statement.executeQuery();

            if (set.next()) {
                String[] uuids = set.getString(4).split(",");
                List<SMPPlayer> members =  new ArrayList<>();
                for (String uuid  : uuids) {
                    members.add(new SMPPlayer(Bukkit.getOfflinePlayer(UUID.fromString(uuid))));
                }
                return new Team(id, set.getString(2), set.getString(5), new SMPPlayer(Bukkit.getOfflinePlayer(UUID.fromString(set.getString(3)))), members, set.getLong(7), (set.getBoolean(8))?new Location(Bukkit.getWorld("world"),set.getInt(9), set.getInt(10), set.getInt(11)):null, set.getString(6));
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean teamExists(String name) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT id FROM teams WHERE name = '" + name + "'");
            ResultSet result = statement.executeQuery();

            return result.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void removeMember(int id, OfflinePlayer player) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE teams SET members = REPLACE(members,'," + player.getUniqueId() + "', '') WHERE id = " + id + "");
            boolean set = statement.execute();

            statement = connection.prepareStatement("UPDATE teams SET members = REPLACE(members,'" + player.getUniqueId() + ",', '') WHERE id = " + id + "");
            set = statement.execute();

            statement = connection.prepareStatement("UPDATE player_profiles SET team = NULL WHERE uuid = '" + player.getUniqueId() + "'");
            set = statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeTeamBalance(int id, long amount) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE `teams` SET bank = bank - " + amount + " WHERE `id` = " + id + "");
            boolean set = statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addTeamBalance(int id, long amount) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE `teams` SET bank = bank + " + amount + " WHERE `id` = " + id + "");
            boolean set = statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setLeader(int id, SMPPlayer leader) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE `teams` SET leader = '" + leader.getUuid().toString() + "' WHERE `id` = " + id + "");
            boolean set = statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setPrefix(int id, String prefix) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE `teams` SET prefix = '" + prefix + "' WHERE `id` = " + id + "");
            boolean set = statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setColor(int id, ChatColor color) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE `teams` SET color = '" + color.name() + "' WHERE `id` = " + id + "");
            boolean set = statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setHome(int id, Location l) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE `teams` SET homeset = true, x = " + l.getBlockX() + ",y = " + l.getBlockY() + ", z = " + l.getBlockZ() + " WHERE `id` = " + id + "");
            boolean set = statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void disband(int id) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE `player_profiles` SET team = NULL WHERE `team` = " + id + "");
            boolean set = statement.execute();

            statement = connection.prepareStatement("DELETE FROM teams WHERE id = " + id);
            set = statement.execute();
        } catch (SQLException e) {

        }
    }

}
