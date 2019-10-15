package me.Block2Block.HotPotato.Managers;

import me.Block2Block.HotPotato.Entities.HotPotatoPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.UUID;

public class PlayerNameManager {

    public static void onGameJoin(HotPotatoPlayer player, int gameId) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (CacheManager.getGames().get(gameId).getPlayers().contains(p)) {

                if (player.isRed()) {
                    CacheManager.getPlayers().get(p.getUniqueId()).getScoreboard().getTeam("Red").addPlayer(player.getPlayer());
                } else {
                    CacheManager.getPlayers().get(p.getUniqueId()).getScoreboard().getTeam("Blue").addPlayer(player.getPlayer());
                }
                if (CacheManager.getPlayers().get(p.getUniqueId()).isRed()) {
                    player.getScoreboard().getTeam("Red").addPlayer(CacheManager.getPlayers().get(p.getUniqueId()).getPlayer());
                } else {
                    player.getScoreboard().getTeam("Blue").addPlayer(CacheManager.getPlayers().get(p.getUniqueId()).getPlayer());
                }

            } else {
                p.hidePlayer(player.getPlayer());
                player.getPlayer().hidePlayer(p);
            }
        }
        if (CacheManager.getPlayers().get(player.getUuid()).isRed()) {
            player.getScoreboard().getTeam("Red").addPlayer(CacheManager.getPlayers().get(player.getUuid()).getPlayer());
        } else {
            player.getScoreboard().getTeam("Blue").addPlayer(CacheManager.getPlayers().get(player.getUuid()).getPlayer());
        }

    }

    public static void onServerJoin(Player p) {
        for (UUID uuid : CacheManager.getPlayers().keySet()) {
            HotPotatoPlayer player = CacheManager.getPlayers().get(uuid);
            player.getPlayer().hidePlayer(p);
            p.hidePlayer(player.getPlayer());
        }
    }

    public static void changeTeam(HotPotatoPlayer player, int gameId) {
        for (Player p : CacheManager.getGames().get(gameId).getPlayers()) {
            if (player.isRed()) {
                CacheManager.getPlayers().get(p.getUniqueId()).getScoreboard().getTeam("Blue").removePlayer(player.getPlayer());
                CacheManager.getPlayers().get(p.getUniqueId()).getScoreboard().getTeam("Red").addPlayer(player.getPlayer());
            } else {
                CacheManager.getPlayers().get(p.getUniqueId()).getScoreboard().getTeam("red").removePlayer(player.getPlayer());
                CacheManager.getPlayers().get(p.getUniqueId()).getScoreboard().getTeam("Blue").addPlayer(player.getPlayer());
            }

        }
    }



}
