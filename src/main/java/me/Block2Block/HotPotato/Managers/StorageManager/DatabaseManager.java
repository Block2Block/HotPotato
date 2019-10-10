package me.Block2Block.HotPotato.Managers.StorageManager;

import me.Block2Block.HotPotato.Entities.HPMap;
import me.Block2Block.HotPotato.Main;
import me.Block2Block.HotPotato.Managers.CacheManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import sun.security.util.Cache;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
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
        loadMaps();
        loadSigns();
    }

    private void createTables() {
        try {
            PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS hp_maps ( `id` INT NOT NULL AUTO_INCREMENT , `name` TEXT NOT NULL , `red_spawns` TEXT NOT NULL , `blue_spawns` TEXT NOT NULL , `tnt_spawns` TEXT NOT NULL , `zip_name` TEXT NOT NULL , `waiting_lobby` TEXT NOT NULL , `author` TEXT NOT NULL , PRIMARY KEY (`id`)) ENGINE = MyISAM");
            boolean set = statement.execute();

            statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS hp_signs ( `id` INT NOT NULL AUTO_INCREMENT , `type` TEXT NOT NULL , `world` TEXT NOT NULL , `x` INT NOT NULL , `y` INT NOT NULL , `z` INT NOT NULL , PRIMARY KEY (`id`)) ENGINE = MyISAM");
            set = statement.execute();
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

                //Loading red spawns.
                List<String> red = Arrays.asList(results.getString(3).split(";"));
                List<List<Integer>> redSpawns = new ArrayList<>();

                for (String s : red) {
                    List<Integer> location = new ArrayList<>();
                    List<String> strings = Arrays.asList(s.split(","));
                    for (String s1 : strings) {
                        location.add(Integer.parseInt(s1));
                    }

                    redSpawns.add(location);

                }

                //Loading blue spawns.
                List<String> blue = Arrays.asList(results.getString(4).split(";"));
                List<List<Integer>> blueSpawns = new ArrayList<>();

                for (String s : blue) {
                    List<Integer> location = new ArrayList<>();
                    List<String> strings = Arrays.asList(s.split(","));
                    for (String s1 : strings) {
                        location.add(Integer.parseInt(s1));
                    }

                    blueSpawns.add(location);

                }

                //Loading TNT spawns.
                List<String> tnt = Arrays.asList(results.getString(5).split(";"));
                List<List<Integer>> tntSpawns = new ArrayList<>();

                for (String s : blue) {
                    List<Integer> location = new ArrayList<>();
                    List<String> strings = Arrays.asList(s.split(","));
                    for (String s1 : strings) {
                        location.add(Integer.parseInt(s1));
                    }

                    tntSpawns.add(location);

                }

                //Getting the world ZIP.
                String zipName = results.getString(6);
                File zip = new File(Bukkit.getPluginManager().getPlugin("HubParkour").getDataFolder().getAbsolutePath()  + "/maps", zipName + ".zip");

                if (!zip.exists()) {
                    Bukkit.getLogger().info("The map " + results.getString(2) + "'s ZIP is missing. Please ZIP the world folder (with the world files in the root of the directory) and place it in this folder. This map will not be included in the map rotation until this is fixed.");
                }

                //Getting the waiting lobby co-ordinates.
                List<String> lobby = Arrays.asList(results.getString(7).split(","));
                List<Integer> waitingLobby = new ArrayList<>();
                for (String s : lobby) {
                    waitingLobby.add(Integer.parseInt(s));
                }

                maps.add(new HPMap(results.getInt(1),results.getString(2),results.getString(8) ,redSpawns, blueSpawns, tntSpawns, zip, waitingLobby));

            }

            CacheManager.setMaps(maps);
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "There has been an error loading Database tables. The plugin will be disabled. Stack Trace:");
            e.printStackTrace();
            Bukkit.getServer().getPluginManager().disablePlugin(Main.getInstance());
        }

    }

    public boolean addSign(Location location, String type) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO `hp_signs`(type, world, x, y, z) VALUES (" + type + ", "+ location.getWorld().getName() + ", " + location.getBlockX() + ", " + location.getBlockY() + ", + " + location.getBlockZ() + ")");
            boolean set = statement.execute();
            return set;
        } catch (Exception e) {
            Bukkit.getLogger().info("Unable to insert Sign locations into database. Please try placing your sign again.");
            return false;
        }
    }

    public void loadSigns() {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM `hp_signs`");
            ResultSet set = statement.executeQuery();

            while (set.next()) {
                Location location = new Location(Bukkit.getWorld(set.getString(3)),set.getDouble(4),set.getDouble(5),set.getDouble(6), 0,0);
                CacheManager.addLocation(location, set.getString(2));
            }

        } catch (Exception e) {
            Bukkit.getLogger().info("Unable to insert Sign locations into database. Please try placing your sign again.");
        }
    }

    public boolean removeSign(Location location, String type) {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM `hp_signs` WHERE (x = " + location.getBlockX() + ") & (y = " + location.getBlockY() + ") & (z = "  + location.getBlockZ() + ") & (world = '" + location.getWorld().getName() + "')");
            boolean set = statement.execute();
            CacheManager.removeSign(location);
            return set;
        } catch (Exception e) {
            Bukkit.getLogger().info("Unable to remove Sign locations from database. Please try placing the sign again and restarting your server.");
            return false;
        }
    }

}
