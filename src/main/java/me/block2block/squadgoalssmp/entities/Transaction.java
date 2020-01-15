package me.block2block.squadgoalssmp.entities;

import org.bukkit.entity.Player;

public class Transaction {

    private int type;
    private EconomyItem item;
    private Player player;
    private int stage;

    public Transaction(int type, EconomyItem item, Player player) {
        this.type = type;
        this.item = item;
        this.player = player;
        stage = 1;
    }

    public EconomyItem getItem() {
        return item;
    }

    public int getType() {
        return type;
    }

    public Player getPlayer() {
        return player;
    }

    public int getStage() {
        return stage;
    }

    public void nextStage() {
        stage++;
    }

    public void setItem(EconomyItem item) {
        this.item = item;
    }
}
