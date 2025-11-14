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
import necesse.level.maps.presets.FairyTreeCirclePreset;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.set.PresetSet;
import necesse.level.maps.presets.set.RockAndOreSet;

public class FairyTreeCircleGenerationPreset
extends SimpleGenerationPreset {
    protected Biome biome;
    public final RockAndOreSet[] stones;

    public FairyTreeCircleGenerationPreset(LevelIdentifier levelIdentifier, Biome biome) {
        super(20, true, true, false, false, biome);
        this.stones = (RockAndOreSet[])RockAndOreSet.getReducedSetForBiome((PresetSet[])new RockAndOreSet[]{RockAndOreSet.forest, RockAndOreSet.snow, RockAndOreSet.plains, RockAndOreSet.swamp}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new RockAndOreSet[0]);
        this.biome = biome;
    }

    @Override
    public void setupTester(WorldPresetTester tester) {
        tester.addApplyPredicate(new WorldApplyAreaPredicate(0, 0, tester.width - 1, tester.height - 1, 0, new WorldApplyAreaPredicate.WorldApplyCornerTest(){

            @Override
            public boolean isValidTile(WorldPreset preset, LevelPresetsRegion presetsRegion, BiomeGeneratorStack generatorStack, int tileX, int tileY) {
                if (presetsRegion.identifier.equals(LevelIdentifier.SURFACE_IDENTIFIER)) {
                    return !generatorStack.isSurfaceOceanOrRiverOrBeach(tileX, tileY);
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
        return new FairyTreeCirclePreset(random, random.getOneOf(this.stones), this.biome);
    }
}

