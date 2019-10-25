package me.Block2Block.HotPotato.Listeners;

import me.Block2Block.HotPotato.Main;
import me.Block2Block.HotPotato.Managers.CacheManager;
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
                e.getPlayer().sendMessage(Main.c("HotPotato","You have left edit mode."));
                if (CacheManager.getEditMode().size() == 0) {
                    Bukkit.getServer().unloadWorld("HPEdit",true);
                }
            }
        } else {
            if (e.getTo().getWorld().getName().equals("HPEdit")) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(Main.c("HotPotato","You must be in edit mode in order to teleport to players also in edit mode."));
            }
        }
    }

}
