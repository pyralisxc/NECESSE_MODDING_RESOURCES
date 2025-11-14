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
import necesse.level.maps.presets.BigCemeteryPreset;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.set.FenceSet;
import necesse.level.maps.presets.set.FloorSet;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.GroundSet;
import necesse.level.maps.presets.set.HedgeSet;
import necesse.level.maps.presets.set.PresetSet;
import necesse.level.maps.presets.set.TreeSet;
import necesse.level.maps.presets.set.WallSet;

public class BigCemeteryGenerationPreset
extends SimpleGenerationPreset {
    public final Biome biome;
    public final LevelIdentifier levelIdentifier;
    public final FloorSet[] path;
    public final GroundSet[] grass;
    public final FenceSet[] fence;
    public final WallSet[] walls;
    public final TreeSet[] trees;
    public final HedgeSet[] hedges;
    public final FurnitureSet[] furniture;

    public BigCemeteryGenerationPreset(LevelIdentifier levelIdentifier, Biome biome) {
        super(20, true, true, false, false, biome);
        this.biome = biome;
        this.levelIdentifier = levelIdentifier;
        this.path = (FloorSet[])GroundSet.getReducedSetForBiome((PresetSet[])new FloorSet[]{FloorSet.sandstone, FloorSet.graniteBrick, FloorSet.deepStoneBrick, FloorSet.deepSnowStone, FloorSet.deepSwampStoneBrick, FloorSet.deadWood, FloorSet.stoneTiled, FloorSet.stoneBrick, FloorSet.snowStoneBrick}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new FloorSet[0]);
        this.grass = (GroundSet[])GroundSet.getReducedSetForBiome((PresetSet[])new GroundSet[]{GroundSet.forest, GroundSet.snow, GroundSet.plains, GroundSet.swamp, GroundSet.desert}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new GroundSet[0]);
        this.fence = (FenceSet[])FenceSet.getReducedSetForBiome((PresetSet[])new FenceSet[]{FenceSet.crypt, FenceSet.iron, FenceSet.wood, FenceSet.stone}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new FenceSet[0]);
        this.walls = (WallSet[])WallSet.getReducedSetForBiome((PresetSet[])new WallSet[]{WallSet.willow, WallSet.deepStone, WallSet.swampStone, WallSet.wood, WallSet.dungeon}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new WallSet[0]);
        this.trees = (TreeSet[])TreeSet.getReducedSetForBiome((PresetSet[])new TreeSet[]{TreeSet.willow, TreeSet.pine, TreeSet.birch, TreeSet.maple, TreeSet.deadwood, TreeSet.spruce}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new TreeSet[0]);
        this.hedges = (HedgeSet[])HedgeSet.getReducedSetForBiome((PresetSet[])new HedgeSet[]{HedgeSet.forest, HedgeSet.snow, HedgeSet.plains, HedgeSet.swamp}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new HedgeSet[0]);
        this.furniture = (FurnitureSet[])FurnitureSet.getReducedSetForBiome((PresetSet[])new FurnitureSet[]{FurnitureSet.bamboo, FurnitureSet.deadwood, FurnitureSet.dryad, FurnitureSet.oak, FurnitureSet.maple}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new FurnitureSet[0]);
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
        return new BigCemeteryPreset(this.biome, this.levelIdentifier, random, random.getOneOf(this.path), random.getOneOf(this.grass), random.getOneOf(this.fence), random.getOneOf(this.walls), random.getOneOf(this.trees), random.getOneOf(this.hedges), random.getOneOf(this.furniture));
    }
}

