package me.block2block.squadgoalssmp.database;

import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
        return createTables();
    }

    private boolean createTables() {
        try {
            PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS hp_maps ( `id` INT PRIMARY KEY AUTO_INCREMENT , `name` TEXT NOT NULL , `red_spawns` TEXT NOT NULL , `blue_spawns` TEXT NOT NULL , `tnt_spawns` TEXT NOT NULL , `zip_name` TEXT NOT NULL , `waiting_lobby` TEXT NOT NULL , `author` TEXT NOT NULL)");
            boolean set = statement.execute();
            return true;
        } catch (SQLException e) {
            Bukkit.getLogger().info("The connection to the database has failed. Shutting down server. Stack trace:");
            e.printStackTrace();
            Bukkit.getServer().shutdown();
            return false;
        }
    }

}
