package me.block2block.squadgoalssmp;

import me.block2block.squadgoalssmp.entities.Purge;
import me.block2block.squadgoalssmp.entities.PurgeMode;

public class CacheManager {

    private static Purge purge;

    public static boolean isPurge() {
        return (purge != null);
    }

    public static Purge getPurge() {
        return purge;
    }

}
