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
import necesse.level.maps.presets.ForgottenShrinePreset;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.HedgeSet;
import necesse.level.maps.presets.set.TreeSet;
import necesse.level.maps.presets.set.WallSet;

public class ForgottenShrineGenerationPreset
extends SimpleGenerationPreset {
    public WallSet[] wallSets;
    public FurnitureSet[] furnitureSets;
    public TreeSet[] treeSets;
    public HedgeSet[] hedgeSets;

    public ForgottenShrineGenerationPreset(WallSet[] wallSets, FurnitureSet[] furnitureSets, TreeSet[] treeSet, HedgeSet[] hedgeSet, Biome ... biomes) {
        super(20, true, true, true, false, biomes);
        this.wallSets = wallSets;
        this.furnitureSets = furnitureSets;
        this.treeSets = treeSet;
        this.hedgeSets = hedgeSet;
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
        WallSet wallSet = random.getOneOf(this.wallSets);
        FurnitureSet furnitureSet = random.getOneOf(this.furnitureSets);
        TreeSet treeSet = random.getOneOf(this.treeSets);
        HedgeSet hedgeSet = random.getOneOf(this.hedgeSets);
        return new ForgottenShrinePreset(random, furnitureSet, wallSet, treeSet, hedgeSet);
    }
}

