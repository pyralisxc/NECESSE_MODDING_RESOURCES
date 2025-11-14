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
import necesse.level.maps.presets.BigPirateShipPreset;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.WallSet;

public class BigPirateShipGenerationPreset
extends SimpleGenerationPreset {
    public WallSet[] wallSets;
    public FurnitureSet[] furnitureSets;

    public BigPirateShipGenerationPreset(Biome ... biomes) {
        super(20, true, true, true, false, biomes);
        this.wallSets = new WallSet[]{WallSet.wood, WallSet.palm};
        this.furnitureSets = new FurnitureSet[]{FurnitureSet.oak, FurnitureSet.spruce, FurnitureSet.pine, FurnitureSet.palm, FurnitureSet.maple, FurnitureSet.bone, FurnitureSet.deadwood};
    }

    public BigPirateShipGenerationPreset(WallSet[] wallSets, FurnitureSet[] furnitureSets, Biome ... biomes) {
        super(20, true, true, true, false, biomes);
        this.wallSets = wallSets;
        this.furnitureSets = furnitureSets;
    }

    @Override
    public void setupTester(WorldPresetTester tester) {
        tester.addApplyPredicate(new WorldApplyAreaPredicate(-5, -5, tester.width + 5, tester.height + 5, 0, new WorldApplyAreaPredicate.WorldApplyGridTest(5){

            @Override
            public boolean isValidTile(WorldPreset preset, LevelPresetsRegion presetsRegion, BiomeGeneratorStack generatorStack, int tileX, int tileY) {
                return generatorStack.isSurfaceOcean(tileX, tileY);
            }
        }));
    }

    @Override
    public Preset getPreset(GameRandom random) {
        WallSet wallSet = random.getOneOf(this.wallSets);
        FurnitureSet furnitureSet = random.getOneOf(this.furnitureSets);
        return new BigPirateShipPreset(random, wallSet, furnitureSet);
    }
}

