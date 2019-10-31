package me.Block2Block.HotPotato.Managers.Utils;

import me.Block2Block.HotPotato.Kits.PlayerKit;
import me.Block2Block.HotPotato.Main;
import me.Block2Block.HotPotato.Managers.CacheManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.List;

import static org.bukkit.Material.*;

public class InventoryUtil {

    public static Inventory kitSelection(Player p) {
        Inventory inv = Bukkit.createInventory(null, 54, Main.c(null, "&a&lKit Selection"));
        List<Integer> kits = CacheManager.getPlayers().get(p.getUniqueId()).getPlayerData().getUnlockedKits();
        i(inv);
        for (PlayerKit kit : PlayerKit.values()) {
            if (kits.contains(kit)) {
                i(inv, kit.getGUISlot(), kit.getIcon(), "&a&l" + kit.getName(), 1, kit.getKit().lore(),
                        (short)0, true);
            } else {
                i(inv, kit.getGUISlot(), kit.getIcon(), "&c&l" + kit.getName(), 1,
                        "&ePrice: &7;&a" +
                                kit.getKit().price() + ";" +
                                kit.getKit().lore() + ";&a&lClick to purchase!");
            }
        }
        return inv;
    }

    public static Inventory confirm(PlayerKit kit) {
        Inventory inv = Bukkit.createInventory(null, 36, Main.c(null, "&c&lPurchase " + kit.getName() + "&c&l?"));
        for (int i = inv.getSize()-1; i>-1; i--) {
            i(inv, i, STAINED_GLASS_PANE, "Are you sure?", 1, null, (short)7);
        }
        i(inv, 4, kit.getIcon(), "&e&l" + kit.getName(), 1, kit.getKit().lore(), (short)0, true);
        int[] green = new int[]{9, 10, 11, 18, 19, 20, 27, 28, 29};
        int[] red = new int[]{15, 16, 17, 24, 25, 26, 33, 34, 35};
        int[] decor = new int[]{12, 14, 30, 32};
        for (int i : green) {
            i(inv, i, STAINED_CLAY, "&a&lYes", 1, null, (short)5);
        }
        for (int i : red) {
            i(inv, i, STAINED_CLAY, "&c&lCancel", 1, null, (short)14);
        }
        for (int i : decor) {
            i(inv, i, STAINED_GLASS_PANE, "Are you sure?");
        }
        return inv;
    }

    public static Inventory teamSelection(Player p) {
        Inventory inv = Bukkit.createInventory(null, 27, Main.c(null, "&a&lTeam Selection"));

        i(inv, 11, WOOL, "&cRed",1,"&rQueue for Red team",(short) 14, CacheManager.getPlayers().get(p.getUniqueId()).isRed());
        i(inv, 15, WOOL, "&3Blue",1,"&rQueue for Blue team",(short) 9, !CacheManager.getPlayers().get(p.getUniqueId()).isRed());

        return inv;
    }


    static void i(Inventory inv, int slot, Material type, String name, int amount, String lore, short data, boolean glowing, String skullName) {
        ItemStack is = new ItemStack(type, amount, data);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(Main.c(null, name));
        if (lore != null) {
            im.setLore(Arrays.asList(Main.c(null, lore).split(";")));
        }
        if (glowing) {
            im.addEnchant(Enchantment.DURABILITY, 1, true);
            im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        if (skullName != null) {
            SkullMeta sm = (SkullMeta) im;
            sm.setOwner(skullName);
            im = sm;
        }
        is.setItemMeta(im);
        inv.setItem(slot, is);
    }
    static void i(Inventory inv, int slot, Material type, String name, int amount, String lore, short data, boolean glowing) {
        i(inv, slot, type, name, amount, lore, data, glowing, null);
    }
    static void i(Inventory inv, int slot, Material type, String name, int amount, String lore, short data) {
        i(inv, slot, type, name, amount, lore, data, false);
    }
    static void i(Inventory inv, int slot, Material type, String name, int amount, String lore) {
        i(inv, slot, type, name, amount, lore, (short)0);
    }
    static void i(Inventory inv, int slot, Material type, String name, int amount) {
        i(inv, slot, type, name, amount, null);
    }
    static void i(Inventory inv, int slot, Material type, String name) {
        i(inv, slot, type, name, 1);
    }
    static void i(Inventory inv, int slot, Material type) {
        i(inv, slot, type, "");
    }
    static void i(Inventory inv) {
        for (int i = inv.getSize()-1; i>-1; i--) {
            if (inv.getItem(i) == null) {
                i(inv, i, STAINED_GLASS_PANE, " ");
            }
        }
        int[] a = new int[]{10,11,12,13,14,15,16,19,25,28,34,37,38,39,40,41,42,43};
        for (int i : a) {
            i(inv, i, STAINED_GLASS_PANE, " ", 1, null, (short)7);
        }
    }



}
