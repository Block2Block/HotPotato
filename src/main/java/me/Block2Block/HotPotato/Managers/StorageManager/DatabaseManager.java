package me.Block2Block.HotPotato.Managers.StorageManager;

import me.Block2Block.HotPotato.Entities.HPMap;
import me.Block2Block.HotPotato.Main;
import me.Block2Block.HotPotato.Managers.CacheManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.File;
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
    private static MySQL dbMySql;
    private static boolean isMysql;
    private static Connection connection;

    public DatabaseManager(boolean mysql) {
        isMysql = mysql;
        i = this;
    }

    public boolean setup() throws SQLException, ClassNotFoundException {
        db = new SQLite("storage.db");
        if (isMysql) {
            dbMySql = new MySQL(Main.getInstance().getConfig().getString("Settings.Database.MySQL.Hostname"), Main.getInstance().getConfig().getString("Settings.Database.MySQL.Port"),Main.getInstance().getConfig().getString("Settings.Database.MySQL.Database"), Main.getInstance().getConfig().getString("Settings.Database.MySQL.Username"), Main.getInstance().getConfig().getString("Settings.Database.MySQL.Password"));
            connection = dbMySql.openConnection();
        } else {
            db = new SQLite("storage.db");
            connection = db.openConnection();
        }

        boolean successful =  createTables();
        if (!successful) {
            return false;
        }
        loadMaps();
        loadSigns();
        loadLobby();
        return true;
    }

    private boolean createTables() {
        try {
            if (isMysql) {
                PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS hp_maps (`id` INT NOT NULL AUTO_INCREMENT , `name` TEXT NOT NULL , `red_spawns` TEXT NOT NULL , `blue_spawns` TEXT NOT NULL , `tnt_spawns` TEXT NOT NULL , `zip_name` TEXT NOT NULL , `waiting_lobby` TEXT NOT NULL , `author` TEXT NOT NULL, PRIMARY KEY (`id`))");
                boolean set = statement.execute();

                statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS hp_signs ( `id` INT NOT NULL AUTO_INCREMENT, `type` TEXT NOT NULL , `world` TEXT NOT NULL , `x` INT NOT NULL , `y` INT NOT NULL , `z` INT NOT NULL,PRIMARY KEY (`id`))");
                set = statement.execute();

                statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS hp_playerdata ( `uuid` VARCHAR(36) NOT NULL , `balance` INT NOT NULL , `kits_unlocked` TEXT NOT NULL , `wins` INT NOT NULL , `games_played` INT NOT NULL , `winning_punch` INT NOT NULL )");
                set = statement.execute();

                statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS hp_data ( `world` TEXT NOT NULL , `x` REAL NOT NULL , `y` REAL NOT NULL , `z` REAL NOT NULL)");
                set = statement.execute();
            } else {
                PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS hp_maps ( `id` INT PRIMARY KEY AUTOINCREMENT , `name` TEXT NOT NULL , `red_spawns` TEXT NOT NULL , `blue_spawns` TEXT NOT NULL , `tnt_spawns` TEXT NOT NULL , `zip_name` TEXT NOT NULL , `waiting_lobby` TEXT NOT NULL , `author` TEXT NOT NULL)");
                boolean set = statement.execute();

                statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS hp_signs ( `id` INTEGER PRIMARY KEY AUTOINCREMENT, `type` TEXT NOT NULL , `world` TEXT NOT NULL , `x` INTEGER NOT NULL , `y` INTEGER NOT NULL , `z` INTEGER NOT NULL)");
                set = statement.execute();

                statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS hp_playerdata ( `uuid` VARCHAR(36) NOT NULL , `balance` INTEGER NOT NULL , `kits_unlocked` TEXT NOT NULL , `wins` INTEGER NOT NULL , `games_played` INTEGER NOT NULL , `winning_punch` INTEGER NOT NULL )");
                set = statement.execute();

                statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS hp_data ( `world` TEXT NOT NULL , `x` REAL NOT NULL , `y` REAL NOT NULL , `z` REAL NOT NULL)");
                set = statement.execute();
            }
            return true;

        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "There has been an error creating Database tables. The plugin will be disabled. Stack Trace:");
            e.printStackTrace();
            Bukkit.getServer().getPluginManager().disablePlugin(Main.getInstance());
            return false;
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
                List<List<Double>> redSpawns = new ArrayList<>();

                for (String s : red) {
                    List<Double> location = new ArrayList<>();
                    List<String> strings = Arrays.asList(s.split(","));
                    for (String s1 : strings) {
                        location.add(Double.parseDouble(s1));
                    }

                    redSpawns.add(location);

                }

                //Loading blue spawns.
                List<String> blue = Arrays.asList(results.getString(4).split(";"));
                List<List<Double>> blueSpawns = new ArrayList<>();

                for (String s : blue) {
                    List<Double> location = new ArrayList<>();
                    List<String> strings = Arrays.asList(s.split(","));
                    for (String s1 : strings) {
                        location.add(Double.parseDouble(s1));
                    }

                    blueSpawns.add(location);

                }

                //Loading TNT spawns.
                List<String> tnt = Arrays.asList(results.getString(5).split(";"));
                List<List<Double>> tntSpawns = new ArrayList<>();

                for (String s : tnt) {
                    List<Double> location = new ArrayList<>();
                    List<String> strings = Arrays.asList(s.split(","));
                    for (String s1 : strings) {
                        location.add(Double.parseDouble(s1));
                    }

                    tntSpawns.add(location);

                }

                //Getting the world ZIP.
                String zipName = results.getString(6);
                File dataFolder = new File(Main.getInstance().getDataFolder().getAbsolutePath() + "/maps");
                File zip = new File(dataFolder, zipName + ".zip");

                if (!zip.exists()) {
                    Bukkit.getLogger().info("The map " + results.getString(2) + "'s ZIP is missing. Please ZIP the world folder (with the world files in the root of the directory) and place it in this folder. This map will not be included in the map rotation until this is fixed.");
                    continue;
                }

                //Getting the waiting lobby co-ordinates.
                List<String> lobby = Arrays.asList(results.getString(7).split(","));
                List<Double> waitingLobby = new ArrayList<>();
                for (String s : lobby) {
                    waitingLobby.add(Double.parseDouble(s));
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

    public void loadLobby() {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM hp_data");
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                if (Bukkit.getWorld(rs.getString(1))== null) {
                    Bukkit.getLogger().info("The world your previous lobby location was in has been deleted. Please re-set your lobby location in order to play games. If the world still exists, please ensure that the world is loaded on server start.");
                    CacheManager.setLobby(null);
                } else {
                    Location l = new Location(Bukkit.getWorld(rs.getString(1)),rs.getDouble(2),rs.getDouble(3),rs.getDouble(4));
                    CacheManager.setLobby(l);
                }
            } else {
                Bukkit.getLogger().info("Your lobby has not yet been set. Execute /hotpotato setlobby to set your lobby. You will not be able to set up maps until you do.");
                CacheManager.setLobby(null);
            }
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "There has been an error loading Database tables. The plugin will be disabled. Stack Trace:");
            e.printStackTrace();
            Bukkit.getServer().getPluginManager().disablePlugin(Main.getInstance());
        }
    }

    public boolean addSign(Location location, String type) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO `hp_signs`(type, world, x, y, z) VALUES ('" + type + "', '"+ location.getWorld().getName() + "', " + location.getBlockX() + ", " + location.getBlockY() + ", + " + location.getBlockZ() + ")");
            boolean set = statement.execute();
            return set;
        } catch (Exception e) {
            Bukkit.getLogger().info("Unable to insert Sign locations into database. Please try placing your sign again. Stack Trace:");
            e.printStackTrace();
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

    public List<Integer> getKits(Player p) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT kits_unlocked FROM hp_playerdata WHERE uuid = '" + p.getUniqueId().toString() + "'");
            ResultSet set = statement.executeQuery();

            if (set.next()) {
                String s = set.getString(1);
                List<String> strings = Arrays.asList(s.split(","));

                List<Integer> kits = new ArrayList<>();
                for (String s1 : strings) {
                    kits.add(Integer.parseInt(s1));
                }
                return kits;
            } else {
                statement = connection.prepareStatement("INSERT INTO hp_playerdata(uuid, balance, kits_unlocked, wins, games_played, winning_punch) VALUES ('" + p.getUniqueId().toString() + "',0,'0',0,0,0)");
                boolean execute = statement.execute();
                List<Integer> kits = new ArrayList<>();
                kits.add(0);
                return kits;
            }
        } catch (Exception e) {
            Main.getInstance().getLogger().info("There has been an error getting player statistics. Stack Trace:");
            e.printStackTrace();
            return null;
        }
    }

    public void addKit(int id, Player p) {
        try {
            if (isMysql) {
                PreparedStatement statement = connection.prepareStatement("UPDATE hp_playerdata SET kits_unlocked = CONCAT(kits_unlocked, '," + id + "') WHERE uuid = '" + p.getUniqueId().toString() + "'");
                boolean set = statement.execute();
                return;
            }
            PreparedStatement statement = connection.prepareStatement("UPDATE hp_playerdata SET kits_unlocked = (kits_unlocked || '," + id + "') WHERE uuid = '" + p.getUniqueId().toString() + "'");
            boolean set = statement.execute();
        } catch (Exception e) {
            Bukkit.getLogger().info("Unable to add kits to database. Please try restarting your server.");
            e.printStackTrace();
        }
    }

    public List<Integer> getStats(Player p) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT balance,wins,games_played,winning_punch FROM hp_playerdata WHERE uuid = '" + p.getUniqueId().toString() + "'");
            ResultSet set = statement.executeQuery();

            if (set.next()) {
                List<Integer> kits = new ArrayList<>();
                    if (!Main.getInstance().getConfig().getBoolean("Settings.Economy.Use-Custom-Economy") && Main.isVault()) {
                        kits.add((int) Math.round(Main.getEcon().getBalance(p)));
                    } else {
                        kits.add(set.getInt(1));
                    }

                kits.add(set.getInt(2));
                kits.add(set.getInt(3));
                kits.add(set.getInt(4));
                return kits;
            } else {
                statement = connection.prepareStatement("INSERT INTO hp_playerdata(uuid, balance, kits_unlocked, wins, games_played, winning_punch) VALUES ('" + p.getUniqueId().toString() + "',0,'0',0,0,0)");
                boolean execute = statement.execute();
                List<Integer> kits = new ArrayList<>();
                kits.add(0);
                kits.add(0);
                kits.add(0);
                kits.add(0);
                return kits;
            }
        } catch (Exception e) {
            Main.getInstance().getLogger().info("There has been an error getting player statistics. Stack Trace:");
            e.printStackTrace();
            return null;
        }
    }

    public void removeFromBalance(Player p, int balance) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE hp_playerdata SET balance = (balance - " + balance + ") WHERE uuid = '" + p.getUniqueId().toString() + "'");
            boolean set = statement.execute();
        } catch (Exception e) {
            Bukkit.getLogger().info("Unable to update balances in the database. Please try restarting your server.");
        }
    }

     public void addWin(Player p) {
         try {
             if (!Main.getInstance().getConfig().getBoolean("Settings.Economy.Use-Custom-Economy") && Main.isVault()) {
                 Main.getEcon().depositPlayer(p, Main.getInstance().getConfig().getInt("Settings.Economy.Money-On-Win"));
                 PreparedStatement statement = connection.prepareStatement("UPDATE hp_playerdata SET (wins = wins + 1), games_played = (games_played + 1) WHERE uuid = '" + p.getUniqueId().toString() + "'");
                 boolean set = statement.execute();
                 return;
             }
             PreparedStatement statement = connection.prepareStatement("UPDATE hp_playerdata SET (wins = wins + 1), games_played = (games_played + 1), balance = (balance + " + Main.getInstance().getConfig().getInt("Settings.Economy.Money-On-Win") + ") WHERE uuid = '" + p.getUniqueId().toString() + "'");
             boolean set = statement.execute();
         } catch (Exception e) {
             Bukkit.getLogger().info("Unable to add stats to database. Please try restarting your server.");
         }
     }

    public void addWinningPunch(Player p) {
        try {
            if (!Main.getInstance().getConfig().getBoolean("Settings.Economy.Use-Custom-Economy") && Main.isVault()) {
                Main.getEcon().depositPlayer(p, Main.getInstance().getConfig().getInt("Settings.Economy.Money-On-Winning-Punch"));
                PreparedStatement statement = connection.prepareStatement("UPDATE hp_playerdata SET (wins = wins + 1), games_played = (games_played + 1), winning_punch = (winning_punch + 1) WHERE uuid = '" + p.getUniqueId().toString() + "'");
                boolean set = statement.execute();
                return;
            }
            PreparedStatement statement = connection.prepareStatement("UPDATE hp_playerdata SET wins = (wins + 1), games_played = (games_played + 1), winning_punch = (winning_punch + 1), balance = (balance + " + Main.getInstance().getConfig().getInt("Settings.Economy.Money-On-Winning-Punch") + ") WHERE uuid = '" + p.getUniqueId().toString() + "'");
            boolean set = statement.execute();
        } catch (Exception e) {
            Bukkit.getLogger().info("Unable to add stats to database. Please try restarting your server.");
        }
    }

    public void addLoss(Player p) {
        try {
            if (!Main.getInstance().getConfig().getBoolean("Settings.Economy.Use-Custom-Economy") && Main.isVault()) {
                Main.getEcon().depositPlayer(p, Main.getInstance().getConfig().getInt("Settings.Economy.Money-On-Loss"));
                PreparedStatement statement = connection.prepareStatement("UPDATE hp_playerdata SET games_played = (games_played + 1) WHERE uuid = '" + p.getUniqueId().toString() + "'");
                boolean set = statement.execute();
                return;
            }
            PreparedStatement statement = connection.prepareStatement("UPDATE hp_playerdata SET games_played = (games_played + 1), balance = (balance + " + Main.getInstance().getConfig().getInt("Settings.Economy.Money-On-Loss") + ") WHERE uuid = '" + p.getUniqueId().toString() + "'");
            boolean set = statement.execute();
        } catch (Exception e) {
            Bukkit.getLogger().info("Unable to add stats to database. Please try restarting your server.");
        }
    }

    public void setLobby(Location l) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM hp_data");
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                statement = connection.prepareStatement("UPDATE hp_data SET (world='" + l.getWorld().getName() + "'), (x = " + l.getX() +"), (y=" + l.getY() + "), (z=" + l.getZ() + ")");
                boolean set = statement.execute();
            } else {
                statement = connection.prepareStatement("INSERT INTO hp_data(world, x, y, z) VALUES ('" + l.getWorld().getName() + "', " + l.getX() +", " + l.getY() + ", " + l.getZ() + ")");
                boolean set = statement.execute();
            }
        } catch (Exception e) {
            Bukkit.getLogger().info("Unable to set lobby location in Database, please retry the command or restart your server and try again.");
        }
    }

    public void addMap(List<String> data) {

        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO hp_maps(name, red_spawns, blue_spawns, tnt_spawns, zip_name, waiting_lobby, author) VALUES ('" + data.get(4) + "','" + data.get(0) + "', '" + data.get(1) + "', '" + data.get(2) + "','" + data.get(6) + "','" + data.get(3) + "','" + data.get(5) + "')");
            boolean set = statement.execute();

            statement = connection.prepareStatement("SELECT * from hp_maps WHERE zip_name = '" + data.get(6) + "'");
            ResultSet results = statement.executeQuery();

            if (!results.next()) {
                Bukkit.getLogger().info("There has been an issue getting the info from the database. Please reload your server to load the map in properly.");
                return;
            }


            //Loading red spawns.
            List<String> red = Arrays.asList(results.getString(3).split(";"));
            List<List<Double>> redSpawns = new ArrayList<>();

            for (String s : red) {
                List<Double> location = new ArrayList<>();
                List<String> strings = Arrays.asList(s.split(","));
                for (String s1 : strings) {
                    location.add(Double.parseDouble(s1));
                }

                redSpawns.add(location);

            }

            //Loading blue spawns.
            List<String> blue = Arrays.asList(results.getString(4).split(";"));
            List<List<Double>> blueSpawns = new ArrayList<>();

            for (String s : blue) {
                List<Double> location = new ArrayList<>();
                List<String> strings = Arrays.asList(s.split(","));
                for (String s1 : strings) {
                    location.add(Double.parseDouble(s1));
                }

                blueSpawns.add(location);

            }

            //Loading TNT spawns.
            List<String> tnt = Arrays.asList(results.getString(5).split(";"));
            List<List<Double>> tntSpawns = new ArrayList<>();

            for (String s : tnt) {
                List<Double> location = new ArrayList<>();
                List<String> strings = Arrays.asList(s.split(","));
                for (String s1 : strings) {
                    location.add(Double.parseDouble(s1));
                }

                tntSpawns.add(location);

            }

            //Getting the world ZIP.
            String zipName = results.getString(6);
            File dataFolder = new File(Main.getInstance().getDataFolder().getAbsolutePath() + "/maps");
            File zip = new File(dataFolder, zipName + ".zip");

            if (!zip.exists()) {
                Bukkit.getLogger().info("The map " + results.getString(2) + "'s ZIP is missing. Please ZIP the world folder (with the world files in the root of the directory) and place it in this folder. This map will not be included in the map rotation until this is fixed.");
                return;
            }

            //Getting the waiting lobby co-ordinates.
            List<String> lobby = Arrays.asList(results.getString(7).split(","));
            List<Double> waitingLobby = new ArrayList<>();
            for (String s : lobby) {
                waitingLobby.add(Double.parseDouble(s));
            }

            CacheManager.addMap(new HPMap(results.getInt(1), results.getString(2), results.getString(8), redSpawns, blueSpawns, tntSpawns, zip, waitingLobby));

        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "There has been an error adding maps to the database. Stack Trace:");
            e.printStackTrace();
        }

    }

    public boolean addBalance(int amount, String uuid) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM hp_playerdata WHERE uuid = '" + uuid + "'");
            ResultSet rs = statement.executeQuery();

            if (!rs.next()) {
                return false;
            }

            statement = connection.prepareStatement("UPDATE hp_playerdata SET balance = (balance + " + amount + ") WHERE uuid = '" + uuid + "'");
            boolean set = statement.execute();

            return true;
        } catch (Exception e) {
            Bukkit.getLogger().info("Unable to update balances in the database. Please try restarting your server.");
            return false;
        }
    }

    public void removeMap(HPMap map) {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM `hp_maps` WHERE id = " + map.getId() + "");
            boolean set = statement.execute();
        } catch (Exception e) {
            Bukkit.getLogger().info("Unable update balances in the database. Please try restarting your server.");
            return;
        }
    }

}
