package me.block2block.hotpotato.managers;

import me.block2block.hotpotato.entities.Enchant;
import me.block2block.hotpotato.entities.Kit;
import me.block2block.hotpotato.managers.utils.ItemUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class KitCreator {

    private int id;
    private String lore;
    private int price;
    private String name;
    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack[] hotbar = new ItemStack[9];
    private ItemStack[] inventory = new ItemStack[27];

    public KitCreator(int id, String name, int price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public Kit create() {
        return new Kit(id, name, lore, price, helmet, chestplate, leggings, boots, hotbar, inventory);
    }

    public String name() {
        return name;
    }

    public KitCreator description(String lore) {
        this.lore = lore;
        return this;
    }

    public KitCreator potion(boolean hotbar, int slot, String name, String lore, int amount, PotionEffect... effects) {
        ItemStack p = ItemUtil.potion(name, amount, lore, effects);
        if (hotbar) {
            this.hotbar[slot-1] = p;
        } else {
            this.inventory[slot-1] = p;
        }
        return this;
    }


    public KitCreator helmet(Material m, String name, String lore, Enchant... enchants) {
        this.helmet = ItemUtil.ci(m, name, 1, lore, (short)0, enchants);
        return this;
    }
    public KitCreator helmet(Material m, String name, Enchant... enchants) {
        this.helmet = ItemUtil.ci(m, name, 1, null, (short)0, enchants);
        return this;
    }
    public KitCreator chestplate(Material m, String name, String lore, Enchant... enchants) {
        this.chestplate = ItemUtil.ci(m, name, 1, lore, (short)0, enchants);
        return this;
    }
    public KitCreator chestplate(Material m, String name, Enchant... enchants) {
        this.chestplate = ItemUtil.ci(m, name, 1, null, (short)0, enchants);
        return this;
    }
    public KitCreator leggings(Material m, String name, String lore, Enchant... enchants) {
        this.leggings = ItemUtil.ci(m, name, 1, lore, (short)0, enchants);
        return this;
    }
    public KitCreator leggings(Material m, String name, Enchant... enchants) {
        this.leggings = ItemUtil.ci(m, name, 1, null, (short)0, enchants);
        return this;
    }
    public KitCreator boots(Material m, String name, String lore, Enchant... enchants) {
        this.boots = ItemUtil.ci(m, name, 1, lore, (short)0, enchants);
        return this;
    }
    public KitCreator boots(Material m, String name, Enchant... enchants) {
        this.boots = ItemUtil.ci(m, name, 1, null, (short)0, enchants);
        return this;
    }
    public KitCreator hb(int slot, Material m, int amount, String name, String lore, short data, Enchant... enchants) {
        this.hotbar[slot-1] = ItemUtil.ci(m, name, amount, lore, data, enchants);
        return this;
    }
    public KitCreator hb(int slot, Material m, int amount, String name, String lore, Enchant... enchants) {
        return hb(slot, m, amount, name, lore, (short)0, enchants);
    }
    public KitCreator hb(int slot, Material m, int amount, String name, Enchant... enchants) {
        return hb(slot, m, amount, name, null, enchants);
    }
    public KitCreator hb(int slot, Material m, int amount, Enchant... enchants) {
        return hb(slot, m, amount, "", enchants);
    }
    public KitCreator hb(int slot, Material m, Enchant... enchants) {
        return hb(slot, m, 1, enchants);
    }
    public KitCreator i(int slot, Material m, int amount, String name, String lore, short data, Enchant... enchants) {
        this.inventory[slot-1] = ItemUtil.ci(m, name, amount, lore, data, enchants);
        return this;
    }
    public KitCreator i(int slot, Material m, int amount, String name, String lore, Enchant... enchants) {
        return i(slot, m, amount, name, lore, (short)0, enchants);
    }
    public KitCreator i(int slot, Material m, int amount, String name, Enchant... enchants) {
        return i(slot, m, amount, name, null, enchants);
    }
    public KitCreator i(int slot, Material m, int amount, Enchant... enchants) {
        return i(slot, m, amount, "", enchants);
    }
    public KitCreator i(int slot, Material m, Enchant... enchants) {
        return i(slot, m, 1, enchants);
    }

}
