package me.block2block.hotpotato.entities;

import me.block2block.hotpotato.Main;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class PlayerData {

    private List<Integer> unlockedKits;
    private int balance;
    private int wins;
    private int gamesPlayed;
    private int winningPunch;
    private Player p;


    public PlayerData(Player p) {
            this.unlockedKits = Main.getDbManager().getKits(p);
            List<Integer> stats = Main.getDbManager().getStats(p);
            this.balance = stats.get(0);
            this.wins = stats.get(1);
            this.gamesPlayed = stats.get(2);
            this.winningPunch = stats.get(3);
            this.p = p;
    }

    public int getBalance() {
        if (!Main.getInstance().getConfig().getBoolean("Settings.Economy.Use-Custom-Economy") && Main.isVault()) {
            return (int) Math.round(Main.getEcon().getBalance(p));
        }
        return balance;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public int getWinningPunch() {
        return winningPunch;
    }

    public int getWins() {
        return wins;
    }

    public List<Integer> getUnlockedKits() {
        return unlockedKits;
    }

    public void addKit(int id) {
        unlockedKits.add(id);
        new BukkitRunnable() {
            @Override
            public void run() {
                Main.getDbManager().addKit(id, p);
            }
        }.runTaskAsynchronously(Main.getInstance());
    }

    public void removeFromBalance(int amount) {
        if (!Main.getInstance().getConfig().getBoolean("Settings.Economy.Use-Custom-Economy") && Main.isVault()) {
            Main.getEcon().withdrawPlayer(p, amount);
            return;
        }
        balance -= amount;
        new BukkitRunnable() {
            @Override
            public void run() {
                Main.getDbManager().removeFromBalance(p, amount);
            }
        }.runTaskAsynchronously(Main.getInstance());
    }

    public void addWin() {
        wins++;
        gamesPlayed++;
        new BukkitRunnable() {
            @Override
            public void run() {
                Main.getDbManager().addWin(p);
            }
        }.runTaskAsynchronously(Main.getInstance());
    }

    public void addWinningPunch() {
        winningPunch++;
        wins++;
        gamesPlayed++;
        new BukkitRunnable() {
            @Override
            public void run() {
                Main.getDbManager().addWinningPunch(p);
            }
        }.runTaskAsynchronously(Main.getInstance());
    }

    public void addLoss() {
        gamesPlayed++;
        new BukkitRunnable() {
            @Override
            public void run() {
                Main.getDbManager().addLoss(p);
            }
        }.runTaskAsynchronously(Main.getInstance());
    }

    public void addBalance(int i) {
        balance += i;
    }

}
