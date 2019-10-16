package me.Block2Block.HotPotato.Managers.StorageManager;

import me.Block2Block.HotPotato.Main;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Connects to and uses a SQLite database
 *
 * @author tips48
 */
public class SQLite extends Database {
    private final String dbLocation;

    /**
     * Creates a new SQLite instance
     *
     * @param dbLocation Location of the Database (Must end in .db)
     */
    public SQLite(String dbLocation) {
        this.dbLocation = dbLocation;
    }

    @Override
    public Connection openConnection() throws SQLException,
            ClassNotFoundException {
        if (checkConnection()) {
            return connection;
        }

        File dataFolder = new File(Main.getInstance().getDataFolder().getAbsolutePath());
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        File file = new File(dataFolder, dbLocation);
        if (!(file.exists())) {
            try {
                file.createNewFile();
                Main.getInstance().getLogger().info("Database file " + dbLocation + " successfully created!");
            } catch (IOException e) {
                Main.getInstance().getLogger().info("Unable to create database. Stack Trace:");
                e.printStackTrace();
            }
        }
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager
                .getConnection("jdbc:sqlite:"
                        + dataFolder + "/"
                        + dbLocation);
        return connection;
    }
}
