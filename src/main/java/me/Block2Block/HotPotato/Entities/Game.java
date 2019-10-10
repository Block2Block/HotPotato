package me.Block2Block.HotPotato.Entities;

import me.Block2Block.HotPotato.Main;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

import static me.Block2Block.HotPotato.Entities.GameState.*;

public class Game {

    private int gameID;
    private GameState state;
    private World world;
    private List<Player> players;
    private int timerTime;
    private BukkitTask timer;
    private HPMap map;
    private List<HotPotatoPlayer> blue;
    private List<HotPotatoPlayer> red;
    private int max;


    public Game(int gameID, HPMap map) {
        state = GameState.WAITING;
        this.gameID = gameID;

        this.map = map;
        map.copy(gameID);
        world = Bukkit.getServer().createWorld(new WorldCreator("HP" + gameID));

        max = map.getBlueSpawns().size() + map.getRedSpawns().size();
    }

    public int getGameID() {return gameID;}

    public World getWorld() {
        return world;
    }

    public GameState getState() {return state;}

    public List<Player> getPlayers() {return players;}

    public List<HotPotatoPlayer> getBlue() {
        return blue;
    }

    public List<HotPotatoPlayer> getRed() {
        return red;
    }

    public void join(List<Player> players) {
        List<Integer> waitingLobby = map.getWaitingLobby();
        this.players.addAll(players);
        if (this.players.size() == max) {
            Main.getQueueManager().noLongerRecruiting();
            startTimer(20);
        } else if (this.players.size() == 2) {
            startTimer(-1);
        }
        for (Player p : players) {
            HotPotatoPlayer hp = new HotPotatoPlayer(p, this.gameID);

            p.teleport(new Location(world, waitingLobby.get(0), waitingLobby.get(1),waitingLobby.get(2),0,0), PlayerTeleportEvent.TeleportCause.PLUGIN);
            p.sendMessage(Main.c("Hot Potato","You have joined a game, id: " + this.gameID));

            //Assigning a team
            boolean kitChosen = false;
            String kit = "None";
            int choose = Main.chooseRan(1, 2);
            boolean onTeam = false;
            String teamJoined = null;

            while (!onTeam) {
                switch (choose) {
                    case 1:
                        if (red.size() != map.getRedSpawns().size()) {
                            if (blue.size() >= red.size()) {
                                red.add(hp);
                                hp.setTeam(true);
                                onTeam = true;
                                //TODO: Set player name in tab
                                teamJoined = "Red";
                                //TODO: Set player's scoreboard
                                break;
                            } else {
                                choose = Main.chooseRan(1, 2);
                                break;
                            }
                        } else {
                            choose = Main.chooseRan(1, 2);
                            break;
                        }
                    case 2:
                        if (blue.size() != map.getBlueSpawns().size()) {
                            if (red.size() >= blue.size()) {
                                blue.add(hp);
                                hp.setTeam(false);
                                onTeam = true;
                                //TODO: Set player name in tab
                                teamJoined = "Blue";
                                //TODO: Set player's scoreboard
                                break;
                            } else {
                                choose = Main.chooseRan(1, 2);
                                break;
                            }
                        } else {
                            choose = Main.chooseRan(1, 2);
                            break;
                        }
                }
            }

        }


    }

    public void startGame() {
        state = INPROGRESS;
        List<List<Integer>> blueSpawns = map.getBlueSpawns();
        List<List<Integer>> redSpawns = map.getRedSpawns();
        //TODO: begin start timer.

        //Teleporting Players
        int counter = 0;
        for (HotPotatoPlayer p : blue) {
            List<Integer> location = blueSpawns.get(counter);
            p.getPlayer().teleport(new Location(world, location.get(0),location.get(1),location.get(2),location.get(3),location.get(4)));
            counter++;
        }
        for (HotPotatoPlayer p : red) {
            List<Integer> location = redSpawns.get(counter);
            p.getPlayer().teleport(new Location(world, location.get(0),location.get(1),location.get(2),location.get(3),location.get(4)));
            counter++;
        }
    }

    public void startTimer(int time) {
        if (state == WAITING) {
            state = STARTING;
            if (time == -1) {
                time = state.getDefaultTime();
            }
            timerTime = time;
            timer = new BukkitRunnable() {
                @Override
                public void run() {
                    timerTime--;
                    for (Player p : players) {
                        p.setLevel(timerTime);
                    }
                    switch (timerTime) {
                        case 60:
                        case 30:
                        case 10:
                        case 5:
                        case 4:
                        case 3:
                        case 2:
                        case 1:
                            for (Player p : players) {
                                p.sendMessage(Main.c("HotPotato","The game will start in &a" + timerTime + " &rsecond" + (timerTime>1?"s":"") + "."));
                                p.playSound(p.getLocation(), Sound.NOTE_PLING,100,1);
                            }
                            break;
                        case 0:
                            startGame();
                            timer.cancel();
                    }



                }
            }.runTaskTimer(Main.getInstance(),0, 20);
        } else {
            if (time < timerTime) {
                timerTime = time;
            }
        }
    }

    public void playerLeave(HotPotatoPlayer p) {
        players.remove(p.getPlayer());
        if (red.contains(p)) {
            red.remove(p);
        } else {
            blue.remove(p);
        }

        for (Player pl : players) {
            pl.sendMessage(Main.c("HotPotato","&a" + p.getName() + "&r has left the game."));
        }
    }
}
