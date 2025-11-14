/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.level.maps.biomes.Biome
 *  necesse.level.maps.biomes.dungeon.DungeonBiome
 *  necesse.level.maps.biomes.swamp.SwampBiome
 */
package aphorea.registry;

import necesse.level.maps.biomes.Biome;
import necesse.level.maps.biomes.dungeon.DungeonBiome;
import necesse.level.maps.biomes.swamp.SwampBiome;

public class AphSpawnTables {
    public static void modifySpawnTables() {
        Biome.defaultSurfaceMobs.addLimited(60, "gelslime", 2, 1024).addLimited(4, "wildphosphorslime", 1, 512, mob -> mob.isHostile);
        Biome.defaultCaveMobs.add(6, "rockygelslime");
        SwampBiome.surfaceMobs.addLimited(1, "pinkwitch", 1, 9600);
        DungeonBiome.defaultDungeonMobs.add(5, "voidadept");
    }
}

