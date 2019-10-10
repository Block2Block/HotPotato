package me.Block2Block.HotPotato.Managers;

import me.Block2Block.HotPotato.Entities.Game;
import me.Block2Block.HotPotato.Entities.Queue;
import me.Block2Block.HotPotato.Main;
import org.bukkit.entity.Player;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

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
        int count = queue.enQueue(p);
        if (count == 2) {
            int map = Main.chooseRan(0,CacheManager.getMaps().size() - 1);
            Game newGame = new Game(counter, CacheManager.getMaps().get(map));
            newGame.join(queue.deQueue());
            CacheManager.newGame(newGame, counter);
            counter++;
            return true;
        } else {
            if (recruiting != -1) {
                CacheManager.getGames().get(recruiting).join(queue.deQueue());
            } else {
                p.sendMessage(Main.c("Queue Manager", "You have been added to the game queue."));
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

}
