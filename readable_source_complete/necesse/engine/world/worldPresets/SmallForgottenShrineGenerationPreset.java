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
import necesse.level.maps.presets.SmallForgottenShrinePreset;
import necesse.level.maps.presets.set.ColumnSet;
import necesse.level.maps.presets.set.GroundSet;
import necesse.level.maps.presets.set.PresetSet;
import necesse.level.maps.presets.set.RockAndOreSet;
import necesse.level.maps.presets.set.TreeSet;

public class SmallForgottenShrineGenerationPreset
extends SimpleGenerationPreset {
    public final Biome biome;
    public final LevelIdentifier levelIdentifier;
    public final TreeSet[] trees;
    public final RockAndOreSet[] stones;
    public final ColumnSet[] column;
    public final GroundSet[] ground;

    public SmallForgottenShrineGenerationPreset(LevelIdentifier levelIdentifier, Biome biome) {
        super(20, true, true, false, false, biome);
        this.biome = biome;
        this.levelIdentifier = levelIdentifier;
        this.trees = (TreeSet[])TreeSet.getReducedSetForBiome((PresetSet[])new TreeSet[]{TreeSet.willow, TreeSet.pine, TreeSet.birch, TreeSet.palm, TreeSet.oak, TreeSet.deadwood, TreeSet.maple, TreeSet.spruce}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new TreeSet[0]);
        this.stones = (RockAndOreSet[])RockAndOreSet.getReducedSetForBiome((PresetSet[])new RockAndOreSet[]{RockAndOreSet.forest, RockAndOreSet.snow, RockAndOreSet.plains, RockAndOreSet.swamp, RockAndOreSet.desert}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new RockAndOreSet[0]);
        this.column = (ColumnSet[])ColumnSet.getReducedSetForBiome((PresetSet[])new ColumnSet[]{ColumnSet.stone, ColumnSet.sandstone, ColumnSet.snowstone, ColumnSet.swampstone, ColumnSet.granite}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new ColumnSet[0]);
        this.ground = (GroundSet[])GroundSet.getReducedSetForBiome((PresetSet[])new GroundSet[]{GroundSet.forest, GroundSet.plains, GroundSet.snow, GroundSet.swamp}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new GroundSet[0]);
    }

    @Override
    public void setupTester(WorldPresetTester tester) {
        tester.addApplyPredicate(new WorldApplyAreaPredicate(0, 0, tester.width - 1, tester.height - 1, 0, new WorldApplyAreaPredicate.WorldApplyCornerTest(){

            @Override
            public boolean isValidTile(WorldPreset preset, LevelPresetsRegion presetsRegion, BiomeGeneratorStack generatorStack, int tileX, int tileY) {
                if (presetsRegion.identifier.equals(LevelIdentifier.SURFACE_IDENTIFIER)) {
                    return !generatorStack.isSurfaceOceanOrRiverOrBeach(tileX, tileY);
                }
                return false;
            }
        }));
    }

    @Override
    public Preset getPreset(GameRandom random) {
        return new SmallForgottenShrinePreset(this.biome, this.levelIdentifier, random, random.getOneOf(this.trees), random.getOneOf(this.stones), random.getOneOf(this.column), random.getOneOf(this.ground));
    }
}

