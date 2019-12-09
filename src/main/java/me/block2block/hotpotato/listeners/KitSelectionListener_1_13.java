package me.block2block.hotpotato.listeners;

import me.block2block.hotpotato.Main;
import me.block2block.hotpotato.entities.HotPotatoPlayer;
import me.block2block.hotpotato.kits.PlayerKit;
import me.block2block.hotpotato.managers.CacheManager;
import me.block2block.hotpotato.managers.ScoreboardManager;
import me.block2block.hotpotato.managers.utils.InventoryUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class KitSelectionListener_1_13 implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player) {
            Player p = (Player) e.getWhoClicked();
            if (e.getClickedInventory() == null) {
                return;
            }
            if (e.getView().getTitle() == null) {
                return;
            }
            if (!ChatColor.stripColor(e.getView().getTitle()).equals("Kit Selection") && !ChatColor.stripColor(e.getView().getTitle()).matches("Purchase [a-zA-Z ]+\\?")) {
                return;
            }
            List<Integer> kits = CacheManager.getPlayers().get(p.getUniqueId()).getPlayerData().getUnlockedKits();
            if (ChatColor.stripColor(e.getView().getTitle()).equals("Kit Selection")) {
                e.setCancelled(true);
                int slot = e.getSlot();
                for (PlayerKit kit : PlayerKit.values()) {
                    if (slot == kit.getGUISlot()) {
                        if (kits.contains(kit.getId())) {
                            CacheManager.getPlayers().get(p.getUniqueId()).setKit(kit.getKit());
                            ScoreboardManager.changeLine(p, 5, "" + kit.getName());
                            p.sendMessage(Main.c(true, Main.getInstance().getConfig().getString("Messages.Kits.Kit-Equipped").replace("{kit-name}",kit.getName())));
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
                    if (ChatColor.stripColor(e.getView().getTitle()).matches("Purchase [a-zA-Z ]+\\?") && ChatColor.stripColor(e.getView().getTitle()).contains(kit.getName())) {
                        e.setCancelled(true);
                        ItemStack clicked = e.getCurrentItem();
                        if (clicked.getItemMeta().getDisplayName().equals(Main.c(false, "&a&lYes"))) {
                            HotPotatoPlayer player = CacheManager.getPlayers().get(p.getUniqueId());
                            if (player.getPlayerData().getBalance()>=kit.getKit().price()) {
                                player.setKit(kit.getKit());
                                player.getPlayerData().addKit(kit.getId());
                                player.getPlayerData().removeFromBalance(kit.getKit().price());
                                p.sendMessage(Main.c(true, Main.getInstance().getConfig().getString("Messages.Kits.Kit-Purchased").replace("{kit-name}",kit.getName())));
                                p.openInventory(InventoryUtil.kitSelection(p));
                            } else {
                                p.sendMessage(Main.c(true, Main.getInstance().getConfig().getString("Messages.Kits.Insufficient-Funds").replace("{kit-name}",kit.getName())));
                                p.closeInventory();
                            }
                        }
                        if (clicked.getItemMeta().getDisplayName().equals(Main.c(false, "&c&lCancel"))) {
                            p.openInventory(InventoryUtil.kitSelection(p));
                        }
                    }
                }
            }
        }

    }

}
