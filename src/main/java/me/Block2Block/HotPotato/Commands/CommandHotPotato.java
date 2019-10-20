package me.Block2Block.HotPotato.Commands;

import me.Block2Block.HotPotato.Entities.Game;
import me.Block2Block.HotPotato.Entities.GameState;
import me.Block2Block.HotPotato.Main;
import me.Block2Block.HotPotato.Managers.CacheManager;
import me.Block2Block.HotPotato.Managers.Utils.InventoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

public class CommandHotPotato implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length > 0) {
                switch (args[0].toLowerCase()) {
                    case "kit":
                        if (CacheManager.getPlayers().containsKey(p.getUniqueId())) {
                            if (CacheManager.getGames().get(CacheManager.getPlayers().get(p.getUniqueId()).getGameID()).getState() == GameState.WAITING||CacheManager.getGames().get(CacheManager.getPlayers().get(p.getUniqueId()).getGameID()).getState() == GameState.STARTING) {
                                p.openInventory(InventoryUtil.kitSelection(p));
                            } else {
                                p.sendMessage(Main.c("HotPotato","You can only set your kit before the game starts."));
                            }
                        } else {
                            p.sendMessage(Main.c("HotPotato","You must be in a game to execute this command."));
                        }
                        break;
                    case "team":
                        if (CacheManager.getPlayers().containsKey(p.getUniqueId())) {
                            if (CacheManager.getGames().get(CacheManager.getPlayers().get(p.getUniqueId()).getGameID()).getState() == GameState.WAITING||CacheManager.getGames().get(CacheManager.getPlayers().get(p.getUniqueId()).getGameID()).getState() == GameState.STARTING) {
                                p.openInventory(InventoryUtil.teamSelection(p));
                            } else {
                                p.sendMessage(Main.c("HotPotato","You can only queue for a team before the game starts."));
                            }
                        } else {
                            p.sendMessage(Main.c("HotPotato","You must be in a game to execute this command."));
                        }
                        break;
                    case "leave":
                        if (CacheManager.getPlayers().containsKey(p.getUniqueId())) {
                            CacheManager.getGames().get(CacheManager.getPlayers().get(p.getUniqueId()).getGameID()).playerLeave(CacheManager.getPlayers().get(p.getUniqueId()));
                            p.teleport(CacheManager.getLobby());
                            CacheManager.getPlayers().remove(p.getUniqueId());

                        } else {
                            p.sendMessage(Main.c("HotPotato","You must be in a game to execute this command."));
                        }
                        break;
                    case "queue":
                        if (CacheManager.getPlayers().containsKey(p.getUniqueId())) {
                            p.sendMessage(Main.c("HotPotato","Please leave your current game in order to use this command."));
                        } else {
                            Main.getQueueManager().addToQueue(p);
                        }
                        break;
                    case "forcestart":
                        if (p.hasPermission("hotpotato.game")) {
                            if (args.length == 1) {
                                if (CacheManager.getPlayers().containsKey(p.getUniqueId())) {
                                    CacheManager.getGames().get(CacheManager.getPlayers().get(p.getUniqueId()).getGameID()).startTimer(-1);
                                    p.sendMessage(Main.c("HotPotato", "You have force started your game."));
                                } else {
                                    p.sendMessage(Main.c("HotPotato","You must specify a Time and ID when not in a game."));
                                    return true;
                                }
                            } else if (args.length == 2) {
                                int time = 0;
                                try {
                                    time = Integer.parseInt(args[1]);
                                } catch (NumberFormatException e) {
                                    p.sendMessage(Main.c("HotPotato","Invalid arguments. Correct arguments: &a/hotpotato forcestart [time] [game id]"));
                                    return true;
                                }

                                if (CacheManager.getPlayers().containsKey(p.getUniqueId())) {
                                    CacheManager.getGames().get(CacheManager.getPlayers().get(p.getUniqueId()).getGameID()).startTimer(time);
                                    p.sendMessage(Main.c("HotPotato", "You have force started your game with &a" + time + "s &rcountdown."));
                                } else {
                                    p.sendMessage(Main.c("HotPotato","You must specify an ID when not in a game."));
                                }

                            } else if (args.length == 3) {
                                int time = 0;
                                int id = 0;
                                try {
                                    time = Integer.parseInt(args[1]);
                                    id = Integer.parseInt(args[2]);
                                } catch (NumberFormatException e) {
                                    p.sendMessage(Main.c("HotPotato","Invalid arguments. Correct arguments: &a/hotpotato forcestart [time] [game id]"));
                                    return true;
                                }
                                if (CacheManager.getGames().containsKey(id)) {
                                    CacheManager.getGames().get(id).startTimer(time);
                                    p.sendMessage(Main.c("HotPotato", "You have force started game &a" + id + " &7with &a" + time + "s &rcountdown."));
                                } else {
                                    p.sendMessage(Main.c("HotPotato","A game does not exist with that ID."));
                                }
                            } else {
                                p.sendMessage(Main.c("HotPotato","Invalid arguments. Correct arguments: &a/hotpotato forcestart [time] [game id]"));
                                return true;
                            }
                        } else {
                            p.sendMessage(Main.c("HotPotato","You do not have permission to perform this command."));
                        }
                        break;
                    case "end":
                        if (p.hasPermission("hotpotato.game")) {
                            if (args.length == 1) {
                                if (CacheManager.getPlayers().containsKey(p.getUniqueId())) {
                                    Game game = CacheManager.getGames().get(CacheManager.getPlayers().get(p.getUniqueId()).getGameID());
                                    for (Player player : game.getPlayers()) {
                                        player.sendMessage(Main.c("HotPotato","Your game has been forced to end by an administrator."));
                                    }
                                    game.endGame();
                                    p.sendMessage(Main.c("HotPotato","You have ended your game."));
                                } else {
                                    p.sendMessage(Main.c("HotPotato","You must specify a game ID when not in a game."));
                                }
                            } else if (args.length == 2) {
                                int id = 0;
                                try {
                                    id = Integer.parseInt(args[1]);
                                } catch (NumberFormatException e) {
                                    p.sendMessage(Main.c("HotPotato","Invalid arguments. Correct arguments: &a/hotpotato end [game id]"));
                                    return true;
                                }

                                if (CacheManager.getGames().containsKey(id)) {
                                    Game game = CacheManager.getGames().get(id);
                                    game.endGame();
                                    for (Player player : game.getPlayers()) {
                                        p.sendMessage(Main.c("HotPotato","Your game has been forced to end by an administrator."));
                                    }
                                    p.sendMessage(Main.c("HotPotato","You have ended the game &a" + id + "&r."));
                                } else {
                                    p.sendMessage(Main.c("HotPotato","A game does not exist with that ID."));
                                }
                            } else {
                                p.sendMessage(Main.c("HotPotato","Invalid arguments. Correct arguments: &a/hotpotato end [game id]"));
                            }
                        } else {
                            p.sendMessage(Main.c("HotPotato","You do not have permission to perform this command."));
                        }
                    case "edit":
                        if (p.hasPermission("hotpotato.edit")) {
                            if (!CacheManager.isEditor(p)) {
                                if (CacheManager.getLobby() != null) {
                                    if (args.length == 2) {

                                    } else if (args.length == 1) {
                                        if (Bukkit.getWorld("HPEdit") == null) {
                                            File worldFolder = new File(Main.getInstance().getServer().getWorldContainer().getAbsolutePath().substring(0, Main.getInstance().getServer().getWorldContainer().getAbsolutePath().length() - 1) + "HPEdit");
                                            if (worldFolder.exists()) {
                                                p.sendMessage(Main.c("HotPotato","There is currently a map loaded into edit mode, this map will be loaded back into memory. If you wanted to start a new map, please either specify a map when loading into edit mode or use /hotpotato finish to mark the map as finished."));
                                            } else {
                                                p.sendMessage(Main.c("HotPotato","There is currently no map loaded into edit mode, creating a new world."));
                                            }

                                            World w = Bukkit.getServer().createWorld(new WorldCreator("HPEdit"));
                                            p.teleport(w.getSpawnLocation());
                                            p.sendMessage(Main.c("HotPotato","You have entered edit mode."));
                                        } else {
                                            World w = Bukkit.getWorld("HPEdit");
                                            p.teleport(w.getSpawnLocation());
                                            p.sendMessage(Main.c("HotPotato","You have entered edit mode."));
                                            CacheManager.getEditMode().put(p, w);
                                        }
                                    } else {
                                        p.sendMessage(Main.c("HotPotato","Invalid arguments. Correct arguments: &a/hotpotato edit [map]"));
                                    }
                                } else {
                                    p.sendMessage(Main.c("HotPotato","You must set your lobby point before you can set up a map."));
                                }
                            } else {
                                World w = CacheManager.getEditMode().get(p);
                                for (Player pl : w.getPlayers()) {
                                    pl.teleport(CacheManager.getLobby());
                                }
                                Bukkit.getServer().unloadWorld(w, true);
                                CacheManager.getEditMode().remove(p);
                                p.sendMessage(Main.c("HotPotato","You have left edit mode. Any setup you have done to the world has not been saved. If you load another map into edit mode, any terrain modifications to the current world will be erased."));
                            }
                        } else {
                            p.sendMessage(Main.c("HotPotato","You do not have permission to perform this command."));
                        }
                        break;
                    case "setup":
                        if (p.hasPermission("hotpotato.edit")) {
                            if (CacheManager.isEditor(p)) {
                                p.sendMessage(Main.c("HotPotato","You are about to setup the map, at the end of which this world files will be put into a ZIP and the world folder will be deleted. Please type 'confirm' to confirm you wish to do this. You can cancel at any time by typing 'cancel'."));
                            } else {
                                p.sendMessage(Main.c("HotPotato","You must be in edit mode in order to setup a map."));
                            }
                        } else {
                            p.sendMessage(Main.c("HotPotato","You do not have permission to perform this command."));
                        }
                        break;
                    case "setlobby":
                        if (p.hasPermission("hotpotato.edit")) {
                            if (p.getLocation().getWorld().getName().matches("HP[0-9]+")||p.getLocation().getWorld().getName().equals("HPEdit")) {
                                p.sendMessage(Main.c("HotPotato","You cannot set your lobby to a location in a HotPotato world. If you believe this to be a mistake, please rename your world."));
                            } else {
                                CacheManager.setLobby(p.getLocation());
                                Location l = p.getLocation().clone();
                                new BukkitRunnable()  {
                                    @Override
                                    public void run() {
                                        Main.getDbManager().setLobby(l);
                                    }
                                }.runTaskAsynchronously(Main.getInstance());
                                p.sendMessage(Main.c("HotPotato","You have set your current location as the Lobby."));
                            }
                        } else {
                            p.sendMessage(Main.c("HotPotato","You do not have permission to perform this command."));
                        }

                    case "finish":
                        if (p.hasPermission("hotpotato.edit")) {
                            if (CacheManager.isEditor(p)) {

                            } else {
                                p.sendMessage(Main.c("HotPotato","You must be in edit mode in order to mark a map as finished."));
                            }
                        } else {
                            p.sendMessage(Main.c("HotPotato","You do not have permission to perform this command."));
                        }
                        break;
                }
            } else {
                p.sendMessage(Main.c("HotPotato", "Available commands: \n" +
                        "&a/hotpotato&r - HotPotato Sub-Command List\n" +
                        "&a/hotpotato kit&r - Set your kit\n" +
                        "&a/hotpotato team&r - Queue for a different team\n" +
                        "&a/hotpotato leave&r - Leave your current game\n" +
                        "&a/hotpotato queue&r - Queue for a new game" +
                        ((p.hasPermission("hotpotato.game")?"\n&a/hotpotato forcestart [time] [game id]&r - Force start a game\n" +
                                "&a/hotpotato end [id]&r - Forces a game to end.":"")) + ((p.hasPermission("hotpotato.edit")?"\n&a/hotpotato edit [map]&r - Toggles edit mode\n" +
                        "&a/hotpotato setup&r - Set up a map while in edit mode.\n&a/hotpotato setlobby&r - Sets your current location as the Lobby.\n&a/hotpotato finish&r - Marks the map in edit mode finished.":""))));
            }
        } else {
            sender.sendMessage("You cannot execute HotPotato commands from Console or a Command Block.");
        }

        return true;
    }

}
