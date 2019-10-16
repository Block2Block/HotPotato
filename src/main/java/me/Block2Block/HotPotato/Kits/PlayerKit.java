package me.Block2Block.HotPotato.Kits;

import me.Block2Block.HotPotato.Entities.Kit;
import org.bukkit.Material;

import static org.bukkit.Material.BAKED_POTATO;
import static org.bukkit.Material.STICK;

public enum PlayerKit {

    DEFAULT(0,"That's hot!", KitLoader.get().Default(), 20, BAKED_POTATO),
    POTATOWHACKER(1,"Potato Whacker", KitLoader.get().PotatoWhacker(), 21, STICK);

    private int id;
    private String name;
    private Kit kit;
    private int GUISlot;
    private Material icon;

    PlayerKit(int id, String name, Kit kit, int GUISlot, Material icon) {
        this.id = id;
        this.name = name;
        this.kit = kit;
        this.GUISlot = GUISlot;
        this.icon = icon;
    }

    public String getName() { return name; }

    public Kit getKit() { return kit; }

    public int getGUISlot() { return GUISlot; }

    public Material getIcon() { return icon; }

    public int getId() {
        return id;
    }
}
