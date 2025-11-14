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
import necesse.level.maps.presets.SmallCaveHomeRuinPreset;
import necesse.level.maps.presets.set.CarpetSet;
import necesse.level.maps.presets.set.FloorSet;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.PresetSet;
import necesse.level.maps.presets.set.WallSet;

public class SmallCaveHomeRuinGenerationPreset
extends SimpleGenerationPreset {
    public final Biome biome;
    public final LevelIdentifier levelIdentifier;
    public final FloorSet[] floors;
    public final FurnitureSet[] furniture;
    public final CarpetSet[] carpet;
    public final WallSet[] walls;

    public SmallCaveHomeRuinGenerationPreset(LevelIdentifier levelIdentifier, Biome biome) {
        super(20, true, true, true, false, biome);
        this.biome = biome;
        this.levelIdentifier = levelIdentifier;
        this.floors = (FloorSet[])FloorSet.getReducedSetForBiome((PresetSet[])new FloorSet[]{FloorSet.palm, FloorSet.wood, FloorSet.stone, FloorSet.deadWood, FloorSet.sandstone, FloorSet.snowStone, FloorSet.sandstoneBrick, FloorSet.swampStone, FloorSet.swampStoneBrick, FloorSet.snowStoneBrick, FloorSet.dungeon}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new FloorSet[0]);
        this.furniture = (FurnitureSet[])FurnitureSet.getReducedSetForBiome((PresetSet[])new FurnitureSet[]{FurnitureSet.bamboo, FurnitureSet.pine, FurnitureSet.oak, FurnitureSet.maple, FurnitureSet.spruce}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new FurnitureSet[0]);
        this.carpet = (CarpetSet[])CarpetSet.getReducedSetForBiome((PresetSet[])new CarpetSet[]{CarpetSet.velour, CarpetSet.brownbear, CarpetSet.green, CarpetSet.leather}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new CarpetSet[0]);
        this.walls = (WallSet[])WallSet.getReducedSetForBiome((PresetSet[])new WallSet[]{WallSet.stone, WallSet.granite, WallSet.basalt, WallSet.swampStone, WallSet.snowStone, WallSet.sandstone}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new WallSet[0]);
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
        return new SmallCaveHomeRuinPreset(this.biome, this.levelIdentifier, random, random.getOneOf(this.floors), random.getOneOf(this.floors), random.getOneOf(this.floors), random.getOneOf(this.furniture), random.getOneOf(this.carpet), random.getOneOf(this.walls));
    }
}

