package me.Block2Block.HotPotato.Entities;

import org.bukkit.enchantments.Enchantment;

public class Enchant {

    private Enchantment enchantment;
    private int level = 1;

    public Enchant(Enchantment enchantment, int level) {
        this.enchantment = enchantment;
        this.level = level;
    }

    public Enchantment e() {
        return enchantment;
    }

    public int l() {
        return level;
    }

}
