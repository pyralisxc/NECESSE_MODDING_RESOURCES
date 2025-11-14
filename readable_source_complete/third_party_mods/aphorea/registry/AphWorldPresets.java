/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.registries.BiomeRegistry
 *  necesse.engine.registries.WorldPresetRegistry
 *  necesse.engine.util.LevelIdentifier
 *  necesse.engine.world.worldPresets.WorldPreset
 */
package aphorea.registry;

import aphorea.presets.worldpresets.InfectedForestCaveWorldPreset;
import aphorea.presets.worldpresets.InfectedLootLakeWorldPreset;
import aphorea.presets.worldpresets.RuneInventorWorldPreset;
import aphorea.presets.worldpresets.SpinelCavesWorldPreset;
import aphorea.presets.worldpresets.SpinelFakeChestWorldPreset;
import aphorea.registry.AphBiomes;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.WorldPresetRegistry;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.worldPresets.WorldPreset;

public class AphWorldPresets {
    public static void registerCore() {
        WorldPresetRegistry.registerPreset((String)"runeworkshopforest", (WorldPreset)new RuneInventorWorldPreset(0.001f, BiomeRegistry.FOREST));
        WorldPresetRegistry.registerPreset((String)"runeworkshopplains", (WorldPreset)new RuneInventorWorldPreset(0.001f, BiomeRegistry.PLAINS));
        WorldPresetRegistry.registerPreset((String)"runeworkshopsnow", (WorldPreset)new RuneInventorWorldPreset(0.001f, BiomeRegistry.SNOW));
        WorldPresetRegistry.registerPreset((String)"runeworkshopinfectedfields", (WorldPreset)new RuneInventorWorldPreset(0.001f, AphBiomes.INFECTED_FIELDS));
        WorldPresetRegistry.registerPreset((String)"spinelcaves", (WorldPreset)new SpinelCavesWorldPreset());
        WorldPresetRegistry.registerPreset((String)"lootlake", (WorldPreset)new InfectedLootLakeWorldPreset(0.004f, AphBiomes.INFECTED_FIELDS));
        WorldPresetRegistry.registerPreset((String)"spinelfakechest", (WorldPreset)new SpinelFakeChestWorldPreset(AphBiomes.INFECTED_FIELDS, LevelIdentifier.CAVE_IDENTIFIER, 0.02f));
        WorldPresetRegistry.registerPreset((String)"infectedforestcave", (WorldPreset)new InfectedForestCaveWorldPreset(AphBiomes.INFECTED_FIELDS, LevelIdentifier.CAVE_IDENTIFIER, 0.008f));
    }
}

