package me.Block2Block.HotPotato.Commands;

import me.Block2Block.HotPotato.Main;
import me.Block2Block.HotPotato.Managers.CacheManager;
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

                        } else {
                            p.sendMessage(Main.c("HubParkour","You must be in a game to execute this command."));
                        }
                        break;
                    case "team":
                        if (CacheManager.getPlayers().containsKey(p.getUniqueId())) {

                        } else {
                            p.sendMessage(Main.c("HubParkour","You must be in a game to execute this command."));
                        }
                        break;
                    case "leave":
                        if (CacheManager.getPlayers().containsKey(p.getUniqueId())) {

                        } else {
                            p.sendMessage(Main.c("HubParkour","You must be in a game to execute this command."));
                        }
                        break;
                    case "queue":
                        if (CacheManager.getPlayers().containsKey(p.getUniqueId())) {
                            p.sendMessage(Main.c("HubParkour","Please leave your current game in order to use this command."));
                        } else {
                            Main.getQueueManager().addToQueue(p);
                        }
                        break;
                    case "forcestart":
                        if (p.hasPermission("hotpotato.game")) {
                            if (args.length == 2) {
                                if (CacheManager.getPlayers().containsKey(p.getUniqueId())) {
                                    CacheManager.getGames().get(CacheManager.getPlayers().get(p.getUniqueId()).getGameID()).startTimer(-1);
                                    p.sendMessage(Main.c("HotPotato", "You have force started your game."));
                                } else {
                                    p.sendMessage(Main.c("HotPotato","You must specify an Time and ID when not in a game."));
                                    return true;
                                }
                            } else if (args.length == 3) {
                                int time = 0;
                                try {
                                    time = Integer.parseInt(args[2]);
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

                            } else if (args.length == 4) {
                                int time = 0;
                                int id = 0;
                                try {
                                    time = Integer.parseInt(args[2]);
                                    id = Integer.parseInt(args[3]);
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
                            if (args.length == 2) {

                            } else if (args.length == 3) {

                            } else {

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
