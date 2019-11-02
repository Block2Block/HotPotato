package me.Block2Block.HotPotato.Commands;

import me.Block2Block.HotPotato.Entities.Game;
import me.Block2Block.HotPotato.Entities.GameState;
import me.Block2Block.HotPotato.Entities.HPMap;
import me.Block2Block.HotPotato.Listeners.EditModeListener;
import me.Block2Block.HotPotato.Main;
import me.Block2Block.HotPotato.Managers.CacheManager;
import me.Block2Block.HotPotato.Managers.PlayerNameManager;
import me.Block2Block.HotPotato.Managers.Utils.InventoryUtil;
import me.Block2Block.HotPotato.Managers.Utils.UUIDUtil;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.security.DomainCombiner;

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
                            PlayerNameManager.onGameLeave(p);
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
                                    if (Main.getQueueManager().getRecruiting() != CacheManager.getPlayers().get(p.getUniqueId()).getGameID()) {
                                        p.sendMessage(Main.c("HotPotato","The game you are in has already started."));
                                        return true;
                                    }
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
                                    if (Main.getQueueManager().getRecruiting() != CacheManager.getPlayers().get(p.getUniqueId()).getGameID()) {
                                        p.sendMessage(Main.c("HotPotato","The game you are in has already started."));
                                        return true;
                                    }
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
                                    if (Main.getQueueManager().getRecruiting() != id) {
                                        p.sendMessage(Main.c("HotPotato","That game has already started."));
                                        return true;
                                    }
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
                        break;
                    case "edit":
                        if (p.hasPermission("hotpotato.edit")) {
                            if (!CacheManager.isEditor(p)) {
                                if (CacheManager.getLobby() != null) {
                                    if (args.length == 2) {
                                        if (args[1].toLowerCase().equals("plugins")||args[1].toLowerCase().equals("logs")||args[1].toLowerCase().contains(".")) {
                                            p.sendMessage(Main.c("HotPotato","You cannot open the server folders as worlds or use a file as a world. Please try another name."));
                                            return true;
                                        }
                                        if (CacheManager.getEditMode().size() > 0) {
                                            p.sendMessage(Main.c("HotPotato","You cannot load another world into edit mode whilst there is still people in edit mode."));
                                            return true;
                                        }
                                        File file = new File(Main.getInstance().getServer().getWorldContainer().getAbsolutePath().substring(0, Main.getInstance().getServer().getWorldContainer().getAbsolutePath().length() - 1) + args[1]);
                                        if (file.exists()) {
                                            File worldFolder = new File(Main.getInstance().getServer().getWorldContainer().getAbsolutePath().substring(0, Main.getInstance().getServer().getWorldContainer().getAbsolutePath().length() - 1) + "HPEdit");
                                            try {
                                                if (worldFolder.exists()) {
                                                    FileUtils.deleteDirectory(worldFolder);
                                                }
                                                FileUtils.copyDirectory(file, worldFolder);
                                            } catch (Exception e) {
                                                p.sendMessage(Main.c("HotPotato","The server is unable to copy the directory of the specified world. Please try again."));
                                                return true;
                                            }

                                            World w = Bukkit.getServer().createWorld(new WorldCreator("HPEdit"));
                                            CacheManager.getEditMode().put(p, w);
                                            p.teleport(w.getSpawnLocation());
                                            p.sendMessage(Main.c("HotPotato","You have entered edit mode."));
                                        } else {
                                            p.sendMessage(Main.c("HotPotato","There is no world called " + args[1] + " in the servers directory."));
                                        }
                                    } else if (args.length == 1) {
                                        if (Bukkit.getWorld("HPEdit") == null) {
                                            File worldFolder = new File(Main.getInstance().getServer().getWorldContainer().getAbsolutePath().substring(0, Main.getInstance().getServer().getWorldContainer().getAbsolutePath().length() - 1) + "HPEdit");
                                            if (worldFolder.exists()) {
                                                p.sendMessage(Main.c("HotPotato","There is currently a map loaded into edit mode, this map will be loaded back into memory. If you wanted to start a new map, please either specify a map when loading into edit mode or use /hotpotato finish to mark the map as finished."));
                                            } else {
                                                p.sendMessage(Main.c("HotPotato","There is currently no map loaded into edit mode, creating a new world."));
                                            }

                                            World w = Bukkit.getServer().createWorld(new WorldCreator("HPEdit"));
                                            CacheManager.getEditMode().put(p, w);
                                            p.teleport(w.getSpawnLocation());
                                            p.sendMessage(Main.c("HotPotato","You have entered edit mode."));
                                        } else {
                                            World w = Bukkit.getWorld("HPEdit");
                                            CacheManager.getEditMode().put(p, w);
                                            p.teleport(w.getSpawnLocation());
                                            p.sendMessage(Main.c("HotPotato","You have entered edit mode."));
                                        }
                                    } else {
                                        p.sendMessage(Main.c("HotPotato","Invalid arguments. Correct arguments: &a/hotpotato edit [map]"));
                                    }
                                } else {
                                    p.sendMessage(Main.c("HotPotato","You must set your lobby point before you can set up a map."));
                                }
                            } else {
                                CacheManager.getEditMode().remove(p);
                                p.sendMessage(Main.c("HotPotato","You have left edit mode. Any setup you have done has been erased."));
                                p.teleport(CacheManager.getLobby());
                                if (CacheManager.isSetup(p)) {
                                    CacheManager.setSetupStage(-1);
                                    CacheManager.getData().clear();
                                    EditModeListener.onLeave();
                                }
                                if (CacheManager.getEditMode().size() == 0) {
                                    World w = Bukkit.getWorld("HPEdit");
                                    Bukkit.unloadWorld(w, true);
                                    p.sendMessage(Main.c("HotPotato","Because there is no-one else left in edit mode, the world was unloaded."));
                                }
                            }
                        } else {
                            p.sendMessage(Main.c("HotPotato","You do not have permission to perform this command."));
                        }
                        break;
                    case "setup":
                        if (p.hasPermission("hotpotato.edit")) {
                            if (CacheManager.isEditor(p)) {
                                p.sendMessage(Main.c("HotPotato","You are about to setup the map, at the end of which this world files will be put into a ZIP and the world folder will be deleted. Please type 'confirm' to confirm you wish to do this. You can cancel at any time by typing 'cancel'."));
                                CacheManager.setSetupStage(0);
                                CacheManager.enterSetup(p);
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
                        break;
                    case "finish":
                        if (p.hasPermission("hotpotato.edit")) {
                            if (CacheManager.isEditor(p)) {
                                p.sendMessage(Main.c("HotPotato","What you are about to do will delete the world from the server. Please type 'confirm' to confirm this is what you want to do. Type 'cancel' if you wish to cancel."));
                                CacheManager.setFinishPlayer(p);
                            } else {
                                p.sendMessage(Main.c("HotPotato","You must be in edit mode in order to mark a map as finished."));
                            }
                        } else {
                            p.sendMessage(Main.c("HotPotato","You do not have permission to perform this command."));
                        }
                        break;
                    case "addbalance":
                        if (p.hasPermission("hotpotato.edit")) {
                            if (args.length == 3) {
                                int amount;
                                try {
                                    amount = Integer.parseInt(args[2]);
                                } catch (NumberFormatException e) {
                                    p.sendMessage(Main.c("HotPotato","Invalid arguments. Correct syntax: &a/hotpotato addbalance <player> <amount>"));
                                    return true;
                                }

                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        String uuid = UUIDUtil.getUUID(args[1]).toString();
                                        if (uuid == null) {
                                            p.sendMessage(Main.c("HotPotato","There is no user by that name."));
                                            return;
                                        }
                                        if (!Main.getDbManager().addBalance(amount, uuid)) {
                                            p.sendMessage(Main.c("HotPotato","That player has not played a game yet. They must play a game in order to receive money."));
                                            return;
                                        }
                                        p.sendMessage(Main.c("HotPotato","You have added &a" + amount + " &rto &a" + args[1] + "'s &rbalance."));
                                    }
                                }.runTaskAsynchronously(Main.getInstance());
                            } else {
                                p.sendMessage(Main.c("HotPotato","Invalid arguments. Correct syntax: &a/hotpotato addbalance <player> <amount>"));
                            }
                        } else {
                            p.sendMessage(Main.c("HotPotato","You do not have permission to perform this command."));
                        }
                        break;
                    case "map":
                        if (p.hasPermission("hotpotato.edit")) {
                            if (args.length == 1) {
                                p.sendMessage(Main.c("HotPotato","Available sub-commands:\n" +
                                        "&a/hotpotato map list &r- Lists all current active maps.\n" +
                                        "&a/hotpotato map remove <id> &r- Remove a map from rotation."));
                            } else if (args.length == 2 || args.length == 3) {
                                switch (args[1].toLowerCase()) {
                                    case "list":
                                        String mapList = Main.c("HotPotato","Available maps:");
                                        for (HPMap map : CacheManager.getMaps()) {
                                            mapList += Main.c(null, "\nID: &a" + map.getId() + " &r- Name: &a" + map.getName() + "&r - Author: &a" + map.getAuthor());
                                        }
                                        p.sendMessage(mapList);
                                        break;
                                    case "remove":
                                        if (args.length == 3) {
                                            int id;
                                            try {
                                                id = Integer.parseInt(args[2]);
                                            } catch (NumberFormatException e) {
                                                p.sendMessage(Main.c("HotPotato","Invalid arguments. Correct arguments: &a/hotpotato map rmove <id>"));
                                                return true;
                                            }

                                            HPMap map = CacheManager.getMap(id);
                                            if (map == null) {
                                                p.sendMessage(Main.c("HotPotato","There is no map by that ID. If you need to see which maps you have active, please do &a/hotpotato map list&r."));
                                            }

                                            map.getZip().delete();
                                            CacheManager.getMaps().remove(map);
                                            Main.getDbManager().removeMap(map);
                                            p.sendMessage(Main.c("HotPotato","The map &a" + map.getName() + "&r was deleted."));
                                        } else {
                                            p.sendMessage(Main.c("HotPotato","Invalid arguments. Correct arguments: &a/hotpotato map rmove <id>"));
                                        }
                                        break;
                                    default:
                                        p.sendMessage(Main.c("HotPotato","Available sub-commands:\n" +
                                                "&a/hotpotato map list &r- Lists all current active maps.\n" +
                                                "&a/hotpotato map remove <id> &r- Remove a map from rotation."));
                                        break;
                                }
                            }
                        } else {
                            p.sendMessage(Main.c("HotPotato","You do not have permission to perform this command."));
                        }
                        break;
                    default:
                        p.sendMessage(Main.c("HotPotato", "Available commands: \n" +
                                "&a/hotpotato&r - HotPotato Sub-Command List\n" +
                                "&a/hotpotato kit&r - Set your kit\n" +
                                "&a/hotpotato team&r - Queue for a different team\n" +
                                "&a/hotpotato leave&r - Leave your current game\n" +
                                "&a/hotpotato queue&r - Queue for a new game" +
                                ((p.hasPermission("hotpotato.game")?"\n&a/hotpotato forcestart [time] [game id]&r - Force start a game\n" +
                                        "&a/hotpotato end [id]&r - Forces a game to end.":"")) + ((p.hasPermission("hotpotato.edit")?"\n&a/hotpotato edit [map]&r - Toggles edit mode\n" +
                                "&a/hotpotato setup&r - Set up a map while in edit mode.\n&a/hotpotato setlobby&r - Sets your current location as the Lobby.\n&a/hotpotato finish&r - Marks the map in edit mode finished and deletes the worlds.\n" +
                                "&a/hotpotato addbalance <player> <amount>\n" +
                                "&a/hotpotato map list &r- Lists all current active maps.\n" +
                                "&a/hotpotato map remove <id> &r- Remove a map from rotation.":""))));

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
                        "&a/hotpotato setup&r - Set up a map while in edit mode.\n&a/hotpotato setlobby&r - Sets your current location as the Lobby.\n&a/hotpotato finish&r - Marks the map in edit mode finished and deletes the worlds.\n" +
                        "&a/hotpotato addbalance <player> <amount>\n" +
                        "&a/hotpotato map list &r- Lists all current active maps.\n" +
                        "&a/hotpotato map remove <id> &r- Remove a map from rotation.":""))));
            }
        } else {
            sender.sendMessage("You cannot execute HotPotato commands from Console or a Command Block.");
        }

        return true;
    }

}
