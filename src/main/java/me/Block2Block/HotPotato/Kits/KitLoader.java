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
        kit.hb(0, BAKED_POTATO, 1, "&a&lHot Potato");
        return kit.create();
    }

}
