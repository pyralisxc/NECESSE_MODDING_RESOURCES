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
import necesse.level.maps.presets.MystrilHomesteadPreset;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.set.BushSet;
import necesse.level.maps.presets.set.CarpetSet;
import necesse.level.maps.presets.set.CropSet;
import necesse.level.maps.presets.set.FenceSet;
import necesse.level.maps.presets.set.FloorSet;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.PresetSet;
import necesse.level.maps.presets.set.WallSet;

public class MystrilHomesteadGenerationPreset
extends SimpleGenerationPreset {
    public final WallSet[] innerWalls;
    public final WallSet[] outerWalls;
    public final FloorSet[] bedRoomsFloors;
    public final FloorSet[] bathroomsFloors;
    public final FloorSet[] mainFloors;
    public final FloorSet[] entranceFloors;
    public final CarpetSet[] entranceCarpets;
    public final CarpetSet[] doubleBedCarpets;
    public final CarpetSet[] bedTopCarpets;
    public final CarpetSet[] bedRightCarpets;
    public final CarpetSet[] bathroomMats;
    public final CarpetSet[] kitchenCarpets;
    public final FurnitureSet[] doubleBedFurniture;
    public final FurnitureSet[] topBedFurniture;
    public final FurnitureSet[] rightBedFurniture;
    public final FurnitureSet[] bathroomFurniture;
    public final FurnitureSet[] mainLights;
    public final FurnitureSet[] kitchenTables;
    public final BushSet[] bushes;
    public final CropSet[] rightFarms;
    public final CropSet[] leftFarms;
    public final FenceSet[] fences;

    public MystrilHomesteadGenerationPreset(LevelIdentifier identifier, Biome biome) {
        super(20, true, true, false, false, biome);
        this.innerWalls = (WallSet[])WallSet.getReducedSetForBiome((PresetSet[])new WallSet[]{WallSet.pine, WallSet.palm, WallSet.wood}, (Biome)biome, (LevelIdentifier)identifier, (PresetSet[])new WallSet[0]);
        this.outerWalls = (WallSet[])WallSet.getReducedSetForBiome((PresetSet[])new WallSet[]{WallSet.pine, WallSet.granite, WallSet.swampStone, WallSet.snowStone, WallSet.wood, WallSet.brick, WallSet.sandstone, WallSet.stone}, (Biome)biome, (LevelIdentifier)identifier, (PresetSet[])new WallSet[0]);
        this.bedRoomsFloors = (FloorSet[])FloorSet.getReducedSetForBiome((PresetSet[])new FloorSet[]{FloorSet.pine, FloorSet.palm, FloorSet.dryad, FloorSet.deadWood, FloorSet.wood}, (Biome)biome, (LevelIdentifier)identifier, (PresetSet[])new FloorSet[0]);
        this.bathroomsFloors = (FloorSet[])FloorSet.getReducedSetForBiome((PresetSet[])new FloorSet[]{FloorSet.deepSnowStoneBrick, FloorSet.sandstoneBrick, FloorSet.deepStoneBrick, FloorSet.graniteBrick, FloorSet.granite, FloorSet.deepSwampStone, FloorSet.snowStone, FloorSet.snowStoneBrick, FloorSet.stoneTiled, FloorSet.dungeon, FloorSet.swampStoneBrick}, (Biome)biome, (LevelIdentifier)identifier, (PresetSet[])new FloorSet[0]);
        this.mainFloors = (FloorSet[])FloorSet.getReducedSetForBiome((PresetSet[])new FloorSet[]{FloorSet.sandstoneBrick, FloorSet.deepStoneBrick, FloorSet.pine, FloorSet.palm, FloorSet.granite, FloorSet.deepStoneTiled, FloorSet.deepSnowStone, FloorSet.dryad, FloorSet.deepSwampStone, FloorSet.snowStone, FloorSet.deadWood, FloorSet.dungeon}, (Biome)biome, (LevelIdentifier)identifier, (PresetSet[])new FloorSet[0]);
        this.entranceFloors = (FloorSet[])FloorSet.getReducedSetForBiome((PresetSet[])new FloorSet[]{FloorSet.deepStoneBrick, FloorSet.graniteBrick, FloorSet.deepStone, FloorSet.deepSwampStoneBrick, FloorSet.stoneTiled, FloorSet.sandstone, FloorSet.stone, FloorSet.swampStoneBrick}, (Biome)biome, (LevelIdentifier)identifier, (PresetSet[])new FloorSet[0]);
        this.bathroomFurniture = (FurnitureSet[])PresetSet.getReducedSetForBiome((PresetSet[])new FurnitureSet[]{FurnitureSet.pine, FurnitureSet.birch, FurnitureSet.palm, FurnitureSet.maple, FurnitureSet.oak, FurnitureSet.dungeon, FurnitureSet.spruce}, (Biome)biome, (LevelIdentifier)identifier, (PresetSet[])new FurnitureSet[0]);
        this.rightBedFurniture = this.bathroomFurniture;
        this.topBedFurniture = this.bathroomFurniture;
        this.kitchenTables = this.bathroomFurniture;
        this.doubleBedFurniture = this.bathroomFurniture;
        this.bushes = (BushSet[])PresetSet.getReducedSetForBiome((PresetSet[])new BushSet[]{BushSet.raspberry, BushSet.blueberry, BushSet.blackberry}, (Biome)biome, (LevelIdentifier)identifier, (PresetSet[])new BushSet[0]);
        this.leftFarms = (CropSet[])PresetSet.getReducedSetForBiome((PresetSet[])new CropSet[]{CropSet.cabbage, CropSet.corn, CropSet.potato, CropSet.onion, CropSet.strawberry, CropSet.wheat, CropSet.tomato, CropSet.chilipepper, CropSet.beet, CropSet.pumpkin, CropSet.eggplant, CropSet.carrot}, (Biome)biome, (LevelIdentifier)identifier, (PresetSet[])new CropSet[0]);
        this.rightFarms = this.leftFarms;
        this.mainLights = new FurnitureSet[]{FurnitureSet.willow, FurnitureSet.pine, FurnitureSet.birch, FurnitureSet.palm, FurnitureSet.oak, FurnitureSet.maple, FurnitureSet.deadwood, FurnitureSet.spruce};
        this.entranceCarpets = new CarpetSet[]{CarpetSet.blue, CarpetSet.purple};
        this.doubleBedCarpets = new CarpetSet[]{CarpetSet.velour, CarpetSet.wool, CarpetSet.brownbear, CarpetSet.green, CarpetSet.blue, CarpetSet.purple, CarpetSet.redyarn, CarpetSet.steelgrey, CarpetSet.heart, CarpetSet.goldgrid};
        this.bedTopCarpets = new CarpetSet[]{CarpetSet.velour, CarpetSet.wool, CarpetSet.brownbear, CarpetSet.green, CarpetSet.blue, CarpetSet.purple, CarpetSet.steelgrey, CarpetSet.heart, CarpetSet.goldgrid};
        this.bedRightCarpets = this.bedTopCarpets;
        this.kitchenCarpets = new CarpetSet[]{CarpetSet.velour, CarpetSet.brownbear, CarpetSet.green, CarpetSet.purple, CarpetSet.steelgrey, CarpetSet.leather};
        this.bathroomMats = new CarpetSet[]{CarpetSet.velour, CarpetSet.brownbear, CarpetSet.green, CarpetSet.purple, CarpetSet.steelgrey, CarpetSet.leather};
        this.fences = new FenceSet[]{FenceSet.iron, FenceSet.wood, FenceSet.stone};
    }

    @Override
    public void setupTester(WorldPresetTester tester) {
        tester.addApplyPredicate(new WorldApplyAreaPredicate(-5, -5, tester.width + 5, tester.height + 5, 0, new WorldApplyAreaPredicate.WorldApplyGridTest(5){

            @Override
            public boolean isValidTile(WorldPreset preset, LevelPresetsRegion presetsRegion, BiomeGeneratorStack generatorStack, int tileX, int tileY) {
                if (presetsRegion.identifier.equals(LevelIdentifier.SURFACE_IDENTIFIER)) {
                    return !generatorStack.isSurfaceOceanOrRiverOrBeach(tileX, tileY);
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
        return new MystrilHomesteadPreset(random, random.getOneOf(this.innerWalls), random.getOneOf(this.outerWalls), random.getOneOf(this.bedRoomsFloors), random.getOneOf(this.bathroomsFloors), random.getOneOf(this.mainFloors), random.getOneOf(this.entranceFloors), random.getOneOf(this.entranceCarpets), random.getOneOf(this.doubleBedCarpets), random.getOneOf(this.bedTopCarpets), random.getOneOf(this.bedRightCarpets), random.getOneOf(this.bathroomMats), random.getOneOf(this.kitchenCarpets), random.getOneOf(this.doubleBedFurniture), random.getOneOf(this.topBedFurniture), random.getOneOf(this.rightBedFurniture), random.getOneOf(this.bathroomFurniture), random.getOneOf(this.mainLights), random.getOneOf(this.kitchenTables), random.getOneOf(this.bushes), random.getOneOf(this.rightFarms), random.getOneOf(this.leftFarms), random.getOneOf(this.fences));
    }
}

