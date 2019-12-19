package me.block2block.squadgoalssmp.entities.mobs;

import me.block2block.squadgoalssmp.Main;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.entity.Zombie;

public class BloodMoonZombie extends EntityZombie {

    public BloodMoonZombie(World world) {


        super(world);

        Zombie craftZombie = (Zombie) this.getBukkitEntity();
        NBTTagCompound compound = new NBTTagCompound();
        this.c(compound);
        NBTTagList attributeList;
        NBTTagCompound attribute = new NBTTagCompound();
        attribute.set("Name",NBTTagString.a("generic.followRange"));
        attribute.set("Base",NBTTagDouble.a(50.0d));
        attributeList = new NBTTagList();
        attributeList.add(attribute);

        compound.set("Attributes",attributeList);

        this.a(compound);

        this.setCustomName(IChatBaseComponent.ChatSerializer.a(Main.c(null, "&4&lBlood Zombie")));
        this.setHealth(100);
        this.setCustomNameVisible(true);
        this.targetSelector.a(0, new PathfinderGoalNearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, true));
        this.getWorld().addEntity(this);
    }
}
