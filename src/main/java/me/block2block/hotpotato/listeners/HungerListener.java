package me.block2block.hotpotato.listeners;

import me.block2block.hotpotato.managers.CacheManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class HungerListener implements Listener {

    @EventHandler
    public void onHungerChange(FoodLevelChangeEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if (CacheManager.getPlayers().containsKey(p.getUniqueId())) {
                e.setFoodLevel(30);
            }
        }
    }

}
