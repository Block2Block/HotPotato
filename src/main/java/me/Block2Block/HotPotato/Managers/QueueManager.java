package me.Block2Block.HotPotato.Managers;

import me.Block2Block.HotPotato.Entities.Game;
import me.Block2Block.HotPotato.Entities.Queue;
import me.Block2Block.HotPotato.Main;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class QueueManager {

    private Queue queue;
    private int counter;
    private int recruiting;


    public QueueManager() {
        queue = new Queue();
        counter = 1;
        recruiting = -1;
    }

    public boolean addToQueue(Player p) {
        if (queue.contains(p)) {
            queue.removeFromQueue(p);
            p.sendMessage(Main.c("HotPotato","You have left the game queue."));

            for (Location loc : CacheManager.getSigns().keySet()) {
                if (CacheManager.getSigns().get(loc).equals("queue")) {
                    Sign sign = (Sign) loc.getBlock().getState();
                    sign.setLine(3, Main.c(null, "Players Queued: &a" + Main.getQueueManager().playersQueued()));
                    sign.update(true);
                } else if (CacheManager.getSigns().get(loc).equals("stats")) {
                    Sign sign = (Sign) loc.getBlock().getState();
                    sign.setLine(1, Main.c(null, "Games Active: &a" + CacheManager.getGames().size()));
                    sign.setLine(2, Main.c(null, "Players: &a" + CacheManager.getPlayers().size()));
                    sign.setLine(3, Main.c(null, "Players Queued: &a" + Main.getQueueManager().playersQueued()));

                    sign.update(true);
                }
            }

            return false;
        }
        int count = queue.enQueue(p);
        if (count == 2) {
            if (CacheManager.getMaps().size() < 1) {
                for (Player player : queue.deQueue()) {
                    p.sendMessage(Main.c("Queue Manager","There are currently no maps set up. You have been removed from the queue."));
                }
                queue.clear();
                return false;
            }
            int map = Main.chooseRan(0,CacheManager.getMaps().size() - 1);
            Game newGame = new Game(counter, CacheManager.getMaps().get(map));
            CacheManager.newGame(newGame, counter);
            newGame.join(queue.deQueue());
            queue.clear();
            recruiting = counter;
            counter++;

            for (Location loc : CacheManager.getSigns().keySet()) {
                if (CacheManager.getSigns().get(loc).equals("queue")) {
                    Sign sign = (Sign) loc.getBlock().getState();
                    sign.setLine(3, Main.c(null, "Players Queued: &a" + Main.getQueueManager().playersQueued()));
                    sign.update(true);
                } else if (CacheManager.getSigns().get(loc).equals("stats")) {
                    Sign sign = (Sign) loc.getBlock().getState();
                    sign.setLine(1, Main.c(null, "Games Active: &a" + CacheManager.getGames().size()));
                    sign.setLine(2, Main.c(null, "Players: &a" + CacheManager.getPlayers().size()));
                    sign.setLine(3, Main.c(null, "Players Queued: &a" + Main.getQueueManager().playersQueued()));

                    sign.update(true);
                }
            }

            return true;
        } else {
            if (recruiting != -1) {
                CacheManager.getGames().get(recruiting).join(queue.deQueue());
            } else {
                p.sendMessage(Main.c("Queue Manager", "You have been added to the game queue."));
            }

            for (Location loc : CacheManager.getSigns().keySet()) {
                if (CacheManager.getSigns().get(loc).equals("queue")) {
                    Sign sign = (Sign) loc.getBlock().getState();
                    sign.setLine(3, Main.c(null, "Players Queued: &a" + Main.getQueueManager().playersQueued()));
                    sign.update(true);
                } else if (CacheManager.getSigns().get(loc).equals("stats")) {
                    Sign sign = (Sign) loc.getBlock().getState();
                    sign.setLine(1, Main.c(null, "Games Active: &a" + CacheManager.getGames().size()));
                    sign.setLine(2, Main.c(null, "Players: &a" + CacheManager.getPlayers().size()));
                    sign.setLine(3, Main.c(null, "Players Queued: &a" + Main.getQueueManager().playersQueued()));

                    sign.update(true);
                }
            }

            return false;
        }


    }

    public void noLongerRecruiting() {
        recruiting = -1;
    }

    public int playersQueued() {
        return queue.size();
    }

    public int getRecruiting() {return recruiting;}

}
