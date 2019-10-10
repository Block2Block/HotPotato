package me.Block2Block.HotPotato.Listeners;

import me.Block2Block.HotPotato.Main;
import me.Block2Block.HotPotato.Managers.CacheManager;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBreak(BlockBreakEvent e) {
        if (e.getBlock().getType() == Material.SIGN_POST||e.getBlock().getType() == Material.WALL_SIGN) {
            if (CacheManager.getPlayers().containsKey(e.getPlayer().getUniqueId())) {
                e.setCancelled(true);
            }
            String type = CacheManager.isSign(e.getBlock().getLocation());
            switch (type) {
                case "queue":
                case "stats":
                    if (e.getPlayer().hasPermission("hotpotato.admin")) {
                        e.getPlayer().sendMessage(Main.c("HotPotato","You are not permitted to break HotPotato signs."));
                        e.setCancelled(true);
                        return;
                    }
                    Main.getDbManager().removeSign(e.getBlock().getLocation(),type);
                    CacheManager.removeSign(e.getBlock().getLocation());
                    e.getPlayer().sendMessage(Main.c("HubParkour","The " + type + " sign has been deleted."));
                    break;
                default:
                    return;
            }
        }
    }

}
