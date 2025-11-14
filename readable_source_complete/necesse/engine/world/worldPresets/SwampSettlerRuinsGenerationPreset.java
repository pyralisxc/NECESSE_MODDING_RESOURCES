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
import necesse.level.maps.presets.SwampSettlerRuinsPreset;
import necesse.level.maps.presets.set.CarpetSet;
import necesse.level.maps.presets.set.ColumnSet;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.PresetSet;
import necesse.level.maps.presets.set.RockAndOreSet;
import necesse.level.maps.presets.set.WallSet;

public class SwampSettlerRuinsGenerationPreset
extends SimpleGenerationPreset {
    protected LevelIdentifier levelIdentifier;
    public final CarpetSet[] carpets;
    public final FurnitureSet[] furnitures;
    public final WallSet[] walls;
    public final RockAndOreSet[] rocks;
    public final FurnitureSet[] chests;
    public final ColumnSet[] columns;

    public SwampSettlerRuinsGenerationPreset(LevelIdentifier levelIdentifier, Biome biome) {
        super(20, true, true, true, false, biome);
        this.levelIdentifier = levelIdentifier;
        this.carpets = (CarpetSet[])CarpetSet.getReducedSetForBiome((PresetSet[])new CarpetSet[]{CarpetSet.velour, CarpetSet.green, CarpetSet.leather}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new CarpetSet[0]);
        this.furnitures = (FurnitureSet[])FurnitureSet.getReducedSetForBiome((PresetSet[])new FurnitureSet[]{FurnitureSet.bamboo, FurnitureSet.willow, FurnitureSet.oak, FurnitureSet.dungeon}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new FurnitureSet[0]);
        this.walls = (WallSet[])WallSet.getReducedSetForBiome((PresetSet[])new WallSet[]{WallSet.swampStone, WallSet.deepSwampStone}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new WallSet[0]);
        this.rocks = (RockAndOreSet[])RockAndOreSet.getReducedSetForBiome((PresetSet[])new RockAndOreSet[]{RockAndOreSet.swampcave, RockAndOreSet.swamp, RockAndOreSet.deepswampcave}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new RockAndOreSet[0]);
        this.chests = (FurnitureSet[])FurnitureSet.getReducedSetForBiome((PresetSet[])new FurnitureSet[]{FurnitureSet.bamboo, FurnitureSet.willow, FurnitureSet.oak, FurnitureSet.dungeon}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new FurnitureSet[0]);
        this.columns = (ColumnSet[])ColumnSet.getReducedSetForBiome((PresetSet[])new ColumnSet[]{ColumnSet.swampstone, ColumnSet.deepswampstone}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new ColumnSet[0]);
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
        return new SwampSettlerRuinsPreset(random, this.levelIdentifier, random.getOneOf(this.carpets), random.getOneOf(this.furnitures), random.getOneOf(this.walls), random.getOneOf(this.rocks), random.getOneOf(this.chests), random.getOneOf(this.columns));
    }
}

