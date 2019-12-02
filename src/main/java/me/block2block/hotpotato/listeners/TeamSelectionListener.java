package me.block2block.hotpotato.listeners;

import me.block2block.hotpotato.entities.HotPotatoPlayer;
import me.block2block.hotpotato.managers.CacheManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class TeamSelectionListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player) {
            Player p = (Player) e.getWhoClicked();
            if (e.getClickedInventory() == null) {
                return;
            }
            if (e.getClickedInventory().getName() == null) {
                return;
            }
            if (e.getClickedInventory().getName().equals("")) {
                return;
            }
            if (!ChatColor.stripColor(e.getClickedInventory().getName()).equals("Team Selection")) {
                return;
            }
            HotPotatoPlayer player = CacheManager.getPlayers().get(p.getUniqueId());
            if (e.getInventory().getItem(e.getSlot()) == null) {
                return;
            }
            if (e.getInventory().getItem(e.getSlot()).getType() == Material.AIR) {
                return;
            }

                ItemStack item = e.getInventory().getItem(e.getSlot());
                String itemName = ChatColor.stripColor(item.getItemMeta().getDisplayName());

                e.setCancelled(true);

                if (itemName.equals("Red")) {
                    if (!player.isRed()) {
                        if (CacheManager.getGames().get(player.getGameID()).getRed().size() < CacheManager.getGames().get(player.getGameID()).getBlue().size()) {
                            CacheManager.getGames().get(player.getGameID()).addToTeam(player);
                        } else {
                            CacheManager.getGames().get(player.getGameID()).queueForTeam(player);
                        }
                    } else {
                        p.playSound(p.getLocation(), Sound.ITEM_BREAK, 100, 2);
                    }
                } else if (itemName.equals("Blue")) {
                    if (player.isRed()) {
                        if (CacheManager.getGames().get(player.getGameID()).getRed().size() > CacheManager.getGames().get(player.getGameID()).getBlue().size()) {
                            CacheManager.getGames().get(player.getGameID()).addToTeam(player);
                        } else {
                            CacheManager.getGames().get(player.getGameID()).queueForTeam(player);
                        }
                    } else {
                        p.playSound(p.getLocation(), Sound.ITEM_BREAK, 100, 2);
                    }
                } else {
                    return;
                }

        }
    }

}
