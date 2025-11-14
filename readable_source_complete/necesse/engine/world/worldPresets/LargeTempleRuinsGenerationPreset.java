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
import necesse.level.maps.presets.LargeTempleRuinsPreset;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.set.ChestRoomSet;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.PresetSet;

public class LargeTempleRuinsGenerationPreset
extends SimpleGenerationPreset {
    public final ChestRoomSet[] chestRoomSets;
    public final FurnitureSet[] chests;

    public LargeTempleRuinsGenerationPreset(LevelIdentifier levelIdentifier, Biome biome) {
        super(20, true, true, true, false, biome);
        this.chestRoomSets = (ChestRoomSet[])ChestRoomSet.getReducedSetForBiome((PresetSet[])new ChestRoomSet[]{ChestRoomSet.basalt, ChestRoomSet.granite, ChestRoomSet.deepStone, ChestRoomSet.swampStone, ChestRoomSet.deepSnowStone, ChestRoomSet.snowStone, ChestRoomSet.deepSwampStone, ChestRoomSet.deepSandstone, ChestRoomSet.sandstone, ChestRoomSet.stone}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new ChestRoomSet[0]);
        this.chests = (FurnitureSet[])FurnitureSet.getReducedSetForBiome((PresetSet[])new FurnitureSet[]{FurnitureSet.birch, FurnitureSet.deadwood, FurnitureSet.oak, FurnitureSet.maple, FurnitureSet.bone, FurnitureSet.dungeon}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new FurnitureSet[0]);
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
        return new LargeTempleRuinsPreset(random, random.getOneOf(this.chestRoomSets), random.getOneOf(this.chests));
    }
}

