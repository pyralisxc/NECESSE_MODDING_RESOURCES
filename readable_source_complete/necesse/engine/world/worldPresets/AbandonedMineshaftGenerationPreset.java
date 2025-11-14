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
import necesse.level.maps.presets.AbandonedMineshaftPreset;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.RockAndOreSet;
import necesse.level.maps.presets.set.TreeSet;
import necesse.level.maps.presets.set.WallSet;

public class AbandonedMineshaftGenerationPreset
extends SimpleGenerationPreset {
    public Biome biome;
    public RockAndOreSet rockAndOreSet;
    public WallSet[] wallSets;
    public FurnitureSet[] furnitureSets;
    public TreeSet[] treeSets;
    public String[] mobIDs;

    public AbandonedMineshaftGenerationPreset(Biome biome, RockAndOreSet rockAndOreSet, WallSet[] wallSets, FurnitureSet[] furnitureSets, TreeSet[] treeSets, String[] mobIDs, Biome ... biomes) {
        super(20, true, true, false, false, biomes);
        this.biome = biome;
        this.rockAndOreSet = rockAndOreSet;
        this.wallSets = wallSets;
        this.furnitureSets = furnitureSets;
        this.treeSets = treeSets;
        this.mobIDs = mobIDs;
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
        return new AbandonedMineshaftPreset(this.biome, random, this.rockAndOreSet, random.getOneOf(this.wallSets), random.getOneOf(this.furnitureSets), random.getOneOf(this.treeSets), this.mobIDs);
    }
}

