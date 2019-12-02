package me.block2block.hotpotato.managers;

import com.nametagedit.plugin.NametagEdit;
import me.block2block.hotpotato.entities.HotPotatoPlayer;
import me.block2block.hotpotato.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class PlayerNameManager {

    public static void onGameJoin(HotPotatoPlayer player, int gameId) {
        if (Main.getInstance().getConfig().getBoolean("Settings.Player-Names.Enabled")) {
            if (!Main.getInstance().getConfig().getBoolean("Settings.Player-Names.Use-Custom-Playernames")) {
                if (Main.isNte()) {
                    if (player.isRed()) {
                        NametagEdit.getApi().setPrefix(player.getPlayer(), Main.c(false, Main.getInstance().getConfig().getString("Settings.Player-Names.Red-Format")));
                    } else {
                        NametagEdit.getApi().setPrefix(player.getPlayer(), Main.c(false, Main.getInstance().getConfig().getString("Settings.Player-Names.Blue-Format")));
                    }

                }
            }
        }



        for (Player p : Bukkit.getOnlinePlayers()) {

            if (CacheManager.getGames().get(gameId).getPlayers().contains(p)) {

                if (Main.getInstance().getConfig().getBoolean("Settings.Player-Names.Hide-Players-In-Games")) {
                    p.showPlayer(player.getPlayer());
                    player.getPlayer().showPlayer(p);
                }

                if (Main.getInstance().getConfig().getBoolean("Settings.Player-Names.Enabled")) {
                    if (Main.getInstance().getConfig().getBoolean("Settings.Player-Names.Use-Custom-Playernames")) {
                        if (player.isRed()) {
                            CacheManager.getPlayers().get(p.getUniqueId()).getScoreboard().getTeam("Red").addPlayer(player.getPlayer());
                        } else {
                            CacheManager.getPlayers().get(p.getUniqueId()).getScoreboard().getTeam("Blue").addPlayer(player.getPlayer());
                        }
                        if (CacheManager.getPlayers().get(p.getUniqueId()).isRed()) {
                            player.getScoreboard().getTeam("Red").addPlayer(CacheManager.getPlayers().get(p.getUniqueId()).getPlayer());
                        } else {
                            player.getScoreboard().getTeam("Blue").addPlayer(CacheManager.getPlayers().get(p.getUniqueId()).getPlayer());
                        }
                    } else {
                        if (!Main.isNte()) {
                            if (player.isRed()) {
                                CacheManager.getPlayers().get(p.getUniqueId()).getScoreboard().getTeam("Red").addPlayer(player.getPlayer());
                            } else {
                                CacheManager.getPlayers().get(p.getUniqueId()).getScoreboard().getTeam("Blue").addPlayer(player.getPlayer());
                            }
                            if (CacheManager.getPlayers().get(p.getUniqueId()).isRed()) {
                                player.getScoreboard().getTeam("Red").addPlayer(CacheManager.getPlayers().get(p.getUniqueId()).getPlayer());
                            } else {
                                player.getScoreboard().getTeam("Blue").addPlayer(CacheManager.getPlayers().get(p.getUniqueId()).getPlayer());
                            }
                        }
                    }
                }

            } else {
                if (Main.getInstance().getConfig().getBoolean("Settings.Player-Names.Hide-Players-In-Games")) {
                    p.hidePlayer(player.getPlayer());
                    player.getPlayer().hidePlayer(p);
                }
            }
        }

        if (Main.getInstance().getConfig().getBoolean("Settings.Player-Names.Enabled")) {
            if (!Main.getInstance().getConfig().getBoolean("Settings.Player-Names.Use-Custom-Playernames")) {
                if (!Main.isNte()) {
                    if (CacheManager.getPlayers().get(player.getUuid()).isRed()) {
                        player.getScoreboard().getTeam("Red").addPlayer(CacheManager.getPlayers().get(player.getUuid()).getPlayer());
                    } else {
                        player.getScoreboard().getTeam("Blue").addPlayer(CacheManager.getPlayers().get(player.getUuid()).getPlayer());
                    }
                }
            } else {
                if (CacheManager.getPlayers().get(player.getUuid()).isRed()) {
                    player.getScoreboard().getTeam("Red").addPlayer(CacheManager.getPlayers().get(player.getUuid()).getPlayer());
                } else {
                    player.getScoreboard().getTeam("Blue").addPlayer(CacheManager.getPlayers().get(player.getUuid()).getPlayer());
                }
            }
        }


    }

    public static void onServerJoin(Player p) {
        if (Main.getInstance().getConfig().getBoolean("Settings.Player-Names.Hide-Players-In-Games")) {
            for (UUID uuid : CacheManager.getPlayers().keySet()) {
                HotPotatoPlayer player = CacheManager.getPlayers().get(uuid);
                player.getPlayer().hidePlayer(p);
                p.hidePlayer(player.getPlayer());
            }
        }
    }

    public static void changeTeam(HotPotatoPlayer player, int gameId) {
        if (Main.getInstance().getConfig().getBoolean("Settings.Player-Names.Enabled")) {
            if (!Main.getInstance().getConfig().getBoolean("Settings.Player-Names.Use-Custom-Playernames")) {
                if (Main.isNte()) {
                    if (player.isRed()) {
                        NametagEdit.getApi().setPrefix(player.getPlayer(), Main.c(false, Main.getInstance().getConfig().getString("Settings.Player-Names.Red-Format")));
                    } else {
                        NametagEdit.getApi().setPrefix(player.getPlayer(), Main.c(false, Main.getInstance().getConfig().getString("Settings.Player-Names.Blue-Format")));
                    }
                }
            } else {
                for (Player p : CacheManager.getGames().get(gameId).getPlayers()) {
                    if (player.isRed()) {
                        CacheManager.getPlayers().get(p.getUniqueId()).getScoreboard().getTeam("Blue").removePlayer(player.getPlayer());
                        CacheManager.getPlayers().get(p.getUniqueId()).getScoreboard().getTeam("Red").addPlayer(player.getPlayer());
                    } else {
                        CacheManager.getPlayers().get(p.getUniqueId()).getScoreboard().getTeam("red").removePlayer(player.getPlayer());
                        CacheManager.getPlayers().get(p.getUniqueId()).getScoreboard().getTeam("Blue").addPlayer(player.getPlayer());
                    }

                }
            }
        }
    }

    public static void onGameEnd(List<Player> players) {
        for (Player p : players) {
            CacheManager.getPlayers().get(p.getUniqueId()).getScoreboard().getTeam("Blue").unregister();
            CacheManager.getPlayers().get(p.getUniqueId()).getScoreboard().getTeam("Red").unregister();
            for (Player p2 : Bukkit.getOnlinePlayers()) {
                if (!CacheManager.getPlayers().containsKey(p2.getUniqueId())) {
                    if (Main.getInstance().getConfig().getBoolean("Settings.Player-Names.Hide-Players-In-Games")) {
                        p2.showPlayer(p);
                        p.showPlayer(p2);
                    }
                }
            }

        }
    }

    public static void onGameLeave(Player p) {
        CacheManager.getPlayers().get(p.getUniqueId()).getScoreboard().getTeam("Blue").unregister();
        CacheManager.getPlayers().get(p.getUniqueId()).getScoreboard().getTeam("Red").unregister();
        for (Player p2 : Bukkit.getOnlinePlayers()) {
            if (!CacheManager.getPlayers().containsKey(p2.getUniqueId())) {
                if (Main.getInstance().getConfig().getBoolean("Settings.Player-Names.Hide-Players-In-Games")) {
                    p2.showPlayer(p);
                    p.showPlayer(p2);
                }
            }
        }
    }



}
