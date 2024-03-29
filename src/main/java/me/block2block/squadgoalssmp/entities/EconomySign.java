package me.block2block.squadgoalssmp.entities;

import me.block2block.squadgoalssmp.Main;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

public class EconomySign {

    private Location location;
    private EconomyItem item;
    private int type;

    public EconomySign(Location l, EconomyItem item, int type) {
        location = l;
        this.item = item;
        this.type = type;
        update();
    }

    public int getType() {
        return type;
    }

    public Location getLocation() {
        return location;
    }

    public Material getMaterial() {
        return item.getMaterial();
    }

    public EconomyItem getItem() {
        return item;
    }

    public void update() {
        Block block = location.getBlock();
        Sign sign = (Sign) block.getState();

        switch (type) {
            case 1:
                sign.setLine(0, Main.c(null, "&5&l&nBUY"));
                sign.setLine(1, Main.c(null, "Click here to"));
                sign.setLine(2, Main.c(null, "buy items!"));
                break;
            case 2:
                sign.setLine(0, Main.c(null, "&5&l&nSELL"));
                sign.setLine(1, Main.c(null, "Click here to"));
                sign.setLine(2, Main.c(null, "sell items!"));
                break;
        }

        sign.update(true);
    }
}
