package me.block2block.hotpotato.entities;

import org.bukkit.inventory.ItemStack;

public class Kit {

    private int id;
    private int price;
    private String lore;
    private String name;
    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack[] hotbar = new ItemStack[8];
    private ItemStack[] inventory = new ItemStack[26];

    public Kit(int id, String name, String lore, int price, ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots, ItemStack[] hotbar, ItemStack[] inventory) {
        this.id = id;
        this.name = name;
        this.lore = lore;
        this.price = price;
        this.helmet = helmet;
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.boots = boots;
        this.hotbar = hotbar;
        this.inventory = inventory;
    }

    public String lore() { return lore; }

    public String name() { return name; }

    public int price() { return price; }

    public ItemStack helmet() { return helmet; }

    public ItemStack chestplate() { return chestplate; }

    public ItemStack leggings() { return leggings; }

    public ItemStack boots() { return boots; }

    public ItemStack[] i(){ return inventory; }

    public ItemStack[] hb() { return hotbar; }
}
