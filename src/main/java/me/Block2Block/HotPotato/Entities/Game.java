package me.Block2Block.HotPotato.Entities;

import me.Block2Block.HotPotato.Kits.KitLoader;
import me.Block2Block.HotPotato.Main;
import me.Block2Block.HotPotato.Managers.CacheManager;
import me.Block2Block.HotPotato.Managers.PlayerNameManager;
import me.Block2Block.HotPotato.Managers.ScoreboardManager;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
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
    private List<Player> players;
    private int timerTime;
    private BukkitTask timer;
    private BukkitTask tnttimer;
    private BukkitTask tntDestroy;
    private HPMap map;
    private List<HotPotatoPlayer> blue;
    private List<HotPotatoPlayer> red;
    private int max;

    private int livesBlue;
    private int livesRed;
    private Location tntLocation;
    private FallingBlock fallingBlock;
    private boolean inAir;
    private ArmorStand armorStand;

    private List<Player> queueRed;
    private List<Player> queueBlue;


    public Game(int gameID, HPMap map) {
        state = GameState.WAITING;
        this.gameID = gameID;

        players = new ArrayList<>();
        blue = new ArrayList<>();
        red = new ArrayList<>();
        queueRed = new ArrayList<>();
        queueBlue = new ArrayList<>();

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
            if (timer == null) {
                startTimer(-1);
            }
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
                                    PlayerNameManager.changeTeam(hotplayer, this.gameID);
                                    blue.remove(hotplayer);
                                    red.add(hotplayer);

                                    hp.setTeam(false);
                                    onTeam = true;

                                    ScoreboardManager.changeLine(p, 8, Main.c(null,"&3Blue"));
                                    blue.add(hp);
                                    break;
                                }
                                hp.setTeam(true);
                                onTeam = true;

                                ScoreboardManager.changeLine(p, 8, Main.c(null,"&cRed"));
                                red.add(hp);

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
                                    red.remove(hotplayer);
                                    blue.add(hotplayer);


                                    hp.setTeam(true);
                                    onTeam = true;
                                    red.add(hp);

                                    ScoreboardManager.changeLine(p, 8, Main.c(null,"&cRed"));
                                    break;
                                }

                                blue.add(hp);
                                hp.setTeam(false);
                                onTeam = true;
                                ScoreboardManager.changeLine(p, 8, Main.c(null,"&3Blue"));
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
            ScoreboardManager.changeLine(p, 10, Main.c(null,"  "));
            ScoreboardManager.changeLine(p, 9, Main.c(null,"&3» &b&lTeam"));
            ScoreboardManager.changeLine(p, 7, Main.c(null,"   "));
            ScoreboardManager.changeLine(p, 6, Main.c(null,"&3» &b&lKit"));
            ScoreboardManager.changeLine(p, 5, Main.c(null,"&r" + kit));
            ScoreboardManager.changeLine(p, 4, Main.c(null,"    "));
            ScoreboardManager.changeLine(p, 3, Main.c(null,"&3» &b&lMap"));
            ScoreboardManager.changeLine(p, 2, Main.c(null,"&r" + map.getName()));
            p.sendMessage(Main.c("HotPotato","Loading Player Data..."));

            new BukkitRunnable(){
                @Override
                public void run() {
                    PlayerData pd = new PlayerData(p);
                    hp.setPlayerData(pd);
                    p.sendMessage(Main.c("HotPotato","Your data has been loaded."));
                }
            }.runTaskAsynchronously(Main.getInstance());
        }

        ScoreboardManager.changeLineGame(gameID,11, Main.c(null,"&r" + this.players.size() + "/" + max));
        for (Player p : players) {
            PlayerNameManager.onGameJoin(CacheManager.getPlayers().get(p.getUniqueId()), this.gameID);
        }




    }

    private void startGame() {
        state = INPROGRESS;
        List<List<Integer>> blueSpawns = map.getBlueSpawns();
        List<List<Integer>> redSpawns = map.getRedSpawns();

        //Changing scoreboard.
        ScoreboardManager.changeLineGame(gameID, 12, Main.c(null, "&3» &c&lRed Lives"));
        ScoreboardManager.changeLineGame(gameID, 11, "3 ");

        ScoreboardManager.changeLineGame(gameID, 9, Main.c(null, "&3» &3&lBlue Lives"));
        ScoreboardManager.changeLineGame(gameID, 8, "3");

        //Teleporting Players
        int counter = 0;
        for (HotPotatoPlayer p : blue) {
            List<Integer> location = blueSpawns.get(counter);
            p.getPlayer().teleport(new Location(world, location.get(0),location.get(1),location.get(2),0,0));
            p.getPlayer().closeInventory();
            counter++;
        }
        for (HotPotatoPlayer p : red) {
            List<Integer> location = redSpawns.get(counter);
            p.getPlayer().teleport(new Location(world, location.get(0),location.get(1),location.get(2),0,0));
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

        if (red.size() == 0) {
            for (Player p2 : players) {
                p2.sendMessage(Main.c("HotPotato","&3&lBlue Wins! &rAll of &cRed &rleft."));
            }
            endGame();
            return;
        } else if (blue.size() == 0) {
            for (Player p2 : players) {
                p2.sendMessage(Main.c("HotPotato","&c&lRed Wins! &rAll of &3Blue &rleft."));
            }
            endGame();
            return;
        }
    }

    private void spawnTNT() {
        long delay = Main.chooseRan(100,800);
        int spawn = Main.chooseRan(0, map.getTntSpawns().size()-1);

        Location l = new Location(world, new Double(map.getTntSpawns().get(spawn).get(0)),new Double(map.getTntSpawns().get(spawn).get(1)),new Double(map.getTntSpawns().get(spawn).get(2)),0,0);
        FallingBlock e = world.spawnFallingBlock(l, Material.TNT, (byte) 0);

        tntDestroy = new BukkitRunnable() {
            @Override
            public void run() {
                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }
                if (tnttimer != null) {
                    tnttimer.cancel();
                    tnttimer = null;
                }
                Location location;
                if (inAir) {
                    fallingBlock.remove();
                    location = fallingBlock.getLocation();
                } else {
                    location = tntLocation;
                    location.getBlock().setType(Material.AIR);
                }

                location.getWorld().playEffect(location, Effect.EXPLOSION_HUGE, 1);

                int y = location.getBlockY();
                while (y >= 0) {
                    Location blockLocation = new Location(world, location.getBlockX(), y, location.getBlockZ(), 0, 0);
                    Block block = blockLocation.getBlock();
                        if (block.getData() == (byte) 14) {
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
                            ScoreboardManager.changeLineGame(gameID, 11, livesRed + " ");
                            break;

                        } else if (block.getData() == (byte) 11) {
                            //Blue lost
                            livesBlue--;
                            if (livesBlue == 0) {
                                for (Player p : players) {
                                    p.sendMessage(Main.c("HotPotato","&c&lRed Wins! &r&3Blue &rhas lost all of their lives."));
                                }
                                endGame();
                                return;
                            }

                            ScoreboardManager.changeLineGame(gameID, 8, livesBlue + "");

                            for (Player p : players) {
                                p.sendMessage(Main.c("HotPotato","&3Blue &rhas lost a life! They now have &a" + livesBlue + " &rlives left!"));
                            }
                            break;
                        }
                    y--;
                }

                if (state != ENDING) {
                    newTnt();
                }
            }
        }.runTaskLater(Main.getInstance(), delay);
    }

    private void newTnt() {
        timerTime = 11;

        tnttimer = new BukkitRunnable() {
            @Override
            public void run() {
                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }
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

                //Clearing scoreboard
                for (int i = 1;i < 16;i++) {
                    ScoreboardManager.resetLineGame(gameID,i);
                }

                //Deleting world folder
                Bukkit.getServer().unloadWorld(world, false);
                File worldFolder = world.getWorldFolder();
                try  {
                    FileUtils.deleteDirectory(worldFolder);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                PlayerNameManager.onGameEnd(players);
                CacheManager.onGameEnd(players, gameID);

                players = new ArrayList<>();
                blue = new ArrayList<>();
                red = new ArrayList<>();

                for (Location loc : CacheManager.getSigns().keySet()) {
                    if (CacheManager.getSigns().get(loc).equals("stats")) {
                        Sign sign = (Sign) loc.getBlock().getState();
                        sign.setLine(1, Main.c(null, "Games Active: &a" + CacheManager.getGames().size()));
                        sign.setLine(2, Main.c(null, "Players: &a" + CacheManager.getPlayers().size()));
                        sign.setLine(3, Main.c(null, "Players Queued: &a" + Main.getQueueManager().playersQueued()));

                        sign.update(true);
                    }
                }



                state = DEAD;
            }
        }.runTaskLater(Main.getInstance(), 200);

        PlayerInteractEvent.getHandlerList().unregister(this);
        EntityChangeBlockEvent.getHandlerList().unregister(this);
        EntityDamageByEntityEvent.getHandlerList().unregister(this);
        PlayerInteractEntityEvent.getHandlerList().unregister(this);
    }

    @EventHandler
    public void onEntityChange(EntityChangeBlockEvent e) {
        if (e.getEntity().getLocation().getWorld().getName().equals(world.getName())) {
            if (e.getEntity().getType() == EntityType.FALLING_BLOCK) {
                inAir = false;
                tntLocation = e.getBlock().getLocation();
                armorStand.remove();
                armorStand = null;
            }
        }
    }

    @EventHandler
    public void onPlayerClick(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) {
            return;
        }
        if (e.getAction() != Action.LEFT_CLICK_BLOCK && e.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (e.getClickedBlock().getLocation().getWorld().getName().equals(world.getName())) {
            if (tntLocation == null && fallingBlock == null) {
                return;
            } else if (tntLocation == null) {
                if (!e.getClickedBlock().getLocation().getBlock().equals(fallingBlock.getLocation().getBlock())) {
                    return;
                }
            } else {
                if (!e.getClickedBlock().getLocation().getBlock().equals(tntLocation.getBlock())) {
                    return;
                }
            }
            if (e.getClickedBlock().getType() == Material.TNT) {
                e.getClickedBlock().setType(Material.AIR);
                FallingBlock e2 = e.getClickedBlock().getLocation().getWorld().spawnFallingBlock(e.getClickedBlock().getLocation(), Material.TNT, (byte) 0);
                e2.setVelocity(e.getPlayer().getLocation().getDirection().setY(0.3).normalize().multiply(1.15));
                ArmorStand armorStand = (ArmorStand) e.getClickedBlock().getLocation().getWorld().spawnEntity(e.getClickedBlock().getLocation(), EntityType.ARMOR_STAND);
                armorStand.setVisible(false);
                e2.setPassenger(armorStand);
                fallingBlock = e2;
                inAir = true;
                this.armorStand = armorStand;
            }
        }
    }

    @EventHandler
    public void onClickMidairLeft(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getEntity() instanceof ArmorStand) {
            if (e.getEntity().getWorld().getName().equals(fallingBlock.getWorld().getName())) {
                fallingBlock.setVelocity(e.getDamager().getLocation().getDirection().setY(0.3).normalize().multiply(1.15));
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onClickMidairRight(PlayerInteractEntityEvent e) {
        if (e.getRightClicked() instanceof FallingBlock) {
            if (e.getRightClicked().getWorld().getName().equals(fallingBlock.getWorld().getName())) {
                fallingBlock.setVelocity(e.getPlayer().getLocation().getDirection().setY(0.3).normalize().multiply(1.15));
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
