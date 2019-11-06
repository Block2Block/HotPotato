package me.Block2Block.HotPotato.Managers.Utils;

import me.Block2Block.HotPotato.Entities.Enchant;
import me.Block2Block.HotPotato.Main;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;

import java.util.Arrays;

import static org.bukkit.Material.POTION;

public class ItemUtil {


    public static ItemStack ci(Material type, String name, int amount, String lore, short data, String skullName, Enchant... enchants) {
        ItemStack is = new ItemStack(type, amount, data);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(Main.c(false, name));
        if (lore != null) {
            im.setLore(Arrays.asList(Main.c(false, lore).split(";")));
        }
        if (skullName != null) {
            SkullMeta sm = (SkullMeta) im;
            sm.setOwner(skullName);
            im = sm;
        }
        for (Enchant e : enchants) {
            im.addEnchant(e.e(), e.l(), true);
        }
        im.spigot().setUnbreakable(true);
        im.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);
        is.setItemMeta(im);
        return is;
    }
    public static ItemStack ci(Material type, String name, int amount, String lore, short data, Enchant... enchants) {
        return ci(type, name, amount, lore, data, null, enchants);
    }
    public static ItemStack ci(Material type, String name, int amount, String lore, Enchant... enchants) {
        return ci(type, name, amount, lore, (short)0, enchants);
    }
    public static ItemStack ci(Material type, String name, int amount, Enchant... enchants) {
        return ci(type, name, amount, null, enchants);
    }
    public static ItemStack ci(Material type, String name, Enchant... enchants) {
        return ci(type, name, 1, enchants);
    }
    public static ItemStack ci(Material type, Enchant... enchants) {
        return ci(type, "", enchants);
    }

    public static ItemStack potion(String name, int amount, String lore, PotionEffect... effects) {
        ItemStack p = ci(POTION, name, amount, lore);
        PotionMeta pm = (PotionMeta) p.getItemMeta();
        for (PotionEffect e : effects) {
            pm.addCustomEffect(e, true);
        }
        p.setItemMeta(pm);
        return p;
    }

}
