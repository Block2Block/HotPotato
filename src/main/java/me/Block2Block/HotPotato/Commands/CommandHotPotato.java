package me.Block2Block.HotPotato.Commands;

import me.Block2Block.HotPotato.Entities.Game;
import me.Block2Block.HotPotato.Entities.GameState;
import me.Block2Block.HotPotato.Main;
import me.Block2Block.HotPotato.Managers.CacheManager;
import me.Block2Block.HotPotato.Managers.Utils.InventoryUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
                                    game.endGame();
                                    for (Player player : game.getPlayers()) {
                                        p.sendMessage(Main.c("HotPotato","Your game has been forced to end by an administrator."));
                                    }
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

                                Game game = CacheManager.getGames().get(id);
                                game.endGame();
                                for (Player player : game.getPlayers()) {
                                    p.sendMessage(Main.c("HotPotato","Your game has been forced to end by an administrator."));
                                }
                                p.sendMessage(Main.c("HotPotato","You have ended the game &a" + id + "&r."));
                            } else {
                                p.sendMessage(Main.c("HotPotato","Invalid arguments. Correct arguments: &a/hotpotato end [game id]"));
                            }
                        } else {
                            p.sendMessage(Main.c("HotPotato","You do not have permission to perform this command."));
                        }
                }
            } else {
                p.sendMessage(Main.c("HotPotato", "Available commands: \n" +
                        "&a/hotpotato&r - HotPotato Sub-Command List\n" +
                        "&a/hotpotato kit&r - Set your kit\n" +
                        "&a/hotpotato team&r - Queue for a different team\n" +
                        "&a/hotpotato leave&r - Leave your current game\n" +
                        "&a/hotpotato queue&r - Queue for a new game" +
                        ((p.hasPermission("hotpotato.game")?"\n&a/hotpotato forcestart [time] [game id]&r - Force start a game\n" +
                                "&ahotpotato end [id]&r - Forces a game to end.":""))));
            }
        } else {
            sender.sendMessage("You cannot execute HotPotato commands from Console or a Command Block.");
        }

        return true;
    }

}
