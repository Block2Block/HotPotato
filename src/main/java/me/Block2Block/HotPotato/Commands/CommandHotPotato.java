package me.Block2Block.HotPotato.Commands;

import me.Block2Block.HotPotato.Main;
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

            } else {
                p.sendMessage(Main.c("HotPotato", "Available commands: \n" +
                        "&a/hotpotato&r - HotPotato Sub-Command List\n" +
                        "&a/hotpotato kit&r - Set your kit\n" +
                        "&a/hotpotato team&r - Queue for a different team\n" +
                        "&a/hotpotato leave&r - Leave your current game\n" +
                        "&a/hotpotato queue&r - Queue for a new game" +
                        ((p.hasPermission("hotpotato.game")?"\n&a/hotpotato forcestart&r - Force start a game\n" +
                                "&ahotpotato end&r - Forces a game to end.":""))));
            }
        } else {
            sender.sendMessage("You cannot execute HotPotato commands from Console or a Command Block.");
        }

        return true;
    }

}
