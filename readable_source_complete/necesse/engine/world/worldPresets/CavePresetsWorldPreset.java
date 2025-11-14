/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.worldPresets;

import necesse.engine.registries.BiomeRegistry;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.worldPresets.CapturedUndergroundFortressGenerationPreset;
import necesse.engine.world.worldPresets.CaveBoomPrankGenerationPreset;
import necesse.engine.world.worldPresets.CaveHoboHomeGenerationPreset;
import necesse.engine.world.worldPresets.CozyCaveHomeGenerationPreset;
import necesse.engine.world.worldPresets.GenerationPresetsWorldPreset;
import necesse.engine.world.worldPresets.LargeTempleRuinsGenerationPreset;
import necesse.engine.world.worldPresets.LevelPresetsRegion;
import necesse.engine.world.worldPresets.MinersOfficeGenerationPreset;
import necesse.engine.world.worldPresets.SlimeRanchGenerationPreset;
import necesse.engine.world.worldPresets.SmallCaveHomeRuinGenerationPreset;
import necesse.engine.world.worldPresets.SmallCaveLabyrinthGenerationPreset;
import necesse.engine.world.worldPresets.SunkenHomeGenerationPreset;
import necesse.engine.world.worldPresets.SurfaceBelowSurfaceGenerationPreset;
import necesse.engine.world.worldPresets.SwampSettlerRuinsGenerationPreset;
import necesse.engine.world.worldPresets.SwampyCaveLakeGenerationPreset;
import necesse.engine.world.worldPresets.VampireChurchGenerationPreset;
import necesse.level.maps.presets.set.BushSet;
import necesse.level.maps.presets.set.FenceSet;
import necesse.level.maps.presets.set.FloorSet;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.TreeSet;
import necesse.level.maps.presets.set.WallSet;

