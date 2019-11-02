package me.Block2Block.HotPotato.Entities;

import me.Block2Block.HotPotato.Main;
import me.Block2Block.HotPotato.Managers.Utils.ZipUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

public class HPMap {

    private String name;
    private String author;
    private int id;

    private List<List<Double>> redSpawns;
    private List<List<Double>> blueSpawns;
    private List<List<Double>> tntSpawns;
    private File zip;

    private List<Double> waitingLobby;


    public HPMap(int id, String name, String author, List<List<Double>> red, List<List<Double>> blue, List<List<Double>> tnt, File zip, List<Double> waitingLobby) {
        this.id = id;
        this.name = name;
        this.redSpawns = red;
        this.blueSpawns = blue;
        this.tntSpawns = tnt;
        this.zip = zip;
        this.waitingLobby = waitingLobby;
        this.author = author;
    }

    public String getName() {return name;}

    public List<List<Double>> getRedSpawns() {
        return redSpawns;
    }

    public List<List<Double>> getBlueSpawns() {
        return blueSpawns;
    }

    public List<List<Double>> getTntSpawns() {
        return tntSpawns;
    }

    public void copy(int gameId) {
        try {
            ZipUtil zipUtil = new ZipUtil();
            zipUtil.unzip(zip.getAbsolutePath(),Main.getInstance().getServer().getWorldContainer().getAbsolutePath().substring(0, Main.getInstance().getServer().getWorldContainer().getAbsolutePath().length() - 1) + "HP" + gameId);
        } catch (IOException e) {
            Main.getInstance().getLogger().log(Level.SEVERE, "ERROR COPYING WORLD FILE. STACK TRACE:");
            e.printStackTrace();
        }
    }

    public List<Double> getWaitingLobby(){return waitingLobby;}

    public String getAuthor() {
        return author;
    }

    public int getId() {
        return id;
    }

    public File getZip() {
        return zip;
    }
}
