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
import necesse.level.maps.presets.ChapelPreset;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.set.FloorSet;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.PresetSet;
import necesse.level.maps.presets.set.SmallPaintingSet;
import necesse.level.maps.presets.set.TreeSet;
import necesse.level.maps.presets.set.WallSet;

public class ChapelGenerationPreset
extends SimpleGenerationPreset {
    public final TreeSet[] treess;
    public final WallSet[] wallss;
    public final FloorSet[] mainFloors;
    public final FurnitureSet[] furnitures;
    public final FloorSet[] kitchenFloors;
    public final SmallPaintingSet[] paintings;

    public ChapelGenerationPreset(LevelIdentifier levelIdentifier, Biome biome) {
        super(20, true, true, false, false, biome);
        this.treess = (TreeSet[])TreeSet.getReducedSetForBiome((PresetSet[])new TreeSet[]{TreeSet.willow, TreeSet.pine, TreeSet.palm, TreeSet.birch, TreeSet.oak, TreeSet.maple, TreeSet.cactus, TreeSet.deadwood, TreeSet.spruce}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new TreeSet[0]);
        this.wallss = (WallSet[])WallSet.getReducedSetForBiome((PresetSet[])new WallSet[]{WallSet.granite, WallSet.deepStone, WallSet.dryad, WallSet.swampStone, WallSet.deepSwampStone, WallSet.deepSandstone, WallSet.brick, WallSet.sandstone, WallSet.dungeon, WallSet.stone}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new WallSet[0]);
        this.mainFloors = (FloorSet[])FloorSet.getReducedSetForBiome((PresetSet[])new FloorSet[]{FloorSet.deepStoneBrick, FloorSet.pine, FloorSet.palm, FloorSet.deepSwampStoneBrick, FloorSet.spiderCastle, FloorSet.deadWood, FloorSet.dungeon, FloorSet.swampStoneBrick, FloorSet.sandstoneBrick, FloorSet.basalt, FloorSet.graniteBrick, FloorSet.granite, FloorSet.deepStone, FloorSet.dryad, FloorSet.wood, FloorSet.stoneBrick}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new FloorSet[0]);
        this.furnitures = (FurnitureSet[])FurnitureSet.getReducedSetForBiome((PresetSet[])new FurnitureSet[]{FurnitureSet.birch, FurnitureSet.maple, FurnitureSet.deadwood, FurnitureSet.dryad, FurnitureSet.oak, FurnitureSet.dungeon}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new FurnitureSet[0]);
        this.kitchenFloors = (FloorSet[])FloorSet.getReducedSetForBiome((PresetSet[])new FloorSet[]{FloorSet.deepSnowStoneBrick, FloorSet.deepStoneBrick, FloorSet.deepStone, FloorSet.deepStoneTiled}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new FloorSet[0]);
        this.paintings = (SmallPaintingSet[])SmallPaintingSet.getReducedSetForBiome((PresetSet[])new SmallPaintingSet[]{SmallPaintingSet.uncommonHeart, SmallPaintingSet.uncommonDagger, SmallPaintingSet.uncommonParrot, SmallPaintingSet.uncommonCastle}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new SmallPaintingSet[0]);
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
        return new ChapelPreset(random, random.getOneOf(this.treess), random.getOneOf(this.wallss), random.getOneOf(this.mainFloors), random.getOneOf(this.furnitures), random.getOneOf(this.kitchenFloors), random.getOneOf(this.paintings));
    }
}

