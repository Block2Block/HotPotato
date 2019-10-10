package me.Block2Block.HotPotato.Managers;

import me.Block2Block.HotPotato.Entities.Game;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Score;

import java.util.Set;

public class ScoreboardManager {

    public static void changeLine(Player player, int line, String changeTo) {
        Set<String> entries = CacheManager.getPlayers().get(player.getUniqueId()).getScoreboard().getEntries();
        for (String pl : entries) {
            if (CacheManager.getPlayers().get(player.getUniqueId()).getObjective().getScore(pl).getScore() == line) {
                CacheManager.getPlayers().get(player.getUniqueId()).getScoreboard().resetScores(pl);
            }
        }
        Score score = CacheManager.getPlayers().get(player.getUniqueId()).getObjective().getScore(changeTo);
        score.setScore(line);
        player.setScoreboard(CacheManager.getPlayers().get(player.getUniqueId()).getScoreboard());

    }

    public static void changeLineGame(int gameId, int line, String changeTo) {
        Game game = CacheManager.getGames().get(gameId);
        for (Player player: game.getPlayers()) {
            Set<String> entries = CacheManager.getPlayers().get(player.getUniqueId()).getScoreboard().getEntries();
            for (String pl : entries) {
                if (CacheManager.getPlayers().get(player.getUniqueId()).getObjective().getScore(pl).getScore() == line) {
                    CacheManager.getPlayers().get(player.getUniqueId()).getScoreboard().resetScores(pl);
                }
            }
            Score score = CacheManager.getPlayers().get(player.getUniqueId()).getObjective().getScore(changeTo);
            score.setScore(line);
            player.setScoreboard(CacheManager.getPlayers().get(player.getUniqueId()).getScoreboard());
        }
    }

    public static void resetLine(Player player, int line) {
        Set<String> entries = CacheManager.getPlayers().get(player.getUniqueId()).getScoreboard().getEntries();
        for (String pl : entries) {
            if (CacheManager.getPlayers().get(player.getUniqueId()).getObjective().getScore(pl).getScore() == line) {
                CacheManager.getPlayers().get(player.getUniqueId()).getScoreboard().resetScores(pl);
            }
        }
        player.setScoreboard(CacheManager.getPlayers().get(player.getUniqueId()).getScoreboard());

    }

    public static void resetLineGame(int gameId, int line) {
        Game game = CacheManager.getGames().get(gameId);
        for (Player player: game.getPlayers()) {
            Set<String> entries = CacheManager.getPlayers().get(player.getUniqueId()).getScoreboard().getEntries();
            for (String pl : entries) {
                if (CacheManager.getPlayers().get(player.getUniqueId()).getObjective().getScore(pl).getScore() == line) {
                    CacheManager.getPlayers().get(player.getUniqueId()).getScoreboard().resetScores(pl);
                }
            }
            player.setScoreboard(CacheManager.getPlayers().get(player.getUniqueId()).getScoreboard());

        }
    }



}
