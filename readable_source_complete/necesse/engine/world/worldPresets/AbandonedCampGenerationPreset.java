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
import necesse.level.maps.presets.AbandonedCampPreset;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.TreeSet;
import necesse.level.maps.presets.set.WallSet;

public class AbandonedCampGenerationPreset
extends SimpleGenerationPreset {
    public WallSet[] wallSets;
    public FurnitureSet[] furnitureSets;
    public TreeSet[] treeSets;
    public Biome biome;

    public AbandonedCampGenerationPreset(WallSet[] wallSets, FurnitureSet[] furnitureSets, TreeSet[] treeSet, Biome biome) {
        super(20, true, true, true, false, biome);
        this.wallSets = wallSets;
        this.furnitureSets = furnitureSets;
        this.treeSets = treeSet;
        this.biome = biome;
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
        return new AbandonedCampPreset(random, furnitureSet, wallSet, treeSet, this.biome);
    }
}

