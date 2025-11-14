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
import necesse.level.maps.presets.MerchantsAmbushPreset;
import necesse.level.maps.presets.Preset;

public class MerchantsAmbushGenerationPreset
extends SimpleGenerationPreset {
    public MerchantsAmbushGenerationPreset(Biome ... biomes) {
        super(20, true, true, true, false, biomes);
    }

    @Override
    public void setupTester(WorldPresetTester tester) {
        tester.addApplyPredicate(new WorldApplyAreaPredicate(0, 0, tester.width - 1, tester.height - 1, 0, new WorldApplyAreaPredicate.WorldApplyCornerTest(){

            @Override
            public boolean isValidTile(WorldPreset preset, LevelPresetsRegion presetsRegion, BiomeGeneratorStack generatorStack, int tileX, int tileY) {
                return !MerchantsAmbushGenerationPreset.this.isWaterOrLavaOrBeach(presetsRegion, generatorStack, tileX, tileY);
            }
        }));
    }

    @Override
    public Preset getPreset(GameRandom random) {
        return new MerchantsAmbushPreset(random, true);
    }

    @Override
    public void modifyPlaceablePreset(LevelPresetsRegion.PlaceableWorldPreset placeablePreset) {
        super.modifyPlaceablePreset(placeablePreset);
        placeablePreset.setRemoveIfWithinSpawnRegionRange(1);
    }
}

