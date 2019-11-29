package me.block2block.squadgoalssmp.entities;

import me.block2block.squadgoalssmp.Main;
import org.bukkit.Material;

public class EconomyItem {

    private Material material;
    private String materialName;
    private int buyPrice;
    private int sellPrice;

    public EconomyItem(Material material) {
        materialName = Main.getInstance().getConfig().getString(material.name() + ".FriendlyName");
        buyPrice = Main.getInstance().getConfig().getInt(material.name() + ".BuyPrice");
        sellPrice = Main.getInstance().getConfig().getInt(material.name() + ".SellPrice");
        this.material = material;
    }

    public Material getMaterial() {
        return material;
    }

    public int getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(int buyPrice) {
        this.buyPrice = buyPrice;
    }

    public int getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(int sellPrice) {
        this.sellPrice = sellPrice;
    }

    public void update() {
        materialName = Main.getInstance().getConfig().getString(material.name() + ".FriendlyName");
        buyPrice = Main.getInstance().getConfig().getInt(material.name() + ".BuyPrice");
        sellPrice = Main.getInstance().getConfig().getInt(material.name() + ".SellPrice");
    }

    public String getMaterialName() {
        return materialName;
    }
}
