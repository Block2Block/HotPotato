package me.block2block.hotpotato.listeners;

import me.block2block.hotpotato.managers.PlayerNameManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        PlayerNameManager.onServerJoin(e.getPlayer());

    }

}
