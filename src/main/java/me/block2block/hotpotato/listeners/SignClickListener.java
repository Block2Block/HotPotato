package me.block2block.hotpotato.listeners;

import me.block2block.hotpotato.Main;
import me.block2block.hotpotato.managers.CacheManager;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

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
                            if (e.getPlayer().getGameMode() == GameMode.CREATIVE && e.getAction() == Action.LEFT_CLICK_BLOCK) {
                                return;
                            }

                            Main.getQueueManager().addToQueue(e.getPlayer());

                            for (Location loc : CacheManager.getSigns().keySet()) {
                                if (CacheManager.getSigns().get(loc).equals("queue")) {
                                    Sign sign2 = (Sign) loc.getBlock().getState();

                                    int counter = 0;
                                    for (String s : Main.getInstance().getConfig().getStringList("Settings.Signs.Queue-Format")) {
                                        sign2.setLine(counter, Main.c(false, s.replace("{games-active}",CacheManager.getGames().size() + "").replace("{players}",CacheManager.getPlayers().size() + "").replace("{queued}",Main.getQueueManager().playersQueued() + "")));
                                        counter++;
                                        if (counter == 4) {
                                            break;
                                        }
                                    }

                                    sign2.update(true);
                                } else if (CacheManager.getSigns().get(loc).equals("stats")) {
                                    Sign sign2 = (Sign) loc.getBlock().getState();

                                    int counter = 0;
                                    for (String s : Main.getInstance().getConfig().getStringList("Settings.Signs.Stats-Format")) {
                                        sign2.setLine(counter, Main.c(false, s.replace("{games-active}",CacheManager.getGames().size() + "").replace("{players}",CacheManager.getPlayers().size() + "").replace("{queued}",Main.getQueueManager().playersQueued() + "")));
                                        counter++;
                                        if (counter == 4) {
                                            break;
                                        }
                                    }

                                    sign2.update(true);
                                }
                            }
                            break;
                        case "stats":
                            if (e.getPlayer().getGameMode() == GameMode.CREATIVE && e.getAction() == Action.LEFT_CLICK_BLOCK) {
                                return;
                            }

                            e.getPlayer().sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Signs.Stats.Loading-Stats")));

                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    List<Integer> stats = Main.getDbManager().getStats(e.getPlayer());
                                    e.getPlayer().sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Signs.Stats.Stats").replace("{balance}",stats.get(0) + "").replace("{games-played}","" + stats.get(2)).replace("{wins}","" + stats.get(1)).replace("{winning-punch}","" + stats.get(3)).replace("{player-name}",e.getPlayer().getName())));
                                }
                            }.runTaskAsynchronously(Main.getInstance());
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
