package me.Block2Block.HotPotato.Entities;

public enum Kit {

    DEFAULT(0, "That's hot!",0, "Ouch! The potato is very hot, time for you to catch it!");

    private int id;
    private String name;
    private int price;
    private String description;

    Kit(int id, String name, int price, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public int getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }
}
