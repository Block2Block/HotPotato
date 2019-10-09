package me.Block2Block.HotPotato.Listeners;

import me.Block2Block.HotPotato.Managers.PlayerNameManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        PlayerNameManager.onServerJoin(e.getPlayer());
    }

}
