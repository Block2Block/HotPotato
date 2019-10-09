package me.Block2Block.HotPotato.Managers.StorageManager;

import me.Block2Block.HotPotato.Main;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
            PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS hp_maps (`type` tinyint(3) NOT NULL,`x` bigint(64) NOT NULL,`y` bigint(64) NOT NULL,`z` bigint(64) NOT NULL, `checkno` tinyint(64) NULL, `world` varchar(64) NOT NULL, PRIMARY KEY (`type`, x, y, z))");
            boolean set = statement.execute();
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "There has been an error creating Database tables. The plugin will be disabled. Stack Trace:");
            e.printStackTrace();
            Bukkit.getServer().getPluginManager().disablePlugin(Main.getInstance());
        }
    }

}
