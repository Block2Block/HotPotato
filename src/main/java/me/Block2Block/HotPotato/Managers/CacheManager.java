package me.Block2Block.HotPotato.Managers;

import me.Block2Block.HotPotato.Entities.Game;
import me.Block2Block.HotPotato.Entities.HPMap;
import me.Block2Block.HotPotato.Entities.HotPotatoPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;


public class CacheManager {

    private Random rand = new Random();

    private static Map<UUID, HotPotatoPlayer> players = new HashMap<>();
    private static Map<Integer, Game> games = new HashMap<>();
    private static List<HPMap> maps = new ArrayList<>();
    private static Map<Location, String> signs = new HashMap<>();
    private static Location lobby = new Location(Bukkit.getWorld("world"), -17, 70.0, -163.0, 0, 0);
    private static Map<Player, World> editMode = new HashMap<>();
    private static Player setupMode;
    private static int setupStage = -1;
    private static List<String> setupData = new ArrayList<>();
    private static Player finishPlayer;


    public static Map<UUID, HotPotatoPlayer> getPlayers() {
        return players;
    }

    public static void addToCache(UUID uuid, HotPotatoPlayer player) {
        players.put(uuid, player);
    }

    public static Map<Integer, Game> getGames() {
        return games;
    }

    public static List<HPMap> getMaps() {return maps;}

    public static void newGame(Game game, int gameId) {
        games.put(gameId, game);
    }

    public static void playerLeave(Player p) {
        if (players.containsKey(p.getUniqueId())) {
            HotPotatoPlayer player = players.remove(p.getUniqueId());
            games.get(player.getGameID()).playerLeave(player);
        }
    }

    public static void setMaps(List<HPMap> mapsList){maps = mapsList;}

    public static void addMap(HPMap map) {maps.add(map);}

    public static void removeMap(int index) {maps.remove(index);}

    public static void addLocation(Location signLocation, String type) {signs.put(signLocation,type);}

    public static String isSign(Location signLocation) {if (signs.containsKey(signLocation)) {return signs.get(signLocation);} return "NONE";}

    public static void removeSign(Location location) {
        signs.remove(location);
    }

    public static Map<Location, String> getSigns() {
        return signs;
    }

    public static void setLobby(Location lobbyLocation) {lobby = lobbyLocation;}

    public static Location getLobby() {return lobby;}

    public static Map<Player, World> getEditMode() {
        return editMode;
    }

    public static void addEditor(Player p, World map) {
        editMode.put(p, map);
    }

    public static boolean isEditor(Player p) {
        if (editMode.containsKey(p)) {
            return true;
        }
        return false;
    }

    public static void removeEditor(Player p) {
        editMode.remove(p);
    }

    public static void onGameEnd(List<Player> gameplayers, int gameId) {
        for (Player p : gameplayers) {
            players.remove(p.getUniqueId());
        }
        games.remove(gameId);
    }

    public static void enterSetup (Player p) {
        setupMode = p;
    }

    public static boolean isSetup(Player p) {
        if (setupMode == null) {
            return false;
        }
        if (setupMode.equals(p)) {
            return true;
        }
        return false;
    }

    public static void exitSetup() {
        setupMode = null;
        setupStage = -1;
    }

    public static void setSetupStage(int i) {setupStage = i;}

    public static int getSetupStage() {return setupStage;}

    public static void addData(String data) {setupData.add(data);}

    public static List<String> getData() {
        return setupData;
    }

    public static Player getFinishPlayer() {return finishPlayer;}

    public static void setFinishPlayer(Player finishPlayer) {
        CacheManager.finishPlayer = finishPlayer;
    }
}
