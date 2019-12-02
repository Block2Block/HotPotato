package me.block2block.hotpotato.managers;

import me.block2block.hotpotato.entities.Game;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Score;

import java.util.Set;

public class ScoreboardManager {

    private static boolean isEnabled = false;
    private static boolean customScoreboard = false;

    public static void changeLine(Player player, int line, String changeTo) {
        if (isEnabled && customScoreboard) {
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

    public static void changeLineGame(int gameId, int line, String changeTo) {
        if (isEnabled && customScoreboard) {
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
    }

    public static void resetLine(Player player, int line) {
        if (isEnabled && customScoreboard) {
            Set<String> entries = CacheManager.getPlayers().get(player.getUniqueId()).getScoreboard().getEntries();
            for (String pl : entries) {
                if (CacheManager.getPlayers().get(player.getUniqueId()).getObjective().getScore(pl).getScore() == line) {
                    CacheManager.getPlayers().get(player.getUniqueId()).getScoreboard().resetScores(pl);
                }
            }
            player.setScoreboard(CacheManager.getPlayers().get(player.getUniqueId()).getScoreboard());
        }
    }

    public static void resetLineGame(int gameId, int line) {
        if (isEnabled && customScoreboard) {
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

    public static void resetBoardGame(int gameId) {
        if (isEnabled && customScoreboard) {
            Game game = CacheManager.getGames().get(gameId);
            for (Player player : game.getPlayers()) {
                Set<String> entries = CacheManager.getPlayers().get(player.getUniqueId()).getScoreboard().getEntries();
                for (String pl : entries) {
                    CacheManager.getPlayers().get(player.getUniqueId()).getScoreboard().resetScores(pl);
                }
                player.setScoreboard(CacheManager.getPlayers().get(player.getUniqueId()).getScoreboard());

            }
        }

    }

    public static void resetBoard(Player player) {
        if (isEnabled && customScoreboard) {
            Set<String> entries = CacheManager.getPlayers().get(player.getUniqueId()).getScoreboard().getEntries();
            for (String pl : entries) {
                CacheManager.getPlayers().get(player.getUniqueId()).getScoreboard().resetScores(pl);
            }
            player.setScoreboard(CacheManager.getPlayers().get(player.getUniqueId()).getScoreboard());

        }
    }

    public static boolean isCustomScoreboard() {
        return customScoreboard;
    }

    public static boolean isEnabled() {
        return isEnabled;
    }

    public static void setEnabled(boolean enabled) {isEnabled = enabled;}

    public static void setCustomScoreboard(boolean customScoreboard1) {customScoreboard = customScoreboard1;}
}
