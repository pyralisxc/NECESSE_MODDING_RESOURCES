/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.worldPresets;

import necesse.engine.util.GameRandom;
import necesse.engine.world.biomeGenerator.BiomeGeneratorStack;
import necesse.engine.world.worldPresets.LevelPresetsRegion;
import necesse.engine.world.worldPresets.SimpleGenerationPreset;
import necesse.engine.world.worldPresets.WorldApplyAreaPredicate;
import necesse.engine.world.worldPresets.WorldPreset;
import necesse.engine.world.worldPresets.WorldPresetTester;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.presets.DesertTavernPreset;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.WallSet;

public class DesertTavernGenerationPreset
extends SimpleGenerationPreset {
    public FurnitureSet[] furnitureSets;
    public WallSet[] wallSets;

    public DesertTavernGenerationPreset(FurnitureSet[] furnitureSets, WallSet[] wallSets, Biome ... biomes) {
        super(20, true, true, false, false, biomes);
        this.furnitureSets = furnitureSets;
        this.wallSets = wallSets;
    }

    @Override
    public void setupTester(WorldPresetTester tester) {
        tester.addApplyPredicate(new WorldApplyAreaPredicate(0, 0, tester.width - 1, tester.height - 1, 0, new WorldApplyAreaPredicate.WorldApplyCornerTest(){

            @Override
            public boolean isValidTile(WorldPreset preset, LevelPresetsRegion presetsRegion, BiomeGeneratorStack generatorStack, int tileX, int tileY) {
                return !generatorStack.isSurfaceOceanOrRiver(tileX, tileY);
            }
        }));
    }

    @Override
    public Preset getPreset(GameRandom random) {
        FurnitureSet furnitureSet = random.getOneOf(this.furnitureSets);
        WallSet wallSet = random.getOneOf(this.wallSets);
        return new DesertTavernPreset(random, furnitureSet, wallSet);
    }
}

