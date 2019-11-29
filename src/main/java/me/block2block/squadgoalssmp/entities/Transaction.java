package me.block2block.squadgoalssmp.entities;

import org.bukkit.entity.Player;

public class Transaction {

    private int type;
    private EconomyItem item;
    private Player player;

    public Transaction(int type, EconomyItem item, Player player) {
        this.type = type;
        this.item = item;
        this.player = player;
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
}
