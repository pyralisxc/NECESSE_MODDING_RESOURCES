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
import necesse.level.maps.presets.AbandonedFountainPreset;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.set.HedgeSet;
import necesse.level.maps.presets.set.TreeSet;

public class AbandonedFountainGenerationPreset
extends SimpleGenerationPreset {
    public TreeSet[] treeSets;
    public HedgeSet[] hedgeSets;

    public AbandonedFountainGenerationPreset(TreeSet[] treeSets, HedgeSet[] hedgeSets, Biome ... biomes) {
        super(20, false, false, false, false, biomes);
        this.treeSets = treeSets;
        this.hedgeSets = hedgeSets;
    }

    @Override
    public void setupTester(WorldPresetTester tester) {
        tester.addApplyPredicate(new WorldApplyAreaPredicate(0, 0, tester.width - 1, tester.height - 1, 0, new WorldApplyAreaPredicate.WorldApplyCornerTest(){

            @Override
            public boolean isValidTile(WorldPreset preset, LevelPresetsRegion presetsRegion, BiomeGeneratorStack generatorStack, int tileX, int tileY) {
                return !generatorStack.isSurfaceOceanOrRiverOrBeach(tileX, tileY);
            }
        }));
    }

    @Override
    public Preset getPreset(GameRandom random) {
        return new AbandonedFountainPreset(random, random.getOneOf(this.treeSets), random.getOneOf(this.treeSets), random.getOneOf(this.hedgeSets));
    }
}

