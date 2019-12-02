package me.block2block.hotpotato.kits;

import me.block2block.hotpotato.entities.Kit;
import me.block2block.hotpotato.managers.KitCreator;

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
        return kit.create();
    }

    public Kit PotatoWhacker() {
        KitCreator kit = new KitCreator(1, "Potato Whacker", 1000);
        kit.description(
                "&7You have learned the skill of;" +
                        "&7hitting potatoes really far!;" +
                        "&7You can hit the potato further!,"
        );
        return kit.create();
    }

    public Kit Leaper() {
        KitCreator kit = new KitCreator(1, "Leaper", 1500);
        kit.description(
                "&7You are able to jump really;" +
                        "&7far to catch those hot hot;" +
                        "&7potatoes!,"
        );
        return kit.create();
    }

}
