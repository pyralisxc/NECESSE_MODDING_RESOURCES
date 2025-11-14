/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.registries.BiomeRegistry
 *  necesse.level.maps.biomes.Biome
 */
package aphorea.registry;

import aphorea.biomes.InfectedFieldsBiome;
import necesse.engine.registries.BiomeRegistry;
import necesse.level.maps.biomes.Biome;

public class AphBiomes {
    public static Biome INFECTED_FIELDS;

    public static void registerCore() {
        INFECTED_FIELDS = new InfectedFieldsBiome().setGenerationWeight(0.75f);
        BiomeRegistry.registerBiome((String)"infectedfields", (Biome)INFECTED_FIELDS, (boolean)true);
    }
}

