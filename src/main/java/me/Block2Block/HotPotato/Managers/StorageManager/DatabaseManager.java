package me.Block2Block.HotPotato.Managers.StorageManager;

import me.Block2Block.HotPotato.Entities.HPMap;
import me.Block2Block.HotPotato.Main;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class DatabaseManager {

    public static DatabaseManager i;

    private static SQLite db;
    private static boolean isMysql;
    private static Connection connection;

    public DatabaseManager() {
        i = this;
    }

    public void setup() throws SQLException, ClassNotFoundException {
        db = new SQLite("maps.db");
        connection = db.openConnection();
        createTables();
    }

    private void createTables() {
        try {
            PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS hp_maps ( `id` INT NOT NULL AUTO_INCREMENT , `name` TEXT NOT NULL , `red_spawns` TEXT NOT NULL , `blue_spawns` TEXT NOT NULL , `tnt_spawns` TEXT NOT NULL , `zip_name` TEXT NOT NULL , `waiting_lobby` TEXT NOT NULL , `author` TEXT NOT NULL , PRIMARY KEY (`id`)) ENGINE = MyISAM");
            boolean set = statement.execute();
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "There has been an error creating Database tables. The plugin will be disabled. Stack Trace:");
            e.printStackTrace();
            Bukkit.getServer().getPluginManager().disablePlugin(Main.getInstance());
        }
    }

    public void loadMaps() {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * from hp_maps");
            ResultSet results = statement.executeQuery();

            List<HPMap> maps = new ArrayList<>();

            while (results.next()) {

                List<String> red = Arrays.asList(results.getString(3).split(""));

            }
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "There has been an error loading Database tables. The plugin will be disabled. Stack Trace:");
            e.printStackTrace();
            Bukkit.getServer().getPluginManager().disablePlugin(Main.getInstance());
        }

    }

}
