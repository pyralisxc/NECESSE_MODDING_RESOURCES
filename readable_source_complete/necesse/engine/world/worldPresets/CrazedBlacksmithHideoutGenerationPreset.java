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
import necesse.level.maps.presets.CrazedBlacksmithHideoutPreset;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.set.ColumnSet;
import necesse.level.maps.presets.set.FloorSet;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.PresetSet;
import necesse.level.maps.presets.set.WallSet;

public class CrazedBlacksmithHideoutGenerationPreset
extends SimpleGenerationPreset {
    public final WallSet[] walls;
    public final FloorSet[] floor;
    public final FurnitureSet[] furniture;
    public final ColumnSet[] columns;

    public CrazedBlacksmithHideoutGenerationPreset(LevelIdentifier levelIdentifier, Biome biome) {
        super(20, true, true, false, false, biome);
        this.walls = (WallSet[])WallSet.getReducedSetForBiome((PresetSet[])new WallSet[]{WallSet.granite, WallSet.brick, WallSet.sandstone}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new WallSet[0]);
        this.floor = (FloorSet[])FloorSet.getReducedSetForBiome((PresetSet[])new FloorSet[]{FloorSet.deepStoneBrick, FloorSet.graniteBrick, FloorSet.stoneBrick, FloorSet.sandstone}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new FloorSet[0]);
        this.furniture = (FurnitureSet[])FurnitureSet.getReducedSetForBiome((PresetSet[])new FurnitureSet[]{FurnitureSet.palm, FurnitureSet.oak, FurnitureSet.maple, FurnitureSet.deadwood, FurnitureSet.dungeon}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new FurnitureSet[]{FurnitureSet.palm, FurnitureSet.dungeon, FurnitureSet.oak});
        this.columns = (ColumnSet[])ColumnSet.getReducedSetForBiome((PresetSet[])new ColumnSet[]{ColumnSet.granite, ColumnSet.deepstone, ColumnSet.sandstone}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new ColumnSet[0]);
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
        return new CrazedBlacksmithHideoutPreset(random, random.getOneOf(this.walls), random.getOneOf(this.floor), random.getOneOf(this.furniture), random.getOneOf(this.columns));
    }
}

