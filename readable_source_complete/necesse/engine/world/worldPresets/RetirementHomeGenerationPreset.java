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
import necesse.level.maps.presets.RetirementHomePreset;
import necesse.level.maps.presets.set.CarpetSet;
import necesse.level.maps.presets.set.FloorSet;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.LargelPaintingSet;
import necesse.level.maps.presets.set.PresetSet;
import necesse.level.maps.presets.set.SmallPaintingSet;
import necesse.level.maps.presets.set.WallSet;

public class RetirementHomeGenerationPreset
extends SimpleGenerationPreset {
    public final WallSet[] walls;
    public final WallSet[] doors;
    public final CarpetSet[] carpets;
    public final FloorSet[] mainFloors;
    public final FloorSet[] bathroomFloors;
    public final FurnitureSet[] furnitures;
    public final SmallPaintingSet[] kitchenPaintings;
    public final SmallPaintingSet[] entrancePaintings;
    public final SmallPaintingSet[] deskPaintings;
    public final SmallPaintingSet[] chestPaintings;
    public final LargelPaintingSet[] largePaintings;

    public RetirementHomeGenerationPreset(LevelIdentifier levelIdentifier, Biome biome) {
        super(20, true, false, false, false, biome);
        this.walls = (WallSet[])WallSet.getReducedSetForBiome((PresetSet[])new WallSet[]{WallSet.willow, WallSet.basalt, WallSet.palm, WallSet.crypt, WallSet.deepStone, WallSet.dryad, WallSet.swampStone, WallSet.wood, WallSet.deepSandstone, WallSet.spidercastle, WallSet.dungeon, WallSet.stone}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new WallSet[0]);
        this.doors = (WallSet[])WallSet.getReducedSetForBiome((PresetSet[])new WallSet[]{WallSet.willow, WallSet.basalt, WallSet.pine, WallSet.palm, WallSet.granite, WallSet.deepStone, WallSet.swampStone, WallSet.wood, WallSet.sandstone, WallSet.stone}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new WallSet[0]);
        this.carpets = (CarpetSet[])CarpetSet.getReducedSetForBiome((PresetSet[])new CarpetSet[]{CarpetSet.velour, CarpetSet.green, CarpetSet.blue, CarpetSet.purple}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new CarpetSet[0]);
        this.mainFloors = (FloorSet[])FloorSet.getReducedSetForBiome((PresetSet[])new FloorSet[]{FloorSet.deepStoneBrick, FloorSet.pine, FloorSet.palm, FloorSet.deepSwampStoneBrick, FloorSet.spiderCastle, FloorSet.snowStone, FloorSet.deadWood, FloorSet.dungeon, FloorSet.swampStoneBrick, FloorSet.basalt, FloorSet.granite, FloorSet.deepStone, FloorSet.dryad, FloorSet.wood}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new FloorSet[0]);
        this.bathroomFloors = (FloorSet[])FloorSet.getReducedSetForBiome((PresetSet[])new FloorSet[]{FloorSet.deepSnowStoneBrick, FloorSet.deepStoneBrick, FloorSet.pine, FloorSet.deepSnowStone, FloorSet.deepSwampStoneBrick, FloorSet.spiderCastle, FloorSet.snowStone, FloorSet.dungeon, FloorSet.swampStoneBrick, FloorSet.basalt, FloorSet.graniteBrick, FloorSet.granite, FloorSet.deepStone, FloorSet.dryad}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new FloorSet[0]);
        this.furnitures = (FurnitureSet[])FurnitureSet.getReducedSetForBiome((PresetSet[])new FurnitureSet[]{FurnitureSet.palm, FurnitureSet.deadwood, FurnitureSet.dryad, FurnitureSet.oak, FurnitureSet.maple, FurnitureSet.dungeon}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new FurnitureSet[0]);
        this.kitchenPaintings = (SmallPaintingSet[])SmallPaintingSet.getReducedSetForBiome((PresetSet[])new SmallPaintingSet[]{SmallPaintingSet.commonApple, SmallPaintingSet.commonAvocado, SmallPaintingSet.commonBanana}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new SmallPaintingSet[0]);
        this.entrancePaintings = (SmallPaintingSet[])SmallPaintingSet.getReducedSetForBiome((PresetSet[])new SmallPaintingSet[]{SmallPaintingSet.rareSandstonecaveling, SmallPaintingSet.rareStonecaveling, SmallPaintingSet.rareSnowcaveling, SmallPaintingSet.rareSwampcaveling, SmallPaintingSet.abandonedMineStonecaveling}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new SmallPaintingSet[0]);
        this.deskPaintings = (SmallPaintingSet[])SmallPaintingSet.getReducedSetForBiome((PresetSet[])new SmallPaintingSet[]{SmallPaintingSet.commonAbstract, SmallPaintingSet.commonRainsun}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new SmallPaintingSet[0]);
        this.chestPaintings = (SmallPaintingSet[])SmallPaintingSet.getReducedSetForBiome((PresetSet[])new SmallPaintingSet[]{SmallPaintingSet.uncommonHeart, SmallPaintingSet.uncommonDagger, SmallPaintingSet.uncommonParrot, SmallPaintingSet.uncommonDuck, SmallPaintingSet.uncommonEye, SmallPaintingSet.uncommonMouse, SmallPaintingSet.uncommonCastle}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new SmallPaintingSet[0]);
        this.largePaintings = (LargelPaintingSet[])LargelPaintingSet.getReducedSetForBiome((PresetSet[])new LargelPaintingSet[]{LargelPaintingSet.rareCastle, LargelPaintingSet.rareWorldMap, LargelPaintingSet.rareShip, LargelPaintingSet.rareAbstract}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new LargelPaintingSet[0]);
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
        return new RetirementHomePreset(random, random.getOneOf(this.walls), random.getOneOf(this.doors), random.getOneOf(this.carpets), random.getOneOf(this.mainFloors), random.getOneOf(this.bathroomFloors), random.getOneOf(this.furnitures), random.getOneOf(this.kitchenPaintings), random.getOneOf(this.entrancePaintings), random.getOneOf(this.deskPaintings), random.getOneOf(this.chestPaintings), random.getOneOf(this.largePaintings));
    }
}

