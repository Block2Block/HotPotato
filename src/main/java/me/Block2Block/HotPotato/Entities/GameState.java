package me.Block2Block.HotPotato.Entities;

import me.Block2Block.HotPotato.Main;

public enum GameState {

    DEAD(1,"Dead",-1),
    WAITING(2, "Waiting",-1),
    STARTING(3,"Starting", Main.getInstance().getConfig().getInt("Settings.Game.Default-Countdown-Time") + 1),
    INPROGRESS(4,"In Progress",-1),
    ENDING(5,"Ending",Main.getInstance().getConfig().getInt("Settings.Game.End-Countdown-Time") + 1);

    private int id;
    private String name;
    private int defaultTime;

    GameState(int id, String name, int defaultTime) {
        this.id = id;
        this.name = name;
        this.defaultTime = defaultTime;
    }

    public int getDefaultTime() {
        return defaultTime;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
