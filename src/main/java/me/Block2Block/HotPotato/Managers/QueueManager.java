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
            p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Game.Queue.Leave")));

            for (Location loc : CacheManager.getSigns().keySet()) {
                if (CacheManager.getSigns().get(loc).equals("queue")) {
                    Sign sign = (Sign) loc.getBlock().getState();

                    int counter = 0;
                    for (String s : Main.getInstance().getConfig().getStringList("Settings.Signs.Queue-Format")) {
                        sign.setLine(counter, Main.c(false, s.replace("{games-active}",CacheManager.getGames().size() + "").replace("{players}",CacheManager.getPlayers().size() + "").replace("{queued}",Main.getQueueManager().playersQueued() + "")));
                        counter++;
                        if (counter == 4) {
                            break;
                        }
                    }

                    sign.update(true);
                } else if (CacheManager.getSigns().get(loc).equals("stats")) {
                    Sign sign = (Sign) loc.getBlock().getState();

                    int counter = 0;
                    for (String s : Main.getInstance().getConfig().getStringList("Settings.Signs.Stats-Format")) {
                        sign.setLine(counter, Main.c(false, s.replace("{games-active}",CacheManager.getGames().size() + "").replace("{players}",CacheManager.getPlayers().size() + "").replace("{queued}",Main.getQueueManager().playersQueued() + "")));
                        counter++;
                        if (counter == 4) {
                            break;
                        }
                    }

                    sign.update(true);
                }
            }

            return false;
        }
        int count = queue.enQueue(p);
        if (count >= Main.getInstance().getConfig().getInt("Settings.Game.Queue-Min-New-Game")) {
            if (CacheManager.getMaps().size() < 1) {
                for (Player player : queue.deQueue()) {
                    p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Game.Queue.No-Maps")));
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

                    int counter = 0;
                    for (String s : Main.getInstance().getConfig().getStringList("Settings.Signs.Queue-Format")) {
                        sign.setLine(counter, Main.c(false, s.replace("{games-active}",CacheManager.getGames().size() + "").replace("{players}",CacheManager.getPlayers().size() + "").replace("{queued}",Main.getQueueManager().playersQueued() + "")));
                        counter++;
                        if (counter == 4) {
                            break;
                        }
                    }

                    sign.update(true);
                } else if (CacheManager.getSigns().get(loc).equals("stats")) {
                    Sign sign = (Sign) loc.getBlock().getState();

                    int counter = 0;
                    for (String s : Main.getInstance().getConfig().getStringList("Settings.Signs.Stats-Format")) {
                        sign.setLine(counter, Main.c(false, s.replace("{games-active}",CacheManager.getGames().size() + "").replace("{players}",CacheManager.getPlayers().size() + "").replace("{queued}",Main.getQueueManager().playersQueued() + "")));
                        counter++;
                        if (counter == 4) {
                            break;
                        }
                    }

                    sign.update(true);
                }
            }

            return true;
        } else {
            if (recruiting != -1) {
                CacheManager.getGames().get(recruiting).join(queue.deQueue());
            } else {
                p.sendMessage(Main.c(true, Main.getInstance().getConfig().getString("Messages.Game.Queue.Join")));
            }

            for (Location loc : CacheManager.getSigns().keySet()) {
                if (CacheManager.getSigns().get(loc).equals("queue")) {
                    Sign sign = (Sign) loc.getBlock().getState();
                    int counter = 0;
                    for (String s : Main.getInstance().getConfig().getStringList("Settings.Signs.Queue-Format")) {
                        sign.setLine(counter, Main.c(false, s.replace("{games-active}",CacheManager.getGames().size() + "").replace("{players}",CacheManager.getPlayers().size() + "").replace("{queued}",Main.getQueueManager().playersQueued() + "")));
                        counter++;
                        if (counter == 4) {
                            break;
                        }
                    }
                    sign.update(true);
                } else if (CacheManager.getSigns().get(loc).equals("stats")) {
                    Sign sign = (Sign) loc.getBlock().getState();
                    int counter = 0;
                    for (String s : Main.getInstance().getConfig().getStringList("Settings.Signs.Stats-Format")) {
                        sign.setLine(counter, Main.c(false, s.replace("{games-active}",CacheManager.getGames().size() + "").replace("{players}",CacheManager.getPlayers().size() + "").replace("{queued}",Main.getQueueManager().playersQueued() + "")));
                        counter++;
                        if (counter == 4) {
                            break;
                        }
                    }

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
