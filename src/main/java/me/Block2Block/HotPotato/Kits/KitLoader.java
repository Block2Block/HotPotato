package me.Block2Block.HotPotato.Kits;

import me.Block2Block.HotPotato.Entities.Enchant;
import me.Block2Block.HotPotato.Entities.Kit;
import me.Block2Block.HotPotato.Managers.KitCreator;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static org.bukkit.Material.*;

public class KitLoader {

    private static KitLoader instance;

    public KitLoader() {
        instance = this;
    }

    public static KitLoader get() {
        return instance;
    }

    public Kit Default() {
        KitCreator kit = new KitCreator(0, "That's Hot!", 0);
        kit.description(
                "&7That potato is pretty;" +
                        "&7hot, so you better go;" +
                        "&7catch it!,"
        );
        kit.helmet(IRON_HELMET, "&7" + kit.name()+"'s Helmet").chestplate(IRON_CHESTPLATE, "&7" + kit.name()+"'s Chestplate")
                .leggings(IRON_LEGGINGS, "&7" + kit.name()+"'s Leggings").boots(DIAMOND_BOOTS, "&7" + kit.name()+"'s Boots", new Enchant(Enchantment.PROTECTION_FALL, 2));
        kit.hb(1, IRON_SWORD, 1, "&7" + kit.name()+"'s Broadsword");
        kit.potion(true, 2, "&c"+kit.name()+"'s Shield Potion", null, 1, new PotionEffect(PotionEffectType.ABSORPTION, 500, 2));
        kit.hb(9, COOKED_BEEF, 12, "&7" + kit.name()+"'s Beef");
        kit.hb(3, FISHING_ROD, 1, "&7" + kit.name()+"'s Fishing Rod", new Enchant(Enchantment.DURABILITY, 200));
        kit.hb(4, COMPASS, 1, "&b&lTracking Compass");
        return kit.create();
    }

}
