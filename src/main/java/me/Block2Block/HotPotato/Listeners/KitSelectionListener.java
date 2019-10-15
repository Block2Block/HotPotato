package me.Block2Block.HotPotato.Listeners;

import me.Block2Block.HotPotato.Entities.HotPotatoPlayer;
import me.Block2Block.HotPotato.Entities.PlayerData;
import me.Block2Block.HotPotato.Kits.PlayerKit;
import me.Block2Block.HotPotato.Main;
import me.Block2Block.HotPotato.Managers.CacheManager;
import me.Block2Block.HotPotato.Managers.ScoreboardManager;
import me.Block2Block.HotPotato.Managers.Utils.InventoryUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import sun.awt.geom.AreaOp;

import java.util.List;

public class KitSelectionListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player) {
            Player p = (Player) e.getWhoClicked();
            if (e.getClickedInventory().getName() == null) {
                return;
            }
            if (!e.getClickedInventory().getName().equals(InventoryUtil.kitSelection(p).getName()) && !ChatColor.stripColor(e.getClickedInventory().getName()).matches("Purchase [a-zA-Z]+\\?")) {
                return;
            }
            List<Integer> kits = CacheManager.getPlayers().get(p.getUniqueId()).getPlayerData().getUnlockedKits();
            if (e.getClickedInventory().getName().equals(InventoryUtil.kitSelection(p).getName())) {
                e.setCancelled(true);
                int slot = e.getSlot();
                for (PlayerKit kit : PlayerKit.values()) {
                    if (slot == kit.getGUISlot()) {
                        if (kits.contains(kit.getId())) {
                            CacheManager.getPlayers().get(p.getUniqueId()).setKit(kit.getKit());
                            ScoreboardManager.changeLine(p, 5, "&r" + kit.getName());
                            p.sendMessage(Main.c("Kits", "You equipped the &a" + kit.getName() + "&r kit."));
                            p.closeInventory();
                        } else {
                            try {
                                p.openInventory(InventoryUtil.confirm(kit));
                            } catch (Throwable t) {
                                t.printStackTrace();
                            }
                        }
                    }
                }
            } else {
                for (PlayerKit kit : PlayerKit.values()) {
                    if (e.getClickedInventory().getName().equals(InventoryUtil.confirm(kit).getName())) {
                        e.setCancelled(true);
                        ItemStack clicked = e.getCurrentItem();
                        if (clicked.getItemMeta().getDisplayName().equals(Main.c(null, "&a&lYes"))) {
                            HotPotatoPlayer player = CacheManager.getPlayers().get(p.getUniqueId());
                            if (player.getPlayerData().getBalance()>=kit.getKit().price()) {
                                player.setKit(kit.getKit());
                                player.getPlayerData().addKit(kit.getId());
                                player.getPlayerData().removeFromBalance(kit.getKit().price());
                                Main.getDbManager().addKit(kit.getId(), p);
                                p.sendMessage(Main.c("Kits", "You purchased the &a" + kit.getName() + "&r kit."));
                                p.openInventory(InventoryUtil.kitSelection(p));
                            } else {
                                p.sendMessage(Main.c("Kits", "You do not have enough money to purchase this kit!"));
                                p.closeInventory();
                            }
                        }
                        if (clicked.getItemMeta().getDisplayName().equals(Main.c(null, "&c&lCancel"))) {
                            p.openInventory(InventoryUtil.kitSelection(p));
                        }
                    }
                }
            }
        }

    }

}
