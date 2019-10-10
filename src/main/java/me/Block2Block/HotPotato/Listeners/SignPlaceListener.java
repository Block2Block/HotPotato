package me.Block2Block.HotPotato.Listeners;

import me.Block2Block.HotPotato.Main;
import me.Block2Block.HotPotato.Managers.CacheManager;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class SignPlaceListener implements Listener {

    @EventHandler
    public void onSignPlace(SignChangeEvent e) {
        if (e.getLines()[0].equals("[HotPotato]")) {
            Sign sign = (Sign) e.getBlock().getState();
            switch (e.getLines()[1]) {
                case "queue":
                    sign.setLine(0, Main.c(null, "&2&l[HotPotato]"));
                    sign.setLine(1, Main.c(null, "Click to join"));
                    sign.setLine(2, Main.c(null, "the Queue!"));
                    sign.setLine(3, Main.c(null, "Players in queue: &a" + Main.getQueueManager().playersQueued()));

                    sign.update(true);

                    Location locationQueue = sign.getLocation();
                    CacheManager.addLocation(locationQueue, "queue");


                    break;
                case "stats":
                    sign.setLine(0, Main.c(null, "&2&l[HotPotato]"));
                    sign.setLine(1, Main.c(null, "Games Active: &a" + CacheManager.getGames().size()));
                    sign.setLine(2, Main.c(null, "Players: &a" + CacheManager.getPlayers().size()));
                    sign.setLine(3, Main.c(null, "Players in queue: &a" + Main.getQueueManager().playersQueued()));

                    sign.update(true);

                    Location locationStats = sign.getLocation();
                    CacheManager.addLocation(locationStats, "stats");

                    break;
            }
        }
    }

}
