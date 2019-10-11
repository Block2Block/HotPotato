package me.Block2Block.HotPotato.Listeners;

import me.Block2Block.HotPotato.Managers.CacheManager;
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
                if (p.getFoodLevel() < 20) {
                    p.setFoodLevel(25);
                }
            }
        }
    }

}
