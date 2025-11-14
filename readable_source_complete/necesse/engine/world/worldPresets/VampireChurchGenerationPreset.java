/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.worldPresets;

import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.biomeGenerator.BiomeGeneratorStack;
import necesse.engine.world.worldPresets.LevelPresetsRegion;
import necesse.engine.world.worldPresets.SimpleGenerationPreset;
import necesse.engine.world.worldPresets.WorldApplyAreaPredicate;
import necesse.engine.world.worldPresets.WorldPreset;
import necesse.engine.world.worldPresets.WorldPresetTester;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.VampireChurchPreset;
import necesse.level.maps.presets.set.CarpetSet;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.PresetSet;
import necesse.level.maps.presets.set.WallSet;

public class VampireChurchGenerationPreset
extends SimpleGenerationPreset {
    public final Biome biome;
    public final LevelIdentifier levelIdentifier;
    public final WallSet[] walls;
    public final FurnitureSet[] furniture;
    public final CarpetSet[] carpetRing;
    public final CarpetSet[] prayerCarpet;

    public VampireChurchGenerationPreset(LevelIdentifier levelIdentifier, Biome biome) {
        super(20, true, true, false, false, biome);
        this.biome = biome;
        this.levelIdentifier = levelIdentifier;
        this.walls = (WallSet[])WallSet.getReducedSetForBiome((PresetSet[])new WallSet[]{WallSet.basalt, WallSet.stone, WallSet.deepStone, WallSet.swampStone, WallSet.deepSnowStone, WallSet.deepSwampStone, WallSet.dungeon}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new WallSet[0]);
        this.furniture = (FurnitureSet[])FurnitureSet.getReducedSetForBiome((PresetSet[])new FurnitureSet[]{FurnitureSet.willow, FurnitureSet.oak, FurnitureSet.deadwood, FurnitureSet.dryad, FurnitureSet.maple, FurnitureSet.bone, FurnitureSet.dungeon}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new FurnitureSet[0]);
        this.carpetRing = (CarpetSet[])CarpetSet.getReducedSetForBiome((PresetSet[])new CarpetSet[]{CarpetSet.green, CarpetSet.brownbear, CarpetSet.blue}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new CarpetSet[0]);
        this.prayerCarpet = (CarpetSet[])CarpetSet.getReducedSetForBiome((PresetSet[])new CarpetSet[]{CarpetSet.green, CarpetSet.blue, CarpetSet.steelgrey, CarpetSet.goldgrid}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new CarpetSet[0]);
    }

    @Override
    public void setupTester(WorldPresetTester tester) {
        tester.addApplyPredicate(new WorldApplyAreaPredicate(0, 0, tester.width - 1, tester.height - 1, 0, new WorldApplyAreaPredicate.WorldApplyCornerTest(){

            @Override
            public boolean isValidTile(WorldPreset preset, LevelPresetsRegion presetsRegion, BiomeGeneratorStack generatorStack, int tileX, int tileY) {
                if (presetsRegion.identifier.equals(LevelIdentifier.SURFACE_IDENTIFIER)) {
                    return !generatorStack.isSurfaceOceanOrRiver(tileX, tileY);
                }
                if (presetsRegion.identifier.equals(LevelIdentifier.CAVE_IDENTIFIER)) {
                    return !generatorStack.isCaveRiverOrLava(tileX, tileY);
                }
                if (presetsRegion.identifier.equals(LevelIdentifier.DEEP_CAVE_IDENTIFIER)) {
                    return !generatorStack.isDeepCaveLava(tileX, tileY);
                }
                return false;
            }
        }));
    }

    @Override
    public Preset getPreset(GameRandom random) {
        return new VampireChurchPreset(this.biome, this.levelIdentifier, random, random.getOneOf(this.walls), random.getOneOf(this.furniture), random.getOneOf(this.carpetRing), random.getOneOf(this.prayerCarpet));
    }
}

