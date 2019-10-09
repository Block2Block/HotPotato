package me.Block2Block.HotPotato.Entities;

import me.Block2Block.HotPotato.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.UUID;

import static me.Block2Block.HotPotato.Entities.Kit.*;

public class HotPotatoPlayer {

    private Player player;
    private UUID uuid;
    private String name;
    private int gameID;
    private Kit kit;
    private Scoreboard scoreboard;
    private Objective objective;
    private boolean team;


    public HotPotatoPlayer(Player player, int gameID) {
        this.player = player;
        this.uuid = player.getUniqueId();
        this.name = player.getName();
        this.gameID = gameID;
        this.kit = DEFAULT;
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = scoreboardManager.getNewScoreboard();
        Team red = scoreboard.registerNewTeam("Red");
        red.setPrefix(Main.c(null, "&c"));
        Team blue = scoreboard.registerNewTeam("Blue");
        blue.setPrefix(Main.c(null, "&9"));
        this.scoreboard = scoreboard;
    }

    public String getName() {
        return name;
    }

    public int getGameID() {
        return gameID;
    }

    public Player getPlayer() {
        return player;
    }

    public Kit getKit() {
        return kit;
    }

    public void setKit(Kit kit) {
        this.kit = kit;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public void setScoreboard(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    public Objective getObjective() {
        return objective;
    }

    public void setObjective(Objective objective) {
        this.objective = objective;
    }

    public boolean isRed() {
        return team;
    }

    public void setTeam(boolean team) {
        this.team = team;
    }
}
