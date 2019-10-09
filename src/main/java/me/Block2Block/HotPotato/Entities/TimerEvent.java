package me.Block2Block.HotPotato.Entities;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TimerEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final TimerType type;

    public TimerEvent(TimerType type) {
        this.type = type;
    }

    public TimerType getType() {
        return type;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public enum TimerType {
        TICK("Tick", 0, 0.05),
        QUARTER("Quarter", 5, 0.25),
        HALF("Half", 10, 0.5),
        SECOND("Second", 20, 1),
        MINUTE("Minute", 1200, 60);

        String name;
        int ticks;
        double seconds;

        TimerType(String name, int ticks, double seconds) {
            this.name = name;
            this.ticks = ticks;
            this.seconds = seconds;
        }

        public String getName() {
            return name;
        }

        public int getTicks() {
            return ticks;
        }

        public double getSeconds() {
            return seconds;
        }
    }

}

