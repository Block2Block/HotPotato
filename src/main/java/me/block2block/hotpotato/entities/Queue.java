package me.block2block.hotpotato.entities;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Queue {

    private List<Player> data;
    private int count;


    public Queue() {
        data = new ArrayList<>();
        count = 0;
    }

    public int enQueue(Player p) {
        data.add(p);
        count++;
        return count;
    }

    public List<Player> deQueue() throws NullPointerException{
        if (count == 0) {
            throw new NullPointerException("There is nothing in the Queue.");
        } else {
            List<Player> toReturn = data;
            data = new ArrayList<>();
            count = 0;
            return  toReturn;
        }
    }

    public void removeFromQueue(Player p) {
        data.remove(p);
        count--;
    }

    public boolean isEmpty() {
        if (count == 0) {
            return true;
        }
        return false;
    }

    public boolean contains(Player p) {
        if (data.contains(p)) {
            return true;
        } else {
            return false;
        }
    }

    public void clear() {
        data.clear();
        count = 0;
    }

    public int size() {return count;}

}
