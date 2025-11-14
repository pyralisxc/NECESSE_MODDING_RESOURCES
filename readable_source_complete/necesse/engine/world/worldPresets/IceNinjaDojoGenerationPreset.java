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
import necesse.level.maps.presets.IceNinjaDojoPreset;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.set.CarpetSet;
import necesse.level.maps.presets.set.PresetSet;
import necesse.level.maps.presets.set.VillageSet;

public class IceNinjaDojoGenerationPreset
extends SimpleGenerationPreset {
    public final VillageSet[] wallsandfloor;
    public final CarpetSet[] carpets;

    public IceNinjaDojoGenerationPreset(LevelIdentifier levelIdentifier, Biome biome) {
        super(20, true, true, false, false, biome);
        this.wallsandfloor = (VillageSet[])VillageSet.getReducedSetForBiome((PresetSet[])new VillageSet[]{VillageSet.palm, VillageSet.maple}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new VillageSet[0]);
        this.carpets = (CarpetSet[])CarpetSet.getReducedSetForBiome((PresetSet[])new CarpetSet[]{CarpetSet.velour, CarpetSet.brownbear, CarpetSet.green, CarpetSet.blue, CarpetSet.purple, CarpetSet.steelgrey, CarpetSet.leather}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new CarpetSet[0]);
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
        return new IceNinjaDojoPreset(random, random.getOneOf(this.wallsandfloor), random.getOneOf(this.carpets));
    }
}

