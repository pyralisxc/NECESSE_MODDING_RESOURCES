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
import necesse.level.maps.presets.BrokenHusbandryFencePreset;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.set.FenceSet;

public class BrokenHusbandryFenceGenerationPreset
extends SimpleGenerationPreset {
    public FenceSet[] fenceSets;

    public BrokenHusbandryFenceGenerationPreset(FenceSet[] fenceSets, Biome ... biomes) {
        super(20, true, true, true, false, biomes);
        this.fenceSets = fenceSets;
    }

    @Override
    public void setupTester(WorldPresetTester tester) {
        tester.addApplyPredicate(new WorldApplyAreaPredicate(0, 0, tester.width - 1, tester.height - 1, 0, new WorldApplyAreaPredicate.WorldApplyCornerTest(){

            @Override
            public boolean isValidTile(WorldPreset preset, LevelPresetsRegion presetsRegion, BiomeGeneratorStack generatorStack, int tileX, int tileY) {
                return !BrokenHusbandryFenceGenerationPreset.this.isWaterOrLavaOrBeach(presetsRegion, generatorStack, tileX, tileY);
            }
        }));
    }

    @Override
    public Preset getPreset(GameRandom random) {
        FenceSet fenceSet = random.getOneOf(this.fenceSets);
        return new BrokenHusbandryFencePreset(random, fenceSet);
    }

    @Override
    public void modifyPlaceablePreset(LevelPresetsRegion.PlaceableWorldPreset placeablePreset) {
        super.modifyPlaceablePreset(placeablePreset);
        placeablePreset.setRemoveIfWithinSpawnRegionRange(1);
    }
}

