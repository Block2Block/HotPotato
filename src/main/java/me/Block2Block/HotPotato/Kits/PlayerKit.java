package me.Block2Block.HotPotato.Kits;

import me.Block2Block.HotPotato.Entities.Kit;
import me.Block2Block.HotPotato.Managers.Utilities.ItemUtility;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

import static org.bukkit.Material.*;

public enum PlayerKit {

    DEFAULT("That's hot!", KitLoader.get().Default(), 20, BAKED_POTATO /*UtilItem.ci(
            ENCHANTED_BOOK, "&e&lWarrior's Book", 1, "&7This ancient book is essentially,&7an upgrade to your current kit.,&7Right " +
                    "click to apply!"),*/
    );

    private String name;
    private Kit kit;
    private int GUISlot;
    private Material icon;

    PlayerKit(String name, Kit kit, int GUISlot, Material icon) {
        this.name = name;
        this.kit = kit;
        this.GUISlot = GUISlot;
        this.icon = icon;
    }

    public String getName() { return name; }

    public Kit getKit() { return kit; }

    public int getGUISlot() { return GUISlot; }

    public Material getIcon() { return icon; }



}
