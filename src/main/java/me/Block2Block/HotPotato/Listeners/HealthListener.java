package me.Block2Block.HotPotato.Listeners;

import me.Block2Block.HotPotato.Managers.CacheManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class HealthListener implements Listener {

    @EventHandler
    public void onHealthChange(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if (CacheManager.getPlayers().containsKey(p.getUniqueId())) {
                if (e.getCause() != EntityDamageEvent.DamageCause.CUSTOM) {
                    e.setCancelled(true);
                }
            }
        }
    }

}
