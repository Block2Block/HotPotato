package me.Block2Block.HotPotato.Listeners;

import me.Block2Block.HotPotato.Entities.HotPotatoPlayer;
import me.Block2Block.HotPotato.Managers.CacheManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import javax.sql.rowset.CachedRowSet;

public class TeamSelectionListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player) {
            Player p = (Player) e.getWhoClicked();
            if (e.getClickedInventory().getName() == null) {
                return;
            }
            if (!ChatColor.stripColor(e.getClickedInventory().getName()).equals("Team Selection")) {
                return;
            }
            e.setCancelled(true);
            HotPotatoPlayer player = CacheManager.getPlayers().get(p.getUniqueId());
            if (e.getInventory().getItem(e.getSlot()).getItemMeta().getDisplayName() == null) {
                return;
            }
            if (e.getInventory().getItem(e.getSlot()).getType() == Material.AIR) {
                return;
            }

        }
    }

}
