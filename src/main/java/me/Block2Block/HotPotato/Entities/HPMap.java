package me.Block2Block.HotPotato.Entities;

import me.Block2Block.HotPotato.Main;
import me.Block2Block.HotPotato.Managers.UnzipUtility;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

public class HPMap {

    private String name;
    private String author;

    private List<List<Integer>> redSpawns;
    private List<List<Integer>> blueSpawns;
    private List<List<Integer>> tntSpawns;
    private File zip;

    private List<Integer> waitingLobby;


    public HPMap(String name, String author, List<List<Integer>> red, List<List<Integer>> blue, List<List<Integer>> tnt, File zip, List<Integer> waitingLobby) {
        this.name = name;
        this.redSpawns = red;
        this.blueSpawns = blue;
        this.tntSpawns = tnt;
        this.zip = zip;
        this.waitingLobby = waitingLobby;
        this.author = author;
    }

    public String getName() {return name;}

    public List<List<Integer>> getRedSpawns() {
        return redSpawns;
    }

    public List<List<Integer>> getBlueSpawns() {
        return blueSpawns;
    }

    public List<List<Integer>> getTntSpawns() {
        return tntSpawns;
    }

    public void copy(int gameId) {
        try {
            UnzipUtility unzipUtility = new UnzipUtility();
            unzipUtility.unzip(Main.getInstance().getDataFolder().getAbsolutePath() + "/maps/" + name + ".zip",Bukkit.getServer().getWorldContainer().getAbsolutePath() + "HP" + gameId);
        } catch (IOException e) {
            Main.getInstance().getLogger().log(Level.SEVERE, "ERROR COPYING WORLD FILE. STACK TRACE:");
            e.printStackTrace();
        }
    }

    public List<Integer> getWaitingLobby(){return waitingLobby;}

    public String getAuthor() {
        return author;
    }
}
