/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.worldPresets;

import necesse.engine.registries.ObjectRegistry;
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
import necesse.level.maps.presets.SurfaceBelowSurfacePreset;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.GroundSet;
import necesse.level.maps.presets.set.PresetSet;
import necesse.level.maps.presets.set.RockAndOreSet;
import necesse.level.maps.presets.set.TreeSet;

public class SurfaceBelowSurfaceGenerationPreset
extends SimpleGenerationPreset {
    public final Biome biome;
    public final LevelIdentifier levelIdentifier;
    public final FurnitureSet[] bench;
    public final RockAndOreSet[] walls;
    public final GroundSet[] ground;
    public final TreeSet[] trees;

    public SurfaceBelowSurfaceGenerationPreset(LevelIdentifier levelIdentifier, Biome biome) {
        super(20, true, true, true, false, biome);
        this.biome = biome;
        this.levelIdentifier = levelIdentifier;
        this.bench = (FurnitureSet[])FurnitureSet.getReducedSetForBiome((PresetSet[])new FurnitureSet[]{FurnitureSet.oak, FurnitureSet.maple, FurnitureSet.dungeon}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new FurnitureSet[0]);
        this.walls = (RockAndOreSet[])RockAndOreSet.getReducedSetForBiome((PresetSet[])new RockAndOreSet[]{RockAndOreSet.deepdesertcave, RockAndOreSet.swampcave, RockAndOreSet.forestcave, RockAndOreSet.deepplainscave, RockAndOreSet.desertcave, RockAndOreSet.deepforestcave, RockAndOreSet.snowcave, RockAndOreSet.deepswampcave, RockAndOreSet.plainscave, RockAndOreSet.deepsnowcave}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new RockAndOreSet[0]);
        this.ground = (GroundSet[])GroundSet.getReducedSetForBiome((PresetSet[])new GroundSet[]{GroundSet.forest, GroundSet.snow, GroundSet.plains, GroundSet.swamp, GroundSet.desert}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new GroundSet[0]);
        this.trees = (TreeSet[])TreeSet.getReducedSetForBiome((PresetSet[])new TreeSet[]{TreeSet.willow, TreeSet.pine, TreeSet.palm, TreeSet.birch, TreeSet.maple, TreeSet.oak, TreeSet.spruce}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new TreeSet[0]);
    }

    @Override
    public void setupTester(WorldPresetTester tester) {
        tester.addApplyPredicate(new WorldApplyAreaPredicate(0, 0, tester.width - 1, tester.height - 1, 0, new WorldApplyAreaPredicate.WorldApplyCornerTest(){

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
        RockAndOreSet chosenWalls = random.getOneOf(this.walls);
        return new SurfaceBelowSurfacePreset(this.biome, this.levelIdentifier, random, random.getOneOf(this.bench), chosenWalls, random.getOneOf(this.ground), random.getOneOf(this.trees)).randomlyReplaceObjects(ObjectRegistry.getObjectID("woodcolumn"), () -> random.getOneOf(ObjectRegistry.getObjectID("air"), chosenWalls.rock));
    }
}

