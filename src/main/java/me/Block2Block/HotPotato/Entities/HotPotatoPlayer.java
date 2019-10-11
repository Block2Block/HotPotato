package me.Block2Block.HotPotato.Entities;

import me.Block2Block.HotPotato.Kits.KitLoader;
import me.Block2Block.HotPotato.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.UUID;

public class HotPotatoPlayer {

    private Player player;
    private UUID uuid;
    private String name;
    private int gameID;
    private Kit kit;
    private Scoreboard scoreboard;
    private Objective objective;
    private boolean team;
    private PlayerData playerData;


    public HotPotatoPlayer(Player player, int gameID, PlayerData pd) {
        this.player = player;
        this.uuid = player.getUniqueId();
        this.name = player.getName();
        this.gameID = gameID;
        this.kit = KitLoader.get().Default();
        this.playerData = pd;
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = scoreboardManager.getNewScoreboard();
        Team red = scoreboard.registerNewTeam("Red");
        red.setPrefix(Main.c(null, "&c"));
        Team blue = scoreboard.registerNewTeam("Blue");
        blue.setPrefix(Main.c(null, "&9"));

        Objective o = scoreboard.registerNewObjective(player.getName(), "dummy");
        o.setDisplaySlot(DisplaySlot.SIDEBAR);
        o.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&9&m----&b&l HOTPOTATO &9&m----"));

        this.objective = o;
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

    public void addKit(int id) {

    }

    public PlayerData getPlayerData() {
        return playerData;
    }
}
