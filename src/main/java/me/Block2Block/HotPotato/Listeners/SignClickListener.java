package me.Block2Block.HotPotato.Listeners;

import me.Block2Block.HotPotato.Main;
import me.Block2Block.HotPotato.Managers.CacheManager;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class SignClickListener implements Listener {

    @EventHandler
    public void onSignClick(PlayerInteractEvent e) {
        if (e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (e.getClickedBlock().getType() == Material.WALL_SIGN ||e.getClickedBlock().getType() == Material.SIGN_POST){
                Sign sign = (Sign) e.getClickedBlock().getState();
                if (CacheManager.getSigns().containsKey(sign.getLocation())) {
                    String type = CacheManager.getSigns().get(sign.getLocation());
                    switch (type) {
                        case "queue":
                            if (Main.getQueueManager().addToQueue(e.getPlayer())) {
                                sign.setLine(3, Main.c(null, "Players in queue: &a0"));
                            } else {
                                sign.setLine(3, Main.c(null, "Player in queue: &a1"));
                            }
                            sign.update(true);
                        case "stats":
                            return;
                    }
                }
            } else{
                return;
            }
        } else {
            return;
        }
    }

}
