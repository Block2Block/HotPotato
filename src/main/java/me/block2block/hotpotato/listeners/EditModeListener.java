package me.block2block.hotpotato.listeners;

import me.block2block.hotpotato.Main;
import me.block2block.hotpotato.managers.CacheManager;
import me.block2block.hotpotato.managers.utils.ItemUtil;
import me.block2block.hotpotato.managers.utils.ZipUtil;
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
                                    e.getPlayer().sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Setup.Must-Specify-Red")));
                                } else {
                                    CacheManager.addData(data);
                                    data = "";
                                    CacheManager.setSetupStage(2);
                                    e.getPlayer().sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Setup.Please-Set-Blue")));
                                }
                                break;
                            case 2:
                                if (data.equals("")) {
                                    e.getPlayer().sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Setup.Must-Specify-Blue")));
                                } else {
                                    CacheManager.addData(data);
                                    data = "";
                                    CacheManager.setSetupStage(3);
                                    e.getPlayer().sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Setup.Please-Set-TNT")));
                                }
                                break;
                            case 3:
                                if (data.equals("")) {
                                    e.getPlayer().sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Setup.Must-Specify-TNT")));
                                } else {
                                    CacheManager.addData(data);
                                    data = "";
                                    CacheManager.setSetupStage(4);
                                    e.getPlayer().sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Setup.Please-Set-Waiting-Lobby")));
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
                            e.getPlayer().sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Setup.Given-Stick")));
                            ItemStack i = ItemUtil.ci(Material.STICK, "&2&lHotPotato Setup Stick", 1, "&rUse this item to;&rsetup your HotPotato;&rmap.");
                            e.getPlayer().getInventory().addItem(i);

                            e.getPlayer().sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Setup.Please-Set-Red")));
                        }
                        break;
                    case "cancel":
                            e.setCancelled(true);
                            e.getPlayer().sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Setup.Setup-Cancelled")));
                            CacheManager.setSetupStage(0);
                            CacheManager.getData().clear();
                            CacheManager.exitSetup();
                            data = "";
                        break;

                    default:
                        if (CacheManager.getSetupStage() == 5) {
                            if (!e.getMessage().equals("")) {
                                e.getPlayer().sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Setup.Name-Set")));
                                e.getPlayer().sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Setup.Please-Set-Author")));
                                CacheManager.addData(e.getMessage());
                                e.setCancelled(true);
                                CacheManager.setSetupStage(6);
                            } else {
                                e.getPlayer().sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Setup.Must-Specify-Name")));
                            }
                        } else if (CacheManager.getSetupStage() == 6) {
                            if (!e.getMessage().equals("")) {
                                e.setCancelled(true);
                                e.getPlayer().sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Setup.Author-Set")));
                                List<String> totalData = CacheManager.getData();
                                totalData.add(e.getMessage());

                                totalData.add(totalData.get(4).replace(" ", "").toLowerCase());

                                if ((new File(Main.getInstance().getDataFolder().getAbsolutePath() + "/maps", totalData.get(4).replace(" ", "").toLowerCase() + ".zip")).exists()) {
                                    e.getPlayer().sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Setup.Map-Name-Exists")));
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
                                            p.sendMessage(Main.c(true, Main.getInstance().getConfig().getString("Messages.Commands.Setup.Teleported-Setup-Complete")));
                                            CacheManager.getEditMode().remove(p);
                                        }

                                        Bukkit.unloadWorld(w, true);

                                        ZipUtil zu = new ZipUtil();
                                        try {
                                            zu.zipFile(w.getWorldFolder().toPath(), Paths.get(Main.getInstance().getDataFolder().getAbsolutePath() + "/maps/" + totalData.get(4).replace(" ", "").toLowerCase() + ".zip"));
                                        } catch (Exception exception) {
                                            e.getPlayer().sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Setup.ZIP-Generation-Fail").replace("{zip-name}",totalData.get(4).replace(" ", "").toLowerCase())));
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
                                e.getPlayer().sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Setup.Must-Specify-Author")));
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
                                            p.sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Finish.Map-Marked-Finished")));
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
                                e.getPlayer().sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Finish.Finish-Cancelled")));
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
                        e.getPlayer().sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Setup.Red-Spawn-Added")));
                        break;
                    case 2:
                        if (data.equals("")) {
                            Location l = e.getPlayer().getLocation();
                            data = l.getX() + "," + l.getY() + "," + l.getZ() + "," + l.getYaw() + "," + l.getPitch();
                        } else {
                            Location l = e.getPlayer().getLocation();
                            data += ";"  + l.getX() + "," + l.getY() + "," + l.getZ() + "," + l.getYaw() + "," + l.getPitch();
                        }
                        e.getPlayer().sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Setup.Blue-Spawn-Added")));
                        break;
                    case 3:
                        if (data.equals("")) {
                            Location l = e.getPlayer().getLocation();
                            data = l.getX() + "," + l.getY() + "," + l.getZ();
                        } else {
                            Location l = e.getPlayer().getLocation();
                            data += ";"  + l.getX() + "," + l.getY() + "," + l.getZ() + "," + l.getYaw() + "," + l.getPitch();
                        }
                        e.getPlayer().sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Setup.TNT-Spawn-Added")));
                        break;
                    case 4:
                        Location l = e.getPlayer().getLocation();
                        CacheManager.addData(l.getX() + "," + l.getY() + "," + l.getZ() + "," + l.getYaw() + "," + l.getPitch());
                        e.getPlayer().sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Setup.Waiting-Lobby-Set")));
                        e.getPlayer().sendMessage(Main.c(true,Main.getInstance().getConfig().getString("Messages.Commands.Setup.Please-Set-Name")));
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
