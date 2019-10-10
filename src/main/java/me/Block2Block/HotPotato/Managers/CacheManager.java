package me.Block2Block.HotPotato.Managers;

import me.Block2Block.HotPotato.Entities.Game;
import me.Block2Block.HotPotato.Entities.HPMap;
import me.Block2Block.HotPotato.Entities.HotPotatoPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;


public class CacheManager {

    private Random rand = new Random();

    private static Map<UUID, HotPotatoPlayer> players = new HashMap<>();
    private static Map<Integer, Game> games = new HashMap<>();
    private static List<HPMap> maps = new ArrayList<>();
    private static Map<Location, String> signs = new HashMap<>();


    public static Map<UUID, HotPotatoPlayer> getPlayers() {
        return players;
    }

    public void addToCache(UUID uuid, HotPotatoPlayer player) {
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
}
