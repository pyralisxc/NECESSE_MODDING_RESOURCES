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
import necesse.level.maps.presets.CozyCaveHomePreset;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.set.ColumnSet;
import necesse.level.maps.presets.set.FloorSet;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.PresetSet;
import necesse.level.maps.presets.set.WallSet;

public class CozyCaveHomeGenerationPreset
extends SimpleGenerationPreset {
    public final Biome biome;
    public final LevelIdentifier levelIdentifier;
    public final WallSet[] walls;
    public final FloorSet[] woodfloor;
    public final ColumnSet[] columns;
    public final FurnitureSet[] furniture;
    public final FloorSet[] stonefloor;

    public CozyCaveHomeGenerationPreset(LevelIdentifier levelIdentifier, Biome biome) {
        super(20, true, true, true, false, biome);
        this.biome = biome;
        this.levelIdentifier = levelIdentifier;
        this.walls = (WallSet[])WallSet.getReducedSetForBiome((PresetSet[])new WallSet[]{WallSet.palm, WallSet.swampStone, WallSet.deepSnowStone, WallSet.snowStone, WallSet.ice, WallSet.sandstone, WallSet.dungeon, WallSet.stone, WallSet.bamboo, WallSet.willow, WallSet.basalt, WallSet.granite, WallSet.wood, WallSet.brick}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new WallSet[0]);
        this.woodfloor = (FloorSet[])FloorSet.getReducedSetForBiome((PresetSet[])new FloorSet[]{FloorSet.pine, FloorSet.palm, FloorSet.dryad, FloorSet.wood}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new FloorSet[0]);
        this.columns = (ColumnSet[])ColumnSet.getReducedSetForBiome((PresetSet[])new ColumnSet[]{ColumnSet.swampstone, ColumnSet.granite, ColumnSet.wood, ColumnSet.snowstone, ColumnSet.sandstone, ColumnSet.stone}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new ColumnSet[0]);
        this.furniture = (FurnitureSet[])FurnitureSet.getReducedSetForBiome((PresetSet[])new FurnitureSet[]{FurnitureSet.bamboo, FurnitureSet.birch, FurnitureSet.palm, FurnitureSet.spruce}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new FurnitureSet[0]);
        this.stonefloor = (FloorSet[])FloorSet.getReducedSetForBiome((PresetSet[])new FloorSet[]{FloorSet.snowStoneBrick, FloorSet.sandstone, FloorSet.swampStoneBrick, FloorSet.stone, FloorSet.graniteBrick}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new FloorSet[0]);
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
        return new CozyCaveHomePreset(this.biome, this.levelIdentifier, random, random.getOneOf(this.walls), random.getOneOf(this.woodfloor), random.getOneOf(this.columns), random.getOneOf(this.furniture), random.getOneOf(this.stonefloor));
    }
}

