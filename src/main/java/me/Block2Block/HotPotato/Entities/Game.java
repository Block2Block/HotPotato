package me.Block2Block.HotPotato.Entities;

import me.Block2Block.HotPotato.Kits.KitLoader;
import me.Block2Block.HotPotato.Main;
import me.Block2Block.HotPotato.Managers.CacheManager;
import me.Block2Block.HotPotato.Managers.PlayerNameManager;
import me.Block2Block.HotPotato.Managers.ScoreboardManager;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static me.Block2Block.HotPotato.Entities.GameState.*;

public class Game implements Listener {

    private int gameID;
    private GameState state;
    private World world;
    private List<Player> players = new ArrayList<>();
    private int timerTime;
    private BukkitTask timer;
    private HPMap map;
    private List<HotPotatoPlayer> blue = new ArrayList<>();
    private List<HotPotatoPlayer> red = new ArrayList<>();
    private int max;

    private int livesBlue;
    private int livesRed;
    private Location tntLocation;
    private FallingBlock fallingBlock;
    private boolean inAir;

    private List<Player> queueRed = new ArrayList<>();
    private List<Player> queueBlue = new ArrayList<>();


    public Game(int gameID, HPMap map) {
        state = GameState.WAITING;
        this.gameID = gameID;

        this.map = map;
        map.copy(gameID);
        world = Bukkit.getServer().createWorld(new WorldCreator("HP" + gameID));

        max = map.getBlueSpawns().size() + map.getRedSpawns().size();

        Bukkit.getServer().getPluginManager().registerEvents(this, Main.getInstance());

        inAir = false;
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
        } else if (this.players.size() >= 2) {
            startTimer(-1);
        }
        for (Player p : players) {
            HotPotatoPlayer hp = new HotPotatoPlayer(p, this.gameID);
            CacheManager.addToCache(p.getUniqueId(), hp);

            p.teleport(new Location(world, waitingLobby.get(0), waitingLobby.get(1),waitingLobby.get(2),0,0), PlayerTeleportEvent.TeleportCause.PLUGIN);
            p.sendMessage(Main.c("HotPotato","You have joined a game, id: " + this.gameID));
            for (Player p2 : players) {
                p2.sendMessage(Main.c("HotPotato","&a" + p.getName() + "&r has joined the game."));
            }

            //Assigning a team
            String kit = KitLoader.get().Default().name();
            hp.setKit(KitLoader.get().Default());
            int choose = Main.chooseRan(1, 2);
            boolean onTeam = false;

            while (!onTeam) {
                switch (choose) {
                    case 1:
                        if (red.size() != map.getRedSpawns().size()) {
                            if (blue.size() >= red.size()) {
                                if (queueRed.size() > 0) {
                                    //If there is a queued user, give them priority.
                                    Player player = queueRed.remove(0);

                                    HotPotatoPlayer hotplayer = CacheManager.getPlayers().get(player.getUniqueId());
                                    hotplayer.setTeam(true);
                                    ScoreboardManager.changeLine(player, 8, Main.c(null,"&cRed"));
                                    player.sendMessage(Main.c("HotPotato","You have joined &cRed&r team."));
                                    PlayerNameManager.changeTeam(hotplayer, gameID);

                                    hp.setTeam(false);
                                    onTeam = true;
                                    PlayerNameManager.onGameJoin(hp, gameID);

                                    ScoreboardManager.changeLine(p, 8, Main.c(null,"&3Blue"));
                                    break;
                                }
                                hp.setTeam(true);
                                onTeam = true;
                                PlayerNameManager.onGameJoin(hp, gameID);

                                ScoreboardManager.changeLine(p, 8, Main.c(null,"&cRed"));

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
                                if (queueBlue.size() > 0) {
                                    //If there is a queued user, give them priority.
                                    Player player = queueRed.remove(0);

                                    HotPotatoPlayer hotplayer = CacheManager.getPlayers().get(player.getUniqueId());
                                    hotplayer.setTeam(false);
                                    ScoreboardManager.changeLine(player, 8, Main.c(null,"&3Blue"));
                                    player.sendMessage(Main.c("HotPotato","You have joined &3Blue&r team."));
                                    PlayerNameManager.changeTeam(hotplayer, gameID);

                                    hp.setTeam(true);
                                    onTeam = true;
                                    PlayerNameManager.onGameJoin(hp, gameID);

                                    ScoreboardManager.changeLine(p, 8, Main.c(null,"&cRed"));
                                    break;
                                }

                                hp.setTeam(false);
                                onTeam = true;
                                PlayerNameManager.onGameJoin(hp, gameID);
                                ScoreboardManager.changeLine(p, 8, Main.c(null,"&cRed"));
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
            ScoreboardManager.changeLine(p, 15, Main.c(null,"&3» &b&lGame"));
            ScoreboardManager.changeLine(p, 14, Main.c(null,"&rHotPotato"));
            ScoreboardManager.changeLine(p, 13, Main.c(null," "));
            ScoreboardManager.changeLine(p, 12, Main.c(null,"&3» &b&lPlayers"));
            ScoreboardManager.changeLineGame(gameID,11, Main.c(null,"&r" + players.size() + "/" + max));
            ScoreboardManager.changeLine(p, 10, Main.c(null,"  "));
            ScoreboardManager.changeLine(p, 9, Main.c(null,"&3» &b&lTeam"));
            ScoreboardManager.changeLine(p, 7, Main.c(null,"  "));
            ScoreboardManager.changeLine(p, 6, Main.c(null,"&3» &b&lKit"));
            ScoreboardManager.changeLine(p, 5, Main.c(null,"&r" + kit));
            ScoreboardManager.changeLine(p, 4, Main.c(null,"  "));
            ScoreboardManager.changeLine(p, 3, Main.c(null,"&3» &b&lMap"));
            ScoreboardManager.changeLine(p, 2, Main.c(null,"&r" + map.getName()));
            p.sendMessage(Main.c("HotPotato","Loading Player Data..."));

            new BukkitRunnable(){
                @Override
                public void run() {
                    PlayerData pd = new PlayerData(p);
                    hp.setPlayerData(pd);
                    if (hp.isRed()) {
                        red.add(hp);
                    } else {
                        blue.add(hp);
                    }
                    p.sendMessage(Main.c("HotPotato","Your data has been loaded."));
                }
            }.runTaskAsynchronously(Main.getInstance());
        }


    }

    private void startGame() {
        state = INPROGRESS;
        List<List<Integer>> blueSpawns = map.getBlueSpawns();
        List<List<Integer>> redSpawns = map.getRedSpawns();

        //Teleporting Players
        int counter = 0;
        for (HotPotatoPlayer p : blue) {
            List<Integer> location = blueSpawns.get(counter);
            p.getPlayer().teleport(new Location(world, location.get(0),location.get(1),location.get(2),location.get(3),location.get(4)));
            p.getPlayer().closeInventory();
            counter++;
        }
        for (HotPotatoPlayer p : red) {
            List<Integer> location = redSpawns.get(counter);
            p.getPlayer().teleport(new Location(world, location.get(0),location.get(1),location.get(2),location.get(3),location.get(4)));
            p.getPlayer().closeInventory();
            counter++;
        }

        livesBlue = 3;
        livesRed = 3;

        newTnt();
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

    private void spawnTNT() {
        long delay = Main.chooseRan(100,800);
        int spawn = Main.chooseRan(0, map.getTntSpawns().size()-1);

        Location l = new Location(world, new Double(map.getTntSpawns().get(spawn).get(0)),new Double(map.getTntSpawns().get(spawn).get(1)),new Double(map.getTntSpawns().get(spawn).get(2)),0,0);
        FallingBlock e = world.spawnFallingBlock(l, Material.TNT, (byte) 0);

        timer = new BukkitRunnable() {
            @Override
            public void run() {
                Location location;
                if (inAir) {
                    fallingBlock.remove();
                    location = fallingBlock.getLocation();
                } else {
                    location = tntLocation;
                    location.getBlock().setType(Material.AIR);
                }

                location.getWorld().createExplosion(location, 3f, false);
                location.getWorld().playEffect(location, Effect.EXPLOSION_HUGE, 1);

                int y = location.getBlockY();
                while (y >= 0) {
                    Location blockLocation = new Location(world, location.getBlockX(), y, location.getBlockZ(), 0, 0);
                    Block block = blockLocation.getBlock();
                    if ((block.getState().getData().getItemTypeId() + "").split(":").length > 1) {
                        if ((block.getState().getData().getItemTypeId() + "").split(":")[1].equals("14")) {
                            //Red lost
                            livesRed--;
                            if (livesRed == 0) {
                                for (Player p : players) {
                                    p.sendMessage(Main.c("HotPotato","&3&lBlue Wins! &r&cRed &rhas lost all of their lives."));
                                }
                                endGame();
                                return;
                            }
                            for (Player p : players) {
                                p.sendMessage(Main.c("HotPotato","&cRed &rhas lost a life! They now have &a" + livesRed + " &rlives left!"));
                            }

                        } else if ((block.getState().getData().getItemTypeId() + "").split(":")[1].equals("11")) {
                            //Blue lost
                            livesBlue--;
                            if (livesBlue == 0) {
                                for (Player p : players) {
                                    p.sendMessage(Main.c("HotPotato","&c&lRed Wins! &r&3Blue &rhas lost all of their lives."));
                                }
                                endGame();
                                return;
                            }
                            for (Player p : players) {
                                p.sendMessage(Main.c("HotPotato","&3Blue &rhas lost a life! They now have &a" + livesBlue + " &rlives left!"));
                            }
                        }
                    }
                }


                newTnt();
            }
        }.runTaskLater(Main.getInstance(), delay);
    }

    private void newTnt() {
        timerTime = 10;

        timer = new BukkitRunnable() {
            @Override
            public void run() {
                timerTime--;
                for (Player p : players) {
                    p.setLevel(timerTime);
                }
                switch (timerTime) {
                    case 10:
                    case 5:
                    case 4:
                    case 3:
                    case 2:
                    case 1:
                        for (Player p : players) {
                            p.sendMessage(Main.c("HotPotato","The TNT will spawn in &a" + timerTime + " &rsecond" + (timerTime>1?"s":"") + "."));
                            p.playSound(p.getLocation(), Sound.NOTE_PLING,100,1);
                        }
                        break;
                    case 0:
                        timer.cancel();
                        spawnTNT();
                }



            }
        }.runTaskTimer(Main.getInstance(),0, 20);
    }

    public void endGame() {
        state = ENDING;
        for (Player p : players) {
            p.sendMessage(Main.c("Game Manager","This game has ended. You will be sent back to the lobby in 10 seconds."));
        }
        timer = new BukkitRunnable() {
            @Override
            public void run() {

                //Teleporting players
                for (Player p : players) {
                    p.teleport(CacheManager.getLobby());
                    p.sendMessage(Main.c("Game Manager","You have been sent back to the lobby."));
                }

                //Deleting world folder
                Bukkit.getServer().unloadWorld(world, false);
                File worldFolder = world.getWorldFolder();
                try  {
                    FileUtils.deleteDirectory(worldFolder);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                state = DEAD;
            }
        }.runTaskLater(Main.getInstance(), 200);

        PlayerInteractEvent.getHandlerList().unregister(this);
        EntityChangeBlockEvent.getHandlerList().unregister(this);
    }

    @EventHandler
    public void onEntityChange(EntityChangeBlockEvent e) {
        if (e.getEntity().getLocation().getWorld().getName().equals(world.getName())) {
            if (e.getEntity().getType() == EntityType.FALLING_BLOCK) {
                inAir = false;
                tntLocation = e.getBlock().getLocation();
            }
        }
    }

    @EventHandler
    public void onPlayerClick(PlayerInteractEvent e) {
        if (e.getClickedBlock().getLocation().getWorld().getName().equals(world.getName())) {
            if (e.getClickedBlock().getType() == Material.TNT) {
                e.getClickedBlock().setType(Material.AIR);
                FallingBlock e2 = (FallingBlock) e.getClickedBlock().getLocation().getWorld().spawnFallingBlock(e.getClickedBlock().getLocation(), Material.TNT, (byte) 0);
                e2.setVelocity(e.getPlayer().getLocation().getDirection().setY(0.5).normalize().multiply(1.3));
                fallingBlock = e2;
                inAir = true;
            }
        }
    }

    public void queueForTeam(HotPotatoPlayer p) {
        if (p.isRed()) {
            queueBlue.add(p.getPlayer());
            p.getPlayer().sendMessage(Main.c("HotPotato","You have queued for &3Blue &rteam."));
        } else {
            queueRed.add(p.getPlayer());
            p.getPlayer().sendMessage(Main.c("HotPotato","You have queued for &cRed &rteam."));
        }
    }

    public void addToTeam(HotPotatoPlayer p) {
        if (p.isRed()) {
            red.remove(p);
            blue.add(p);
            p.setTeam(false);
            p.getPlayer().sendMessage(Main.c("HotPotato","You have joined &3Blue&r team."));
            ScoreboardManager.changeLine(p.getPlayer(), 8, Main.c(null,"&3Blue"));
        } else {
            blue.remove(p);
            red.add(p);
            p.setTeam(true);
            p.getPlayer().sendMessage(Main.c("HotPotato","You have joined &cRed&r team."));
            ScoreboardManager.changeLine(p.getPlayer(), 8, Main.c(null,"&cRed"));
        }
    }


}
