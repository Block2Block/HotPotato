package me.Block2Block.HotPotato.Kits.Abilities;

import me.Block2Block.HotPotato.Managers.CacheManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotatoWhacker implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerClick(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) {
            return;
        }
        if (e.getAction() != Action.LEFT_CLICK_BLOCK && e.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (e.getClickedBlock().getLocation().getWorld().getName().equals(e.getPlayer().getWorld().getName())) {
            if (!CacheManager.getPlayers().containsKey(e.getPlayer().getUniqueId())) {
                return;
            }
            FallingBlock fallingBlock = CacheManager.getGames().get(CacheManager.getPlayers().get(e.getPlayer().getUniqueId()).getGameID()).getFallingBlock();
            Location tntLocation = CacheManager.getGames().get(CacheManager.getPlayers().get(e.getPlayer().getUniqueId()).getGameID()).getTntLocation();
            if (tntLocation == null && fallingBlock == null) {
                return;
            } else if (tntLocation == null) {
                if (!e.getClickedBlock().getLocation().getBlock().equals(fallingBlock.getLocation().getBlock())) {
                    return;
                }
            } else {
                if (!e.getClickedBlock().getLocation().getBlock().equals(tntLocation.getBlock())) {
                    return;
                }
            }
            if (e.getClickedBlock().getType() == Material.TNT) {
                if (!CacheManager.getPlayers().get(e.getPlayer().getUniqueId()).getKit().name().equals("Potato Whacker")) {
                    return;
                }
                CacheManager.getGames().get(CacheManager.getPlayers().get(e.getPlayer().getUniqueId()).getGameID()).setLastHit(e.getPlayer());
                e.getClickedBlock().setType(Material.AIR);
                FallingBlock e2 = e.getClickedBlock().getLocation().getWorld().spawnFallingBlock(e.getClickedBlock().getLocation(), Material.TNT, (byte) 0);
                e2.setVelocity(e.getPlayer().getLocation().getDirection().setY(0.3).normalize().multiply(1.25));

                Squid squid = (Squid) e.getClickedBlock().getLocation().getWorld().spawnEntity(e.getClickedBlock().getLocation(), EntityType.SQUID);
                squid.setVelocity(e.getPlayer().getLocation().getDirection().setY(0.3).normalize().multiply(1.25));

                CacheManager.getGames().get(CacheManager.getPlayers().get(e.getPlayer().getUniqueId()).getGameID()).setSquid(squid);

                e2.setPassenger(squid);

                LivingEntity l = (LivingEntity) squid;
                l.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1000000, 2, true, false), true);

                CacheManager.getGames().get(CacheManager.getPlayers().get(e.getPlayer().getUniqueId()).getGameID()).setFallingBlock(e2);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClickMidairLeft(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getEntity() instanceof Squid) {
            if (!CacheManager.getPlayers().containsKey(e.getDamager().getUniqueId())) {
                return;
            }
            Player p = (Player) e.getDamager();
            FallingBlock fallingBlock = CacheManager.getGames().get(CacheManager.getPlayers().get(p.getUniqueId()).getGameID()).getFallingBlock();
            if (e.getEntity().getWorld().getName().equals(fallingBlock.getWorld().getName())) {
                if (!CacheManager.getPlayers().get(p.getUniqueId()).getKit().name().equals("Potato Whacker")) {
                    return;
                }
                e.setCancelled(true);
                CacheManager.getGames().get(CacheManager.getPlayers().get(p.getUniqueId()).getGameID()).setLastHit((Player) e.getDamager());
                fallingBlock.setVelocity(e.getDamager().getLocation().getDirection().setY(0.3).normalize().multiply(1.25));
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClickMidairRight(PlayerInteractEntityEvent e) {
        if (e.getRightClicked() instanceof FallingBlock) {
            if (!CacheManager.getPlayers().containsKey(e.getPlayer().getUniqueId())) {
                return;
            }
            FallingBlock fallingBlock = CacheManager.getGames().get(CacheManager.getPlayers().get(e.getPlayer().getUniqueId()).getGameID()).getFallingBlock();
            if (e.getRightClicked().getWorld().getName().equals(fallingBlock.getWorld().getName())) {
                if (!CacheManager.getPlayers().get(e.getPlayer().getUniqueId()).getKit().name().equals("Potato Whacker")) {
                    return;
                }
                CacheManager.getGames().get(CacheManager.getPlayers().get(e.getPlayer().getUniqueId()).getGameID()).setLastHit(e.getPlayer());
                fallingBlock.setVelocity(e.getPlayer().getLocation().getDirection().setY(0.3).normalize().multiply(1.25));
            }
        }
    }

}
