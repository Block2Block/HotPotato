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
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.UUID;

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
                                p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Kit.Only-Execute-Before-Game")));
                            }
                        } else {
                            p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Must-Be-In-Game")));
                        }
                        break;
                    case "team":
                        if (CacheManager.getPlayers().containsKey(p.getUniqueId())) {
                            if (CacheManager.getGames().get(CacheManager.getPlayers().get(p.getUniqueId()).getGameID()).getState() == GameState.WAITING||CacheManager.getGames().get(CacheManager.getPlayers().get(p.getUniqueId()).getGameID()).getState() == GameState.STARTING) {
                                p.openInventory(InventoryUtil.teamSelection(p));
                            } else {
                                p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Team.Only-Execute-Before-Game")));
                            }
                        } else {
                            p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Must-Be-In-Game")));
                        }
                        break;
                    case "leave":
                        if (CacheManager.getPlayers().containsKey(p.getUniqueId())) {
                            CacheManager.getGames().get(CacheManager.getPlayers().get(p.getUniqueId()).getGameID()).playerLeave(CacheManager.getPlayers().get(p.getUniqueId()));
                            p.teleport(CacheManager.getLobby());
                            PlayerNameManager.onGameLeave(p);
                            CacheManager.getPlayers().remove(p.getUniqueId());

                        } else {
                            p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Must-Be-In-Game")));
                        }
                        break;
                    case "queue":
                        if (CacheManager.getPlayers().containsKey(p.getUniqueId())) {
                            p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Must-Not-Be-In-Game")));
                        } else {
                            Main.getQueueManager().addToQueue(p);
                        }
                        break;
                    case "forcestart":
                        if (p.hasPermission("hotpotato.game")) {
                            if (args.length == 1) {
                                if (CacheManager.getPlayers().containsKey(p.getUniqueId())) {
                                    if (Main.getQueueManager().getRecruiting() != CacheManager.getPlayers().get(p.getUniqueId()).getGameID()) {
                                        p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Force-Start.Current-Game-Already-Started")));
                                        return true;
                                    }
                                    CacheManager.getGames().get(CacheManager.getPlayers().get(p.getUniqueId()).getGameID()).startTimer(-1);
                                    p.sendMessage(Main.c(true, Main.getInstance().getConfig().getString("Messages.Commands.Force-Start.You-Forcestarted")));
                                } else {
                                    p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Force-Start.Must-Specify-Time-ID")));
                                    return true;
                                }
                            } else if (args.length == 2) {
                                int time = 0;
                                try {
                                    time = Integer.parseInt(args[1]);
                                } catch (NumberFormatException e) {
                                    p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Force-Start.Invalid-Arguments")));
                                    return true;
                                }

                                if (CacheManager.getPlayers().containsKey(p.getUniqueId())) {
                                    if (Main.getQueueManager().getRecruiting() != CacheManager.getPlayers().get(p.getUniqueId()).getGameID()) {
                                        p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Force-Start.Current-Game-Already-Started")));
                                        return true;
                                    }
                                    CacheManager.getGames().get(CacheManager.getPlayers().get(p.getUniqueId()).getGameID()).startTimer(time);
                                    p.sendMessage(Main.c(true, Main.getInstance().getConfig().getString("Messages.Commands.Force-Start.You-Forcestarted-Time").replace("{time}",time + "")));
                                } else {
                                    p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Force-Start.Must-Specify-Time-ID")));
                                }

                            } else if (args.length == 3) {
                                int time = 0;
                                int id = 0;
                                try {
                                    time = Integer.parseInt(args[1]);
                                    id = Integer.parseInt(args[2]);
                                } catch (NumberFormatException e) {
                                    p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Force-Start.Invalid-Arguments")));
                                    return true;
                                }
                                if (CacheManager.getGames().containsKey(id)) {
                                    if (Main.getQueueManager().getRecruiting() != id) {
                                        p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Force-Start.Other-Game-Already-Started")));
                                        return true;
                                    }
                                    CacheManager.getGames().get(id).startTimer(time);
                                    p.sendMessage(Main.c(true, Main.getInstance().getConfig().getString("Messages.Commands.Force-Start.You-Forcestarted-Other-Game").replace("{time}",time + "").replace("{id}",id + "")));
                                } else {
                                    p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Force-Start.No-Game-With-ID")));
                                }
                            } else {
                                p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Force-Start.Invalid-Arguments")));
                                return true;
                            }
                        } else {
                            p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.No-Permission")));
                        }
                        break;
                    case "end":
                        if (p.hasPermission("hotpotato.game")) {
                            if (args.length == 1) {
                                if (CacheManager.getPlayers().containsKey(p.getUniqueId())) {
                                    Game game = CacheManager.getGames().get(CacheManager.getPlayers().get(p.getUniqueId()).getGameID());
                                    for (Player player : game.getPlayers()) {
                                        player.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.End.Your-Game-Ended")));
                                    }
                                    game.endGame();
                                    p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.End.You-Ended")));
                                } else {
                                    p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.End.Must-Specify-ID")));
                                }
                            } else if (args.length == 2) {
                                int id = 0;
                                try {
                                    id = Integer.parseInt(args[1]);
                                } catch (NumberFormatException e) {
                                    p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.End.Invalid-Arguments")));
                                    return true;
                                }

                                if (CacheManager.getGames().containsKey(id)) {
                                    Game game = CacheManager.getGames().get(id);
                                    game.endGame();
                                    for (Player player : game.getPlayers()) {
                                        p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.End.Your-Game-Ended")));
                                    }
                                    p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.End.You-Ended-ID").replace("{id}",id + "")));
                                } else {
                                    p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.End.No-Game-With-ID")));
                                }
                            } else {
                                p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.End.Invalid-Arguments")));
                            }
                        } else {
                            p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.No-Permission")));
                        }
                        break;
                    case "edit":
                        if (p.hasPermission("hotpotato.edit")) {
                            if (!CacheManager.isEditor(p)) {
                                if (CacheManager.getLobby() != null) {
                                    if (args.length == 2) {
                                        if (args[1].toLowerCase().equals("plugins")||args[1].toLowerCase().equals("logs")||args[1].toLowerCase().contains(".")) {
                                            p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Edit.Cannot-Open-Server-Files")));
                                            return true;
                                        }
                                        if (CacheManager.getEditMode().size() > 0) {
                                            p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Edit.People-In-Edit-Mode")));
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
                                                p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Edit.World-Copy-Fail")));
                                                return true;
                                            }

                                            World w = Bukkit.getServer().createWorld(new WorldCreator("HPEdit"));
                                            CacheManager.getEditMode().put(p, w);
                                            p.teleport(w.getSpawnLocation());
                                            p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Edit.Entered-Edit-Mode")));
                                        } else {
                                            p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Edit.No-World").replace("{world}",args[1])));
                                        }
                                    } else if (args.length == 1) {
                                        if (Bukkit.getWorld("HPEdit") == null) {
                                            File worldFolder = new File(Main.getInstance().getServer().getWorldContainer().getAbsolutePath().substring(0, Main.getInstance().getServer().getWorldContainer().getAbsolutePath().length() - 1) + "HPEdit");
                                            if (worldFolder.exists()) {
                                                p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Edit.Map-Loaded")));
                                            } else {
                                                p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Edit.Map-Not-Loaded")));
                                            }

                                            World w = Bukkit.getServer().createWorld(new WorldCreator("HPEdit"));
                                            CacheManager.getEditMode().put(p, w);
                                            p.teleport(w.getSpawnLocation());
                                            p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Edit.Entered-Edit-Mode")));
                                        } else {
                                            World w = Bukkit.getWorld("HPEdit");
                                            CacheManager.getEditMode().put(p, w);
                                            p.teleport(w.getSpawnLocation());
                                            p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Edit.Entered-Edit-Mode")));
                                        }
                                    } else {
                                        p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Edit.Invalid-Arguments")));
                                    }
                                } else {
                                    p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Edit.Must-Set-Lobby")));
                                }
                            } else {
                                CacheManager.getEditMode().remove(p);
                                p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Edit.Left-Edit-Mode")));
                                p.teleport(CacheManager.getLobby());
                                if (CacheManager.isSetup(p)) {
                                    CacheManager.setSetupStage(-1);
                                    CacheManager.getData().clear();
                                    EditModeListener.onLeave();
                                }
                                if (CacheManager.getEditMode().size() == 0) {
                                    World w = Bukkit.getWorld("HPEdit");
                                    Bukkit.unloadWorld(w, true);
                                    p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Edit.Unloaded-World")));
                                }
                            }
                        } else {
                            p.sendMessage(Main.c(true, Main.getInstance().getConfig().getString("Messages.Commands.No-Permission")));
                        }
                        break;
                    case "setup":
                        if (p.hasPermission("hotpotato.edit")) {
                            if (CacheManager.isEditor(p)) {
                                if (CacheManager.getSetupStage() != -1) {
                                    p.sendMessage(Main.c(true, Main.getInstance().getConfig().getString("Messages.Commands.Setup.Already-In-Setup")));
                                    return true;
                                }
                                p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Setup.Setup-Warning")));
                                CacheManager.setSetupStage(0);
                                CacheManager.enterSetup(p);
                            } else {
                                p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Setup.Must-Be-In-Edit-Mode")));
                            }
                        } else {
                            p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.No-Permission")));
                        }
                        break;
                    case "setlobby":
                        if (p.hasPermission("hotpotato.edit")) {
                            if (p.getLocation().getWorld().getName().matches("HP[0-9]+")||p.getLocation().getWorld().getName().equals("HPEdit")) {
                                p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Set-Lobby.Cannot-Set-In-HotPotato-World")));
                            } else {
                                CacheManager.setLobby(p.getLocation());
                                Location l = p.getLocation().clone();
                                new BukkitRunnable()  {
                                    @Override
                                    public void run() {
                                        Main.getDbManager().setLobby(l);
                                    }
                                }.runTaskAsynchronously(Main.getInstance());
                                p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Set-Lobby.Set-Lobby")));
                            }
                        } else {
                            p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.No-Permission")));
                        }
                        break;
                    case "finish":
                        if (p.hasPermission("hotpotato.edit")) {
                            if (CacheManager.isEditor(p)) {
                                p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Finish.Finish-Warning")));
                                CacheManager.setFinishPlayer(p);
                            } else {
                                p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Finish.Must-Be-In-Edit-Mode")));
                            }
                        } else {
                            p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.No-Permission")));
                        }
                        break;
                    case "addbalance":
                        if (p.hasPermission("hotpotato.edit")) {
                            if (args.length == 3) {
                                int amount;
                                try {
                                    amount = Integer.parseInt(args[2]);
                                } catch (NumberFormatException e) {
                                    p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Add-Balance.Invalid-Arguments")));
                                    return true;
                                }

                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        UUID playerUuid = UUIDUtil.getUUID(args[1]);
                                        if (playerUuid == null) {
                                            p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Add-Balance.No-User")));
                                            return;
                                        }
                                        String uuid = playerUuid.toString();
                                        if (uuid == null) {
                                            p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Add-Balance.No-User")));
                                            return;
                                        }
                                        if (!Main.getDbManager().addBalance(amount, uuid)) {
                                            p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Add-Balance.Not-Played-Game")));
                                            return;
                                        }
                                        p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Add-Balance.Success").replace("{amount}",amount + "").replace("{player}",args[1])));
                                    }
                                }.runTaskAsynchronously(Main.getInstance());
                            } else {
                                p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Add-Balance.Invalid-Arguments")));
                            }
                        } else {
                            p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.No-Permission")));
                        }
                        break;
                    case "map":
                        if (p.hasPermission("hotpotato.edit")) {
                            if (args.length == 1) {
                                p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Map.Sub-command-List")));
                            } else if (args.length == 2 || args.length == 3) {
                                switch (args[1].toLowerCase()) {
                                    case "list":
                                        String mapList = Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Map.List.Available-Maps"));
                                        for (HPMap map : CacheManager.getMaps()) {
                                            mapList += Main.c(false, Main.getInstance().getConfig().getString("Messages.Commands.Map.List.Map-List-Format").replace("{id}",map.getId() + "").replace("{name}",map.getName()).replace("{author}",map.getAuthor()));
                                        }
                                        p.sendMessage(mapList);
                                        break;
                                    case "remove":
                                        if (args.length == 3) {
                                            int id;
                                            try {
                                                id = Integer.parseInt(args[2]);
                                            } catch (NumberFormatException e) {
                                                p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Map.Remove.Invalid-Arguments")));
                                                return true;
                                            }

                                            HPMap map = CacheManager.getMap(id);
                                            if (map == null) {
                                                p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Map.Remove.No-Map")));
                                            }

                                            map.getZip().delete();
                                            CacheManager.getMaps().remove(map);
                                            Main.getDbManager().removeMap(map);
                                            p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Map.Remove.Success").replace("{name}",map.getName())));
                                        } else {
                                            p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Map.Remove.Invalid-Arguments")));
                                        }
                                        break;
                                    default:
                                        p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Map.Sub-command-List")));
                                        break;
                                }
                            }
                        } else {
                            p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.No-Permission")));
                        }
                        break;
                    default:
                        p.sendMessage(Main.c(true, Main.getInstance().getConfig().getString("Messages.Commands.Default-Command-List") +
                                ((p.hasPermission("hotpotato.game")?Main.getInstance().getConfig().getString("Messages.Commands.Game-Command-List") + "":"")) + ((p.hasPermission("hotpotato.edit")?Main.getInstance().getConfig().get("Messages.Commands.Edit-Command-List") + "":""))));

                }
            } else {
                p.sendMessage(Main.c(true, Main.getInstance().getConfig().getString("Messages.Commands.Default-Command-List") +
                        ((p.hasPermission("hotpotato.game")?Main.getInstance().getConfig().getString("Messages.Commands.Game-Command-List") + "":"")) + ((p.hasPermission("hotpotato.edit")?Main.getInstance().getConfig().get("Messages.Commands.Edit-Command-List") + "":""))));
            }
        } else {
            sender.sendMessage("You cannot execute HotPotato commands from Console or a Command Block.");
        }

        return true;
    }

}
