package me.Block2Block.HotPotato.Entities;

import me.Block2Block.HotPotato.Kits.KitLoader;
import me.Block2Block.HotPotato.Main;
import me.Block2Block.HotPotato.Managers.CacheManager;
import me.Block2Block.HotPotato.Managers.PlayerNameManager;
import me.Block2Block.HotPotato.Managers.ScoreboardManager;
import me.Block2Block.HotPotato.Managers.TitleManager;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
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
    final private int MAX;
    private Player lastHitBlue;
    private Player lastHitRed;

    private int livesBlue;
    private int livesRed;
    private Location tntLocation;
    private FallingBlock fallingBlock;
    private boolean inAir;
    private Squid squid;

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

        MAX = map.getBlueSpawns().size() + map.getRedSpawns().size();

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
        List<Double> waitingLobby = map.getWaitingLobby();
        this.players.addAll(players);
        if ((this.players.size()/(double) MAX) >= (Main.getInstance().getConfig().getInt("Settings.Game.Percent-To-Shorten")/100d)) {
            Main.getQueueManager().noLongerRecruiting();
            startTimer(Main.getInstance().getConfig().getInt("Settings.Game.Shorten-Countdown-Time"));
        } else if ((this.players.size()/(double) MAX) >= (Main.getInstance().getConfig().getInt("Settings.Game.Percent-To-Start")/100d)) {
            if (timer == null) {
                startTimer(-1);
            }
        }
        for (Player p : players) {
            p.getPlayer().getInventory().clear();
            HotPotatoPlayer hp = new HotPotatoPlayer(p, this.gameID);
            CacheManager.addToCache(p.getUniqueId(), hp);

            p.teleport(new Location(world, waitingLobby.get(0), waitingLobby.get(1),waitingLobby.get(2),waitingLobby.get(3).floatValue(),waitingLobby.get(4).floatValue()), PlayerTeleportEvent.TeleportCause.PLUGIN);
            p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Game.Join.User-Message").replace("{id}","" + this.gameID)));
            for (Player p2 : this.players) {
                p2.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Game.Join.Game-Message").replace("{player}",p.getName())));
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
                                    player.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Game.Join.Join-Red")));
                                    PlayerNameManager.changeTeam(hotplayer, this.gameID);
                                    blue.remove(hotplayer);
                                    red.add(hotplayer);

                                    hp.setTeam(false);
                                    onTeam = true;

                                    hp.getPlayer().sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Game.Join.Join-Blue")));
                                    blue.add(hp);
                                    break;
                                }
                                hp.setTeam(true);
                                onTeam = true;

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
                                    Player player = queueBlue.remove(0);

                                    HotPotatoPlayer hotplayer = CacheManager.getPlayers().get(player.getUniqueId());
                                    hotplayer.setTeam(false);
                                    player.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Game.Join.Join-Blue")));
                                    PlayerNameManager.changeTeam(hotplayer, gameID);
                                    red.remove(hotplayer);
                                    blue.add(hotplayer);


                                    hp.setTeam(true);
                                    onTeam = true;
                                    red.add(hp);
                                    hp.getPlayer().sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Game.Join.Join-Red")));

                                    break;
                                }

                                blue.add(hp);
                                hp.setTeam(false);
                                onTeam = true;
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


            p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Game.Join.Loading-Data")));

            new BukkitRunnable(){
                @Override
                public void run() {
                    PlayerData pd = new PlayerData(p);
                    hp.setPlayerData(pd);
                    p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Game.Join.Data-Loaded")));
                }
            }.runTaskAsynchronously(Main.getInstance());
        }

        for (Player p : players) {
            PlayerNameManager.onGameJoin(CacheManager.getPlayers().get(p.getUniqueId()), this.gameID);
        }


        List<String> scoreboardLayout = Main.getInstance().getConfig().getStringList("Settings.Scoreboard.Before-Layout");
        for (Player p : this.players) {
            HotPotatoPlayer hp = CacheManager.getPlayers().get(p.getUniqueId());
            int counter = 15;
            for (String line : scoreboardLayout) {
                ScoreboardManager.changeLine(p, counter, line.replace("{team}",Main.getInstance().getConfig().getString("Messages.Scoreboard.Team." + ((hp.isRed())?"Red-Format":"Blue-Format"))).replace("{players}",players.size() + "").replace("{max}",MAX + "").replace("{map-name}",map.getName()).replace("{map-author}",map.getAuthor()).replace("{game-id}",this.gameID + "").replace("{kit}",hp.getKit().name()));
                counter--;
                if (counter == 0) {
                    break;
                }
            }
        }




    }

    private void startGame() {
        state = INPROGRESS;
        List<List<Double>> blueSpawns = map.getBlueSpawns();
        List<List<Double>> redSpawns = map.getRedSpawns();

        //Changing scoreboard.
        ScoreboardManager.resetBoardGame(this.gameID);

        List<String> scoreboardLayout = Main.getInstance().getConfig().getStringList("Settings.Scoreboard.Game-Layout");
        for (Player p : this.players) {
            HotPotatoPlayer hp = CacheManager.getPlayers().get(p.getUniqueId());
            int counter = 15;
            for (String line : scoreboardLayout) {
                ScoreboardManager.changeLine(p, counter, line.replace("{team}",Main.getInstance().getConfig().getString("Messages.Scoreboard.Team." + ((hp.isRed())?"Red-Format":"Blue-Format"))).replace("{players}",players.size() + "").replace("{max}",MAX + "").replace("{map-name}",map.getName()).replace("{map-author}",map.getAuthor()).replace("{game-id}",this.gameID + "").replace("{kit}",hp.getKit().name()).replace("{red-lives}",livesRed + "").replace("{blue-lives}",livesBlue + ""));
                counter--;
                if (counter == 0) {
                    break;
                }
            }
        }

        //Teleporting Players
        int counter = 0;
        for (HotPotatoPlayer p : blue) {
            List<Double> location = blueSpawns.get(counter);
            p.getPlayer().teleport(new Location(world, location.get(0),location.get(1),location.get(2),location.get(3).floatValue(),location.get(3).floatValue()));
            p.getPlayer().closeInventory();
            counter++;
        }
        for (HotPotatoPlayer p : red) {
            List<Double> location = redSpawns.get(counter);
            p.getPlayer().teleport(new Location(world, location.get(0),location.get(1),location.get(2),location.get(3).floatValue(),location.get(3).floatValue()));
            p.getPlayer().closeInventory();
            counter++;
        }

        livesBlue = 3;
        livesRed = 3;

        //Applying kit.
        for (Player p : players) {
            Kit kit = CacheManager.getPlayers().get(p.getUniqueId()).getKit();

            p.getInventory().clear();
            p.getEquipment().setArmorContents(new ItemStack[]{kit.boots(), kit.leggings(), kit.chestplate(), kit.helmet()});
            for (int i = 0; i<kit.hb().length; i++) {
                ItemStack s = kit.hb()[i];
                if (s != null) p.getInventory().setItem(i, s);
            }
            for (int i = 0; i<kit.i().length; i++) {
                ItemStack s = kit.i()[i];
                if (s != null) p.getInventory().setItem(i+9, s);
            }

            if (kit.name().equals("Leaper")) {
                p.setAllowFlight(true);
                p.setFlying(false);
            }

            //Map Info
            p.sendMessage(Main.c(false,Main.getInstance().getConfig().getString("Messages.Game.Game-Start")));

        }

        newTnt();

        Main.getQueueManager().noLongerRecruiting();


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
                                p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Game.Start-Timer.Title-Format").replace("{time}","" + timerTime).replace("{s}",(timerTime>1?"s":""))));
                                p.playSound(p.getLocation(), Sound.NOTE_PLING,100,1);
                                TitleManager.sendTitle(p, Main.c(false, Main.getInstance().getConfig().getString("Messages.Game.Start-Timer.Title-Format")).replace("{time}","" + timerTime),5,20,5, ChatColor.DARK_GREEN);
                            }
                            break;
                        case 0:
                            startGame();
                    }



                }
            }.runTaskTimer(Main.getInstance(),0, 20);
        } else {
            if (time < timerTime) {
                timerTime = time + 1;
            }
        }
    }

    public void playerLeave(HotPotatoPlayer p) {
        if (state==ENDING) {
            return;
        }
        if (state!=INPROGRESS) {
            players.remove(p.getPlayer());
            if (red.contains(p)) {
                red.remove(p);
            } else {
                blue.remove(p);
            }
            for (Player pl : players) {
                pl.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Game.Leave.Game-Message").replace("{player}",p.getName())));
            }
            p.getPlayer().sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Game.Leave.User-Message")));
            if (players.size() < 2) {
                timer.cancel();
                for (Player pl : players) {
                    pl.setLevel(0);
                }
            }
            p.getPlayer().getInventory().clear();
            p.getPlayer().teleport(CacheManager.getLobby());
            return;
        }
        Main.getDbManager().addLoss(p.getPlayer());
        p.getPlayer().getInventory().clear();
        players.remove(p.getPlayer());
        if (red.contains(p)) {
            red.remove(p);
        } else {
            blue.remove(p);
        }

        for (Player pl : players) {
            pl.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Game.Leave.Game-Message").replace("{player}",p.getName())));
        }

        p.getPlayer().teleport(CacheManager.getLobby());

        if (red.size() == 0) {
            for (Player p2 : players) {
                p2.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Game.Leave.Blue-Win")));
            }
            for (HotPotatoPlayer p2 : blue) {
                if (p2.getPlayer().equals(lastHitBlue)) {
                    Main.getDbManager().addWinningPunch(lastHitBlue);
                } else {
                    Main.getDbManager().addWin(p2.getPlayer());
                }
            }
            for (HotPotatoPlayer p2 : red) {
                Main.getDbManager().addLoss(p2.getPlayer());
            }
            endGame();
            return;
        } else if (blue.size() == 0) {
            for (Player p2 : players) {
                p2.sendMessage(Main.c(true ,Main.getInstance().getConfig().getString("Messages.Game.Leave.Red-Win")));
            }
            for (HotPotatoPlayer p2 : red) {
                if (p2.getPlayer().equals(lastHitRed)) {
                    Main.getDbManager().addWinningPunch(lastHitRed);
                } else {
                    Main.getDbManager().addWin(p2.getPlayer());
                }
            }
            for (HotPotatoPlayer p2 : blue) {
                Main.getDbManager().addLoss(p2.getPlayer());
            }
            endGame();
            return;
        }
    }

    private void spawnTNT() {
        long delay = Main.chooseRan(Main.getInstance().getConfig().getInt("Settings.Game.TNT-Explode-Min-Time"),Main.getInstance().getConfig().getInt("Settings.Game.TNT-Explode-Max-Time"));
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
                for (Player p : players) {
                    p.playSound(p.getLocation(), Sound.EXPLODE, 100, 1);
                }

                int y = location.getBlockY();
                while (y >= 0) {
                    Location blockLocation = new Location(world, location.getBlockX(), y, location.getBlockZ(), 0, 0);
                    Block block = blockLocation.getBlock();
                        if (block.getData() == (byte) 14) {
                            //Red lost
                            livesRed--;
                            List<String> scoreboardLayout = Main.getInstance().getConfig().getStringList("Settings.Scoreboard.Game-Layout");
                            for (Player p : players) {
                                HotPotatoPlayer hp = CacheManager.getPlayers().get(p.getUniqueId());
                                int counter = 15;
                                for (String line : scoreboardLayout) {
                                    ScoreboardManager.changeLine(p, counter, line.replace("{team}",Main.getInstance().getConfig().getString("Messages.Scoreboard.Team." + ((hp.isRed())?"Red-Format":"Blue-Format"))).replace("{players}",players.size() + "").replace("{max}",MAX + "").replace("{map-name}",map.getName()).replace("{map-author}",map.getAuthor()).replace("{game-id}",gameID + "").replace("{kit}",hp.getKit().name()).replace("{red-lives}",livesRed + "").replace("{blue-lives}",livesBlue + ""));
                                    counter--;
                                    if (counter == 0) {
                                        break;
                                    }
                                }
                            }
                            if (livesRed == 0) {
                                for (Player p : players) {
                                    p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Game.Blue-Won")));
                                    for (HotPotatoPlayer p2 : blue) {
                                        if (p2.getPlayer().equals(lastHitBlue)) {
                                            Main.getDbManager().addWinningPunch(lastHitBlue);
                                        } else {
                                            Main.getDbManager().addWin(p2.getPlayer());
                                        }
                                    }
                                    for (HotPotatoPlayer p2 : red) {
                                        Main.getDbManager().addLoss(p2.getPlayer());
                                    }
                                }
                                endGame();
                                return;
                            }
                            for (Player p : players) {
                                p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Game.Red-Lost-Life").replace("{red-lives}","" + livesRed)));
                            }
                            ScoreboardManager.changeLineGame(gameID, 11, livesRed + " ");
                            break;

                        } else if (block.getData() == (byte) 11 ||block.getData() == (byte) 3) {
                            //Blue los
                            livesBlue--;
                            List<String> scoreboardLayout = Main.getInstance().getConfig().getStringList("Settings.Scoreboard.Game-Layout");
                            for (Player p : players) {
                                HotPotatoPlayer hp = CacheManager.getPlayers().get(p.getUniqueId());
                                int counter = 15;
                                for (String line : scoreboardLayout) {
                                    ScoreboardManager.changeLine(p, counter, line.replace("{team}",Main.getInstance().getConfig().getString("Messages.Scoreboard.Team." + ((hp.isRed())?"Red-Format":"Blue-Format"))).replace("{players}",players.size() + "").replace("{max}",MAX + "").replace("{map-name}",map.getName()).replace("{map-author}",map.getAuthor()).replace("{game-id}",gameID + "").replace("{kit}",hp.getKit().name()).replace("{red-lives}",livesRed + "").replace("{blue-lives}",livesBlue + ""));
                                    counter--;
                                    if (counter == 0) {
                                        break;
                                    }
                                }
                            }
                            if (livesBlue == 0) {
                                for (Player p : players) {
                                    p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Game.Red-Won")));
                                }
                                for (HotPotatoPlayer p2 : red) {
                                    if (p2.getPlayer().equals(lastHitRed)) {
                                        Main.getDbManager().addWinningPunch(lastHitRed);
                                    } else {
                                        Main.getDbManager().addWin(p2.getPlayer());
                                    }
                                }
                                for (HotPotatoPlayer p2 : blue) {
                                    Main.getDbManager().addLoss(p2.getPlayer());
                                }
                                endGame();
                                return;
                            }

                            for (Player p : players) {
                                p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Game.Blue-Lost-Life").replace("{blue-lives}","" + livesBlue)));
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
        timerTime = Main.getInstance().getConfig().getInt("Settings.Game.TNT-Countdown-Time") + 1;

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
                            p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Game.TNT-Timer-Message").replace("{time}","" + timerTime).replace("{s}",(timerTime>1?"s":""))));
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
            p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Game.End-Game.Game-Has-Ended")));
            p.getPlayer().getInventory().clear();
        }

        if (timer != null) {
            timer.cancel();
        }
        if (tnttimer != null) {
            tnttimer.cancel();
        }
        if (tntDestroy != null) {
            tntDestroy.cancel();
        }

        timer = new BukkitRunnable() {
            @Override
            public void run() {

                //Teleporting players
                for (Player p : players) {
                    p.teleport(CacheManager.getLobby());
                    p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Game.End-Game.Sent-To-Lobby")));
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

                        int counter = 0;
                        for (String s : Main.getInstance().getConfig().getStringList("Settings.Signs.Stats-Format")) {
                            sign.setLine(counter, Main.c(false, s.replace("{games-active}",CacheManager.getGames().size() + "").replace("{players}",CacheManager.getPlayers().size() + "").replace("{queued}",Main.getQueueManager().playersQueued() + "")));
                            counter++;
                            if (counter == 4) {
                                break;
                            }
                        }

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
                if (squid != null) {
                    squid.remove();
                    squid = null;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
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
                if (CacheManager.getPlayers().get(e.getPlayer().getUniqueId()).isRed()) {
                    lastHitRed = e.getPlayer();
                } else {
                    lastHitBlue = e.getPlayer();
                }
                e.getClickedBlock().setType(Material.AIR);
                FallingBlock e2 = e.getClickedBlock().getLocation().getWorld().spawnFallingBlock(e.getClickedBlock().getLocation(), Material.TNT, (byte) 0);
                e2.setVelocity(e.getPlayer().getLocation().getDirection().setY(0.3).normalize().multiply(1.15));

                Squid squid = (Squid) e.getClickedBlock().getLocation().getWorld().spawnEntity(e.getClickedBlock().getLocation(), EntityType.SQUID);
                squid.setVelocity(e.getPlayer().getLocation().getDirection().setY(0.3).normalize().multiply(1.15));

                this.squid = squid;

                e2.setPassenger(squid);

                LivingEntity l = (LivingEntity) squid;
                l.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1000000, 2, true, false), true);

                fallingBlock = e2;
                inAir = true;
            }
        }
    }

    @EventHandler
    public void onClickMidairLeft(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getEntity() instanceof Squid) {
            if (e.getEntity().getWorld().getName().equals(fallingBlock.getWorld().getName())) {
                if (CacheManager.getPlayers().get(e.getDamager().getUniqueId()).isRed()) {
                    lastHitRed = (Player) e.getDamager();
                } else {
                    lastHitBlue = (Player) e.getDamager();
                }
                if (e.isCancelled()) {
                    return;
                }
                fallingBlock.setVelocity(e.getDamager().getLocation().getDirection().setY(0.3).normalize().multiply(1.15));
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClickMidairRight(PlayerInteractEntityEvent e) {
        if (e.getRightClicked() instanceof FallingBlock) {
            if (e.getRightClicked().getWorld().getName().equals(fallingBlock.getWorld().getName())) {
                if (CacheManager.getPlayers().get(e.getPlayer().getUniqueId()).isRed()) {
                    lastHitRed = e.getPlayer();
                } else {
                    lastHitBlue = e.getPlayer();
                }
                fallingBlock.setVelocity(e.getPlayer().getLocation().getDirection().setY(0.3).normalize().multiply(1.15));
            }
        }
    }

    public void queueForTeam(HotPotatoPlayer p) {
        if (p.isRed()) {
            if (queueRed.size() > 0) {
                red.remove(p);
                blue.add(p);
                p.setTeam(false);
                p.getPlayer().sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Game.Join.Join-Blue")));

                HotPotatoPlayer p2 = CacheManager.getPlayers().get(queueBlue.get(0).getUniqueId());
                blue.remove(p2);
                red.add(p2);
                p2.setTeam(true);
                p2.getPlayer().sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Game.Join.Join-Red")));
                queueRed.remove(0);
                return;
            }
            queueBlue.add(p.getPlayer());
            p.getPlayer().sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Game.Queue-Blue")));
        } else {
            if (queueBlue.size() > 0) {
                blue.remove(p);
                red.add(p);
                p.setTeam(true);
                p.getPlayer().sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Game.Join.Join-Red")));

                HotPotatoPlayer p2 = CacheManager.getPlayers().get(queueBlue.get(0).getUniqueId());
                red.remove(p2);
                blue.add(p2);
                p2.setTeam(false);
                p2.getPlayer().sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Game.Join.Join-Blue")));
                queueBlue.remove(0);
                return;
            }
            queueRed.add(p.getPlayer());
            p.getPlayer().sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Game.Queue-Red")));
        }
    }

    public void addToTeam(HotPotatoPlayer p) {
        if (p.isRed()) {
            red.remove(p);
            blue.add(p);
            p.setTeam(false);
            p.getPlayer().sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Game.Join.Join-Blue")));
        } else {
            blue.remove(p);
            red.add(p);
            p.setTeam(true);
            p.getPlayer().sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Game.Join.Join-Red")));
        }
    }

    public FallingBlock getFallingBlock() {
        return fallingBlock;
    }

    public Location getTntLocation() {
        return tntLocation;
    }

    public void setSquid(Squid s) {
        squid = s;
        inAir = true;
    }

    public void setFallingBlock(FallingBlock fb) {
        fallingBlock = fb;
    }

    public void setLastHit(Player p) {
        if (CacheManager.getPlayers().get(p.getUniqueId()).isRed()) {
            lastHitRed = p;
        } else {
            lastHitBlue = p;
        }
    }

    public HPMap getMap() {
        return map;
    }

    public int getMAX() {return MAX;}

    public int getLivesBlue() {
        return livesBlue;
    }

    public int getLivesRed() {
        return livesRed;
    }
}
