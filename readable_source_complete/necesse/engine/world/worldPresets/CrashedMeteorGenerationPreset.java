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
import necesse.level.maps.presets.CrashedMeteorPreset;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.set.CrystalSet;

public class CrashedMeteorGenerationPreset
extends SimpleGenerationPreset {
    public CrystalSet[] crystalSets;

    public CrashedMeteorGenerationPreset(CrystalSet[] crystalSets, Biome ... biomes) {
        super(20, true, true, false, false, biomes);
        this.crystalSets = crystalSets;
    }

    @Override
    public void setupTester(WorldPresetTester tester) {
        tester.addApplyPredicate(new WorldApplyAreaPredicate(-5, -5, tester.width + 5, tester.height + 5, 0, new WorldApplyAreaPredicate.WorldApplyGridTest(6){

            @Override
            public boolean isValidTile(WorldPreset preset, LevelPresetsRegion presetsRegion, BiomeGeneratorStack generatorStack, int tileX, int tileY) {
                return CrashedMeteorGenerationPreset.this.isWaterOrLava(presetsRegion, generatorStack, tileX, tileY);
            }
        }));
    }

    @Override
    public Preset getPreset(GameRandom random) {
        CrystalSet crystalSet = random.getOneOf(this.crystalSets);
        return new CrashedMeteorPreset(crystalSet);
    }
}

