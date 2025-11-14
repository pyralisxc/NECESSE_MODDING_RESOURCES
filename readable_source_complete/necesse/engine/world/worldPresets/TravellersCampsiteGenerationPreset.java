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
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.TravellersCampsitePreset;
import necesse.level.maps.presets.set.BushSet;
import necesse.level.maps.presets.set.FenceSet;
import necesse.level.maps.presets.set.TreeSet;

public class TravellersCampsiteGenerationPreset
extends SimpleGenerationPreset {
    public TreeSet[] treeSets;
    public FenceSet[] fenceSets;
    public BushSet[] bushSets;

    public TravellersCampsiteGenerationPreset(FenceSet[] fenceSets, TreeSet[] treeSets, BushSet[] bushSets, Biome ... biomes) {
        super(20, true, true, false, false, biomes);
        this.treeSets = treeSets;
        this.fenceSets = fenceSets;
        this.bushSets = bushSets;
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
        TreeSet treeSet = random.getOneOf(this.treeSets);
        FenceSet fenceSet = random.getOneOf(this.fenceSets);
        BushSet bushSet = random.getOneOf(this.bushSets);
        return new TravellersCampsitePreset(random, fenceSet, treeSet, bushSet);
    }
}

