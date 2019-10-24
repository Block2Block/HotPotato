package me.Block2Block.HotPotato.Listeners;

import me.Block2Block.HotPotato.Managers.CacheManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class LeaveListener implements Listener {

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        CacheManager.playerLeave(e.getPlayer());
        CacheManager.getEditMode().remove(e.getPlayer());
        if (CacheManager.isSetup(e.getPlayer())) {
            CacheManager.exitSetup();
            CacheManager.getData().clear();
            EditModeListener.onLeave();
            CacheManager.setSetupStage(0);
        }
    }

}
