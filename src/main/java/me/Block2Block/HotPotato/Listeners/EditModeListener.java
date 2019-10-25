package me.Block2Block.HotPotato.Listeners;

import me.Block2Block.HotPotato.Main;
import me.Block2Block.HotPotato.Managers.CacheManager;
import me.Block2Block.HotPotato.Managers.Utils.ItemUtil;
import me.Block2Block.HotPotato.Managers.Utils.ZipUtil;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

public class EditModeListener implements Listener {

    private static String data = "";

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (CacheManager.isEditor(e.getPlayer())) {
            if (CacheManager.isSetup(e.getPlayer())) {
                switch (e.getMessage().toLowerCase()) {
                    case "done":
                        e.setCancelled(true);
                        switch (CacheManager.getSetupStage()) {
                            case 0:
                                return;
                            case 1:
                                if (data.equals("")) {
                                    e.getPlayer().sendMessage(Main.c("HotPotato","You must specify at least 1 Red Spawn Point."));
                                } else {
                                    CacheManager.addData(data);
                                    data = "";
                                    CacheManager.setSetupStage(2);
                                    e.getPlayer().sendMessage(Main.c("HotPotato","Now, please stand wherever you want BLUE team spawns and click the stick. Please ensure you look in the direction you want them to spawn. When you are done, type 'done'."));
                                }
                                break;
                            case 2:
                                if (data.equals("")) {
                                    e.getPlayer().sendMessage(Main.c("HotPotato","You must specify at least 1 Blue Spawn Point."));
                                } else {
                                    CacheManager.addData(data);
                                    data = "";
                                    CacheManager.setSetupStage(3);
                                    e.getPlayer().sendMessage(Main.c("HotPotato","Next, you need to specify where you want the TNT spawn. Please stand where you want them to spawn and click the stick. When you are done, type 'done'."));
                                }
                                break;
                            case 3:
                                if (data.equals("")) {
                                    e.getPlayer().sendMessage(Main.c("HotPotato","You must specify at least 1 TNT Spawn Point."));
                                } else {
                                    CacheManager.addData(data);
                                    data = "";
                                    CacheManager.setSetupStage(4);
                                    e.getPlayer().sendMessage(Main.c("HotPotato","Now you need to specify where you would like your Waiting Lobby. Please stand where you would like them to spawn and click the stick. Please look in the direction you would like them to spawn. Please note: This MUST be in the SAME WORLD as your map. The plugin will not make the waiting lobby disappear, so it is recommended to put it out of view of the map."));
                                }
                                break;
                            default:
                                e.setCancelled(false);
                                break;
                        }

                        break;
                    case "confirm":
                        if (CacheManager.getSetupStage() == 0) {
                            e.setCancelled(true);
                            CacheManager.setSetupStage(1);
                            e.getPlayer().sendMessage(Main.c("HotPotato","You have been given the Setup Stick. Click this when you are in position."));
                            ItemStack i = ItemUtil.ci(Material.STICK, "&2&lHotPotato Setup Stick", 1, "&rUse this item to;&rsetup your HotPotato;&rmap.");
                            e.getPlayer().getInventory().addItem(i);

                            e.getPlayer().sendMessage(Main.c("HotPotato","To start off, please stand wherever you want RED team spawns and click on the stick. Please ensure you are looking in the direction you want them to spawn. When you are done, type 'done'."));
                        }
                        break;
                    case "cancel":
                            e.setCancelled(true);
                            e.getPlayer().sendMessage(Main.c("HotPotato","You have stopped setting up the map. Any points setup have been deleted."));
                            CacheManager.setSetupStage(0);
                            CacheManager.getData().clear();
                            CacheManager.exitSetup();
                            data = "";
                        break;

                    default:
                        if (CacheManager.getSetupStage() == 5) {
                            if (!e.getMessage().equals("")) {
                                e.getPlayer().sendMessage(Main.c("HotPotato","You have set the name of the map."));
                                e.getPlayer().sendMessage(Main.c("HotPotato","Finally, please specify the authors of the map. Please enter the map author."));
                                CacheManager.addData(e.getMessage());
                                e.setCancelled(true);
                                CacheManager.setSetupStage(6);
                            } else {
                                e.getPlayer().sendMessage(Main.c("HotPotato","You must specify a map name. If you wish to cancel setup, please enter 'cancel'."));
                            }
                        } else if (CacheManager.getSetupStage() == 6) {
                            if (!e.getMessage().equals("")) {
                                e.setCancelled(true);
                                e.getPlayer().sendMessage(Main.c("HotPotato","You have set the map author. Map setup is complete!"));
                                List<String> totalData = CacheManager.getData();
                                totalData.add(e.getMessage());

                                totalData.add(totalData.get(4).replace(" ", "").toLowerCase());

                                if ((new File(Main.getInstance().getDataFolder().getAbsolutePath() + "/maps", totalData.get(4).replace(" ", "").toLowerCase() + ".zip")).exists()) {
                                    e.getPlayer().sendMessage(Main.c("HotPotato","You already have a map named " + totalData.get(4) + ". Please rename your map and try again."));
                                    data = "";
                                    CacheManager.getData().clear();
                                    CacheManager.exitSetup();
                                    return;
                                }

                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        World w = e.getPlayer().getWorld();
                                        for (Player p : w.getPlayers()) {
                                            p.teleport(CacheManager.getLobby());
                                            p.sendMessage(Main.c("HotPotato", "You have been teleported to the lobby as the map you were on was just set up. You are no longer in edit mode."));
                                            CacheManager.getEditMode().remove(p);
                                        }

                                        Bukkit.unloadWorld(w, true);

                                        ZipUtil zu = new ZipUtil();
                                        try {
                                            zu.zipFile(w.getWorldFolder().toPath(), Paths.get(Main.getInstance().getDataFolder().getAbsolutePath() + "/maps/" + totalData.get(4).replace(" ", "").toLowerCase() + ".zip"));
                                        } catch (Exception exception) {
                                            e.getPlayer().sendMessage(Main.c("HotPotato","Unable to generate ZIP folder. Please ZIP the folder yourself (with the world files in the root), name it " + totalData.get(4).replace(" ", "").toLowerCase() + ".zip, and put it in the 'Maps' folder in the plugins data folder."));
                                        }

                                        try {
                                            FileUtils.deleteDirectory(w.getWorldFolder());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }.runTask(Main.getInstance());

                                new BukkitRunnable(){
                                    @Override
                                    public void run() {
                                        Main.getDbManager().addMap(totalData);
                                    }
                                }.runTaskAsynchronously(Main.getInstance());

                            } else {
                                e.getPlayer().sendMessage(Main.c("HotPotato","You must specify map authors. If you wish to cancel setup, please enter 'cancel'."));
                            }
                        }
                }
            } else {
                switch (e.getMessage().toLowerCase()) {
                    case "confirm":
                        if (CacheManager.getFinishPlayer() != null) {
                            if (CacheManager.getFinishPlayer().equals(e.getPlayer())) {
                                e.setCancelled(true);
                                CacheManager.setFinishPlayer(null);

                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        World w = Bukkit.getWorld("HPEdit");
                                        for (Player p : w.getPlayers()) {
                                            p.teleport(CacheManager.getLobby());
                                            p.sendMessage(Main.c("HotPotato","Your map was marked finished. It has now been deleted."));
                                        }
                                        Bukkit.getServer().unloadWorld(w, false);
                                        File worldFolder = w.getWorldFolder();
                                        try  {
                                            FileUtils.deleteDirectory(worldFolder);
                                        } catch (Exception e2) {
                                            e2.printStackTrace();
                                        }
                                    }
                                }.runTask(Main.getInstance());

                            }
                        }
                        break;
                    case "cancel":
                        if (CacheManager.getFinishPlayer() != null) {
                            if (CacheManager.getFinishPlayer().equals(e.getPlayer())) {
                                CacheManager.setFinishPlayer(null);
                                e.getPlayer().sendMessage(Main.c("HotPotato","You have cancelled finishing your map."));
                                e.setCancelled(true);
                            }
                        }
                }

            }
        }
    }

    @EventHandler
    public void onStickCLick(PlayerInteractEvent e) {
        if (e.getItem() == null) {
            return;
        }
        if (e.getItem().getType() == Material.STICK) {
            if (CacheManager.isSetup(e.getPlayer()) && ChatColor.stripColor(e.getItem().getItemMeta().getDisplayName()).equals("HotPotato Setup Stick")) {
                switch (CacheManager.getSetupStage()) {
                    case 0:
                        return;
                    case 1:
                        if (data.equals("")) {
                            Location l = e.getPlayer().getLocation();
                            data = l.getX() + "," + l.getY() + "," + l.getZ() + "," + l.getYaw() + "," + l.getPitch();
                        } else {
                            Location l = e.getPlayer().getLocation();
                            data += ";"  + l.getX() + "," + l.getY() + "," + l.getZ() + "," + l.getYaw() + "," + l.getPitch();
                        }
                        e.getPlayer().sendMessage(Main.c("HotPotato","Red Spawn Point added!"));
                        break;
                    case 2:
                        if (data.equals("")) {
                            Location l = e.getPlayer().getLocation();
                            data = l.getX() + "," + l.getY() + "," + l.getZ() + "," + l.getYaw() + "," + l.getPitch();
                        } else {
                            Location l = e.getPlayer().getLocation();
                            data += ";"  + l.getX() + "," + l.getY() + "," + l.getZ() + "," + l.getYaw() + "," + l.getPitch();
                        }
                        e.getPlayer().sendMessage(Main.c("HotPotato","Blue Spawn Point added!"));
                        break;
                    case 3:
                        if (data.equals("")) {
                            Location l = e.getPlayer().getLocation();
                            data = l.getX() + "," + l.getY() + "," + l.getZ();
                        } else {
                            Location l = e.getPlayer().getLocation();
                            data += ";"  + l.getX() + "," + l.getY() + "," + l.getZ() + "," + l.getYaw() + "," + l.getPitch();
                        }
                        e.getPlayer().sendMessage(Main.c("HotPotato","TNT Spawn Point added!"));
                        break;
                    case 4:
                        Location l = e.getPlayer().getLocation();
                        CacheManager.addData(l.getX() + "," + l.getY() + "," + l.getZ() + "," + l.getYaw() + "," + l.getPitch());
                        e.getPlayer().sendMessage(Main.c("HotPotato","Waiting Lobby Location set!"));
                        e.getPlayer().sendMessage(Main.c("HotPotato","Now you need to specify a name for your map. Please enter a name for your map."));
                        CacheManager.setSetupStage(5);
                        break;
                }
            }
        }
    }

    public static void onLeave() {
        data = "";
    }

}
