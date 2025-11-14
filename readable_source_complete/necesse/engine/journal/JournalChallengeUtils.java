/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.journal;

import necesse.engine.registries.BiomeRegistry;
import necesse.level.maps.biomes.Biome;

public class JournalChallengeUtils {
    public static boolean isForestBiome(Biome biome) {
        return biome == BiomeRegistry.FOREST || biome == BiomeRegistry.DUNGEON;
    }

    public static boolean isPlainsBiome(Biome biome) {
        return biome == BiomeRegistry.PLAINS;
    }

    public static boolean isSnowBiome(Biome biome) {
        return biome == BiomeRegistry.SNOW;
    }

    public static boolean isSwampBiome(Biome biome) {
        return biome == BiomeRegistry.SWAMP;
    }

    public static boolean isDesertBiome(Biome biome) {
        return biome == BiomeRegistry.DESERT;
    }

    public static boolean isDesertOrTempleBiome(Biome biome) {
        return JournalChallengeUtils.isDesertBiome(biome) || biome == BiomeRegistry.TEMPLE;
    }
}

