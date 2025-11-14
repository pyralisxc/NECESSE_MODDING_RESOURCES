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
import necesse.level.maps.presets.GuardTowerPreset;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.set.FloorSet;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.GroundSet;
import necesse.level.maps.presets.set.PathSet;
import necesse.level.maps.presets.set.PresetSet;
import necesse.level.maps.presets.set.WallSet;

public class GuardTowerGenerationPreset
extends SimpleGenerationPreset {
    public final Biome biome;
    public final LevelIdentifier levelIdentifier;
    public final WallSet[] outerWalls;
    public final WallSet[] interiorWalls;
    public final FurnitureSet[] furniture;
    public final FloorSet[] floorset;
    public final GroundSet[] gravel;
    public final PathSet[] path;

    public GuardTowerGenerationPreset(LevelIdentifier levelIdentifier, Biome biome) {
        super(20, true, true, false, false, biome);
        this.biome = biome;
        this.levelIdentifier = levelIdentifier;
        this.outerWalls = (WallSet[])WallSet.getReducedSetForBiome((PresetSet[])new WallSet[]{WallSet.snowStone, WallSet.sandstone}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new WallSet[0]);
        this.interiorWalls = (WallSet[])WallSet.getReducedSetForBiome((PresetSet[])new WallSet[]{WallSet.pine, WallSet.palm, WallSet.snowStone}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new WallSet[0]);
        this.furniture = (FurnitureSet[])FurnitureSet.getReducedSetForBiome((PresetSet[])new FurnitureSet[]{FurnitureSet.pine, FurnitureSet.palm, FurnitureSet.oak, FurnitureSet.spruce}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new FurnitureSet[0]);
        this.floorset = (FloorSet[])FloorSet.getReducedSetForBiome((PresetSet[])new FloorSet[]{FloorSet.sandstoneBrick, FloorSet.snowStoneBrick}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new FloorSet[0]);
        this.gravel = (GroundSet[])GroundSet.getReducedSetForBiome((PresetSet[])new GroundSet[]{GroundSet.snow, GroundSet.plains}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new GroundSet[0]);
        this.path = (PathSet[])PathSet.getReducedSetForBiome((PresetSet[])new PathSet[]{PathSet.sandStone, PathSet.snowStone}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new PathSet[0]);
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
        return new GuardTowerPreset(this.biome, this.levelIdentifier, random, random.getOneOf(this.outerWalls), random.getOneOf(this.interiorWalls), random.getOneOf(this.furniture), random.getOneOf(this.floorset), random.getOneOf(this.gravel), random.getOneOf(this.path));
    }
}

