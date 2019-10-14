package me.Block2Block.HotPotato.Kits;

import me.Block2Block.HotPotato.Entities.Enchant;
import me.Block2Block.HotPotato.Entities.Kit;
import me.Block2Block.HotPotato.Managers.KitCreator;
import org.bukkit.enchantments.Enchantment;

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

    public Kit PotatoWhacker() {
        KitCreator kit = new KitCreator(0, "That's Hot!", 0);
        kit.description(
                "&7You have learned the skill of;" +
                        "&7hitting potatoes really far!;" +
                        "&7You can hit the potato further!,"
        );
        kit.hb(0, STICK, 1, "&a&lHot Potato", new Enchant(Enchantment.KNOCKBACK, 1));
        return kit.create();
    }

}