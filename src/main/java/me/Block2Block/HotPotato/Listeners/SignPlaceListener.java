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
            e.setCancelled(true);
            Sign sign = (Sign) e.getBlock().getState();
            switch (e.getLines()[1]) {
                case "queue":
                    int counter = 0;
                    for (String s : Main.getInstance().getConfig().getStringList("Settings.Signs.Queue-Format")) {
                        sign.setLine(counter, Main.c(false, s.replace("{games-active}",CacheManager.getGames().size() + "").replace("{players}",CacheManager.getPlayers().size() + "").replace("{queued}",Main.getQueueManager().playersQueued() + "")));
                        counter++;
                        if (counter == 4) {
                            break;
                        }
                    }

                    sign.update(true);

                    Location locationQueue = sign.getLocation();
                    CacheManager.addLocation(locationQueue, "queue");
                    Main.getDbManager().addSign(locationQueue, "queue");


                    break;
                case "stats":
                    int counter2 = 0;
                    for (String s : Main.getInstance().getConfig().getStringList("Settings.Signs.Stats-Format")) {
                        sign.setLine(counter2, Main.c(false, s.replace("{games-active}",CacheManager.getGames().size() + "").replace("{players}",CacheManager.getPlayers().size() + "").replace("{queued}",Main.getQueueManager().playersQueued() + "")));
                        counter2++;
                        if (counter2 == 4) {
                            break;
                        }
                    }

                    sign.update(true);

                    Location locationStats = sign.getLocation();
                    CacheManager.addLocation(locationStats, "stats");
                    Main.getDbManager().addSign(locationStats, "stats");

                    break;
            }
        }
    }

}
