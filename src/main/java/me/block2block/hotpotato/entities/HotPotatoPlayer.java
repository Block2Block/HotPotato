package me.block2block.hotpotato.entities;

import com.nametagedit.plugin.NametagEdit;
import com.nametagedit.plugin.api.data.FakeTeam;
import me.block2block.hotpotato.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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


    public HotPotatoPlayer(Player player, int gameID) {
        this.player = player;
        this.uuid = player.getUniqueId();
        this.name = player.getName();
        this.gameID = gameID;
        this.kit = Main.getKitLoader().get().Default();
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = scoreboardManager.getNewScoreboard();
        Team red = scoreboard.registerNewTeam("Red");
        red.setPrefix(Main.c(false, Main.getInstance().getConfig().getString("Settings.Player-Names.Red-Format")));
        Team blue = scoreboard.registerNewTeam("Blue");
        blue.setPrefix(Main.c(false, Main.getInstance().getConfig().getString("Settings.Player-Names.Blue-Format")));

        String name = player.getName();
        if (me.block2block.hotpotato.managers.ScoreboardManager.isEnabled() && !me.block2block.hotpotato.managers.ScoreboardManager.isCustomScoreboard() && Main.isNte()) {
            FakeTeam team = NametagEdit.getApi().getFakeTeam(player.getPlayer());
            name = team.getName();
        }
        Objective o = scoreboard.registerNewObjective(name, "dummy");
        o.setDisplaySlot(DisplaySlot.SIDEBAR);
        o.setDisplayName(ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString("Settings.Scoreboard.Title")));

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
        playerData.addKit(id);
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    public void setPlayerData(PlayerData playerData) {
        this.playerData = playerData;
    }

    public void applyKit() {
        player.getInventory().clear();
        player.getEquipment().setArmorContents(new ItemStack[]{kit.boots(), kit.leggings(), kit.chestplate(), kit.helmet()});
        for (int i = 0; i<kit.hb().length; i++) {
            ItemStack s = kit.hb()[i];
            if (s != null) player.getInventory().setItem(i, s);
        }
        for (int i = 0; i<kit.i().length; i++) {
            ItemStack s = kit.i()[i];
            if (s != null) player.getInventory().setItem(i+9, s);
        }
    }

}
