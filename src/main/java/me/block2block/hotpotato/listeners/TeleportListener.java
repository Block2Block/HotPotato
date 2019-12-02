package me.block2block.hotpotato.listeners;

import me.block2block.hotpotato.Main;
import me.block2block.hotpotato.managers.CacheManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class TeleportListener implements Listener {

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        if (CacheManager.getEditMode().containsKey(e.getPlayer())) {
            if (!e.getTo().getWorld().getName().equals("HPEdit")) {
                CacheManager.getEditMode().remove(e.getPlayer());
                e.getPlayer().sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Edit.Left-Edit-Mode")));
                if (CacheManager.getEditMode().size() == 0) {
                    Bukkit.getServer().unloadWorld("HPEdit",true);
                }
            }
        } else {
            if (e.getTo().getWorld().getName().equals("HPEdit")) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Edit.Teleport-Not-In-Edit-Mode")));
            }
        }
    }

}
