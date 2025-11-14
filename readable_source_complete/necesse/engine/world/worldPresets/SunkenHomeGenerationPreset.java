/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.worldPresets;

import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.biomeGenerator.BiomeGeneratorStack;
import necesse.engine.world.worldPresets.LevelPresetsRegion;
import necesse.engine.world.worldPresets.SimpleGenerationPreset;
import necesse.engine.world.worldPresets.WorldApplyAreaPredicate;
import necesse.engine.world.worldPresets.WorldPreset;
import necesse.engine.world.worldPresets.WorldPresetTester;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.SunkenHomePreset;
import necesse.level.maps.presets.set.BushSet;
import necesse.level.maps.presets.set.FenceSet;
import necesse.level.maps.presets.set.FloorSet;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.TreeSet;
import necesse.level.maps.presets.set.WallSet;

public class SunkenHomeGenerationPreset
extends SimpleGenerationPreset {
    public WallSet[] wallSets;
    public FurnitureSet[] furnitureSets;
    public TreeSet[] treeSets;
    public BushSet[] bushSets;
    public BushSet[] bushSets2;
    public FenceSet[] fenceSets;
    public FloorSet[] floorSets;
    public FloorSet[] floorSets2;

    public SunkenHomeGenerationPreset(WallSet[] wallSets, FurnitureSet[] furnitureSets, TreeSet[] treeSets, BushSet[] bushSets, BushSet[] bushSets2, FenceSet[] fenceSets, FloorSet[] floorSets, FloorSet[] floorSets2, Biome ... biomes) {
        super(20, true, true, true, false, biomes);
        this.wallSets = wallSets;
        this.furnitureSets = furnitureSets;
        this.treeSets = treeSets;
        this.bushSets = bushSets;
        this.bushSets2 = bushSets2;
        this.fenceSets = fenceSets;
        this.floorSets = floorSets;
        this.floorSets2 = floorSets2;
    }

    @Override
    public void setupTester(WorldPresetTester tester) {
        tester.addApplyPredicate(new WorldApplyAreaPredicate(-2, -2, tester.width + 2, tester.height + 2, 0, new WorldApplyAreaPredicate.WorldApplyGridTest(2){

            @Override
            public boolean isValidTile(WorldPreset preset, LevelPresetsRegion presetsRegion, BiomeGeneratorStack generatorStack, int tileX, int tileY) {
                if (presetsRegion.identifier.equals(LevelIdentifier.SURFACE_IDENTIFIER)) {
                    return !generatorStack.isSurfaceOceanOrRiver(tileX, tileY);
                }
                if (presetsRegion.identifier.equals(LevelIdentifier.CAVE_IDENTIFIER)) {
                    return !generatorStack.isCaveRiverOrLava(tileX, tileY);
                }
                if (presetsRegion.identifier.equals(LevelIdentifier.DEEP_CAVE_IDENTIFIER)) {
                    return !generatorStack.isDeepCaveLava(tileX, tileY);
                }
                return false;
            }
        }));
    }

    @Override
    public Preset getPreset(GameRandom random) {
        return new SunkenHomePreset(random.getOneOf(this.wallSets), random.getOneOf(this.furnitureSets), random.getOneOf(this.treeSets), random.getOneOf(this.bushSets), random.getOneOf(this.bushSets2), random.getOneOf(this.fenceSets), random.getOneOf(this.floorSets), random.getOneOf(this.floorSets2));
    }
}