public class CavePresetsWorldPreset
extends GenerationPresetsWorldPreset {
    public CavePresetsWorldPreset() {
        super(0.05f);
    }

    @Override
    public void addCorePresets() {
        this.addForestPresets();
        this.addSnowPresets();
        this.addPlainsPresets();
        this.addSwampPresets();
        this.addDesertPresets();
    }

    @Override
    public boolean shouldAddToRegion(LevelPresetsRegion presetsRegion) {
        return presetsRegion.identifier.equals("cave");
    }

    protected void addForestPresets() {
        this.addPreset(85, new CapturedUndergroundFortressGenerationPreset(LevelIdentifier.CAVE_IDENTIFIER, BiomeRegistry.FOREST));
        this.addPreset(100, new CaveBoomPrankGenerationPreset(LevelIdentifier.CAVE_IDENTIFIER, BiomeRegistry.FOREST));
        this.addPreset(100, new CozyCaveHomeGenerationPreset(LevelIdentifier.CAVE_IDENTIFIER, BiomeRegistry.FOREST));
        this.addPreset(85, new SmallCaveLabyrinthGenerationPreset(LevelIdentifier.CAVE_IDENTIFIER, BiomeRegistry.FOREST));
        this.addPreset(85, new SurfaceBelowSurfaceGenerationPreset(LevelIdentifier.CAVE_IDENTIFIER, BiomeRegistry.FOREST));
        this.addPreset(85, new SwampyCaveLakeGenerationPreset(LevelIdentifier.CAVE_IDENTIFIER, BiomeRegistry.FOREST));
        this.addPreset(100, new MinersOfficeGenerationPreset(LevelIdentifier.CAVE_IDENTIFIER, BiomeRegistry.FOREST));
        this.addPreset(85, new VampireChurchGenerationPreset(LevelIdentifier.CAVE_IDENTIFIER, BiomeRegistry.FOREST));
        this.addPreset(100, new SmallCaveHomeRuinGenerationPreset(LevelIdentifier.CAVE_IDENTIFIER, BiomeRegistry.FOREST));
        this.addPreset(85, new LargeTempleRuinsGenerationPreset(LevelIdentifier.CAVE_IDENTIFIER, BiomeRegistry.FOREST));
        this.addPreset(85, new SunkenHomeGenerationPreset(new WallSet[]{WallSet.willow, WallSet.swampStone, WallSet.wood}, new FurnitureSet[]{FurnitureSet.oak, FurnitureSet.pine, FurnitureSet.spruce}, new TreeSet[]{TreeSet.oak, TreeSet.spruce}, new BushSet[]{BushSet.raspberry, BushSet.blueberry, BushSet.blackberry}, new BushSet[]{BushSet.raspberry, BushSet.blueberry, BushSet.blackberry}, new FenceSet[]{FenceSet.iron, FenceSet.wood, FenceSet.stone}, new FloorSet[]{FloorSet.stoneTiled, FloorSet.stoneBrick}, new FloorSet[]{FloorSet.stoneTiled, FloorSet.stoneBrick}, BiomeRegistry.FOREST));
    }

    protected void addSnowPresets() {
        this.addPreset(85, new CapturedUndergroundFortressGenerationPreset(LevelIdentifier.CAVE_IDENTIFIER, BiomeRegistry.SNOW));
        this.addPreset(100, new CaveBoomPrankGenerationPreset(LevelIdentifier.CAVE_IDENTIFIER, BiomeRegistry.SNOW));
        this.addPreset(100, new CozyCaveHomeGenerationPreset(LevelIdentifier.CAVE_IDENTIFIER, BiomeRegistry.SNOW));
        this.addPreset(85, new SmallCaveLabyrinthGenerationPreset(LevelIdentifier.CAVE_IDENTIFIER, BiomeRegistry.SNOW));
        this.addPreset(85, new SurfaceBelowSurfaceGenerationPreset(LevelIdentifier.CAVE_IDENTIFIER, BiomeRegistry.SNOW));
        this.addPreset(100, new MinersOfficeGenerationPreset(LevelIdentifier.CAVE_IDENTIFIER, BiomeRegistry.SNOW));
        this.addPreset(100, new SmallCaveHomeRuinGenerationPreset(LevelIdentifier.CAVE_IDENTIFIER, BiomeRegistry.SNOW));
        this.addPreset(85, new LargeTempleRuinsGenerationPreset(LevelIdentifier.CAVE_IDENTIFIER, BiomeRegistry.SNOW));
        this.addPreset(85, new CaveHoboHomeGenerationPreset(LevelIdentifier.CAVE_IDENTIFIER, BiomeRegistry.SNOW));
    }

    protected void addPlainsPresets() {
        this.addPreset(85, new CapturedUndergroundFortressGenerationPreset(LevelIdentifier.CAVE_IDENTIFIER, BiomeRegistry.PLAINS));
        this.addPreset(100, new CaveBoomPrankGenerationPreset(LevelIdentifier.CAVE_IDENTIFIER, BiomeRegistry.PLAINS));
        this.addPreset(100, new CozyCaveHomeGenerationPreset(LevelIdentifier.CAVE_IDENTIFIER, BiomeRegistry.PLAINS));
        this.addPreset(100, new SmallCaveLabyrinthGenerationPreset(LevelIdentifier.CAVE_IDENTIFIER, BiomeRegistry.PLAINS));
        this.addPreset(85, new SurfaceBelowSurfaceGenerationPreset(LevelIdentifier.CAVE_IDENTIFIER, BiomeRegistry.PLAINS));
        this.addPreset(100, new MinersOfficeGenerationPreset(LevelIdentifier.CAVE_IDENTIFIER, BiomeRegistry.PLAINS));
        this.addPreset(100, new SmallCaveHomeRuinGenerationPreset(LevelIdentifier.CAVE_IDENTIFIER, BiomeRegistry.PLAINS));
        this.addPreset(85, new LargeTempleRuinsGenerationPreset(LevelIdentifier.CAVE_IDENTIFIER, BiomeRegistry.PLAINS));
        this.addPreset(85, new SunkenHomeGenerationPreset(new WallSet[]{WallSet.willow, WallSet.swampStone, WallSet.wood}, new FurnitureSet[]{FurnitureSet.maple, FurnitureSet.oak}, new TreeSet[]{TreeSet.birch, TreeSet.maple}, new BushSet[]{BushSet.raspberry, BushSet.blueberry, BushSet.blackberry}, new BushSet[]{BushSet.raspberry, BushSet.blueberry, BushSet.blackberry}, new FenceSet[]{FenceSet.iron, FenceSet.wood, FenceSet.stone}, new FloorSet[]{FloorSet.graniteBrick, FloorSet.stoneTiled, FloorSet.stoneBrick}, new FloorSet[]{FloorSet.graniteBrick, FloorSet.stoneTiled, FloorSet.stoneBrick}, BiomeRegistry.PLAINS));
    }

    protected void addSwampPresets() {
        this.addPreset(85, new CapturedUndergroundFortressGenerationPreset(LevelIdentifier.CAVE_IDENTIFIER, BiomeRegistry.SWAMP));
        this.addPreset(100, new CaveBoomPrankGenerationPreset(LevelIdentifier.CAVE_IDENTIFIER, BiomeRegistry.SWAMP));
        this.addPreset(100, new CozyCaveHomeGenerationPreset(LevelIdentifier.CAVE_IDENTIFIER, BiomeRegistry.SWAMP));
        this.addPreset(100, new SmallCaveLabyrinthGenerationPreset(LevelIdentifier.CAVE_IDENTIFIER, BiomeRegistry.SWAMP));
        this.addPreset(85, new SlimeRanchGenerationPreset(LevelIdentifier.CAVE_IDENTIFIER, BiomeRegistry.SWAMP));
        this.addPreset(85, new SurfaceBelowSurfaceGenerationPreset(LevelIdentifier.CAVE_IDENTIFIER, BiomeRegistry.SWAMP));
        this.addPreset(85, new SwampyCaveLakeGenerationPreset(LevelIdentifier.CAVE_IDENTIFIER, BiomeRegistry.SWAMP));
        this.addPreset(100, new MinersOfficeGenerationPreset(LevelIdentifier.CAVE_IDENTIFIER, BiomeRegistry.SWAMP));
        this.addPreset(85, new VampireChurchGenerationPreset(LevelIdentifier.CAVE_IDENTIFIER, BiomeRegistry.SWAMP));
        this.addPreset(100, new SmallCaveHomeRuinGenerationPreset(LevelIdentifier.CAVE_IDENTIFIER, BiomeRegistry.SWAMP));
        this.addPreset(85, new LargeTempleRuinsGenerationPreset(LevelIdentifier.CAVE_IDENTIFIER, BiomeRegistry.SWAMP));
        this.addPreset(85, new SwampSettlerRuinsGenerationPreset(LevelIdentifier.CAVE_IDENTIFIER, BiomeRegistry.SWAMP));
        this.addPreset(85, new SunkenHomeGenerationPreset(new WallSet[]{WallSet.willow, WallSet.swampStone, WallSet.wood}, new FurnitureSet[]{FurnitureSet.spruce, FurnitureSet.dungeon, FurnitureSet.oak, FurnitureSet.willow, FurnitureSet.deadwood}, new TreeSet[]{TreeSet.willow, TreeSet.spruce}, new BushSet[]{BushSet.raspberry, BushSet.blueberry, BushSet.blackberry}, new BushSet[]{BushSet.raspberry, BushSet.blueberry, BushSet.blackberry}, new FenceSet[]{FenceSet.iron, FenceSet.wood, FenceSet.stone}, new FloorSet[]{FloorSet.stoneTiled, FloorSet.stoneBrick, FloorSet.swampStoneBrick}, new FloorSet[]{FloorSet.stoneTiled, FloorSet.stoneBrick, FloorSet.swampStoneBrick}, BiomeRegistry.SWAMP));
    }

    protected void addDesertPresets() {
        this.addPreset(85, new CapturedUndergroundFortressGenerationPreset(LevelIdentifier.CAVE_IDENTIFIER, BiomeRegistry.DESERT));
        this.addPreset(100, new CaveBoomPrankGenerationPreset(LevelIdentifier.CAVE_IDENTIFIER, BiomeRegistry.DESERT));
        this.addPreset(100, new CozyCaveHomeGenerationPreset(LevelIdentifier.CAVE_IDENTIFIER, BiomeRegistry.DESERT));
        this.addPreset(85, new SmallCaveLabyrinthGenerationPreset(LevelIdentifier.CAVE_IDENTIFIER, BiomeRegistry.DESERT));
        this.addPreset(85, new SurfaceBelowSurfaceGenerationPreset(LevelIdentifier.CAVE_IDENTIFIER, BiomeRegistry.DESERT));
        this.addPreset(100, new MinersOfficeGenerationPreset(LevelIdentifier.CAVE_IDENTIFIER, BiomeRegistry.DESERT));
        this.addPreset(100, new SmallCaveHomeRuinGenerationPreset(LevelIdentifier.CAVE_IDENTIFIER, BiomeRegistry.DESERT));
        this.addPreset(85, new LargeTempleRuinsGenerationPreset(LevelIdentifier.CAVE_IDENTIFIER, BiomeRegistry.DESERT));
    }
}

