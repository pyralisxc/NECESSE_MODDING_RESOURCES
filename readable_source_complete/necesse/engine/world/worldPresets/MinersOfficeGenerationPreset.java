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
import necesse.level.maps.presets.MinersOfficePreset;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.set.ColumnSet;
import necesse.level.maps.presets.set.CrystalSet;
import necesse.level.maps.presets.set.FloorSet;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.PresetSet;
import necesse.level.maps.presets.set.RockAndOreSet;

public class MinersOfficeGenerationPreset
extends SimpleGenerationPreset {
    public final Biome biome;
    public final LevelIdentifier levelIdentifier;
    public final RockAndOreSet[] rocksAndWalls;
    public final CrystalSet[] crystal;
    public final FloorSet[] floor;
    public final ColumnSet[] columns;
    public final FurnitureSet[] furniture;

    public MinersOfficeGenerationPreset(LevelIdentifier levelIdentifier, Biome biome) {
        super(20, true, true, false, false, biome);
        this.biome = biome;
        this.levelIdentifier = levelIdentifier;
        this.rocksAndWalls = (RockAndOreSet[])RockAndOreSet.getReducedSetForBiome((PresetSet[])new RockAndOreSet[]{RockAndOreSet.forest, RockAndOreSet.forestcave, RockAndOreSet.deepforestcave, RockAndOreSet.desert, RockAndOreSet.desertcave, RockAndOreSet.deepdesertcave, RockAndOreSet.snow, RockAndOreSet.snowcave, RockAndOreSet.deepsnowcave, RockAndOreSet.swamp, RockAndOreSet.swampcave, RockAndOreSet.deepswampcave, RockAndOreSet.plains, RockAndOreSet.plainscave, RockAndOreSet.deepplainscave}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new RockAndOreSet[0]);
        this.crystal = (CrystalSet[])CrystalSet.getReducedSetForBiome((PresetSet[])new CrystalSet[]{CrystalSet.topaz, CrystalSet.amethyst, CrystalSet.emerald, CrystalSet.sapphire, CrystalSet.ruby}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new CrystalSet[0]);
        this.floor = (FloorSet[])FloorSet.getReducedSetForBiome((PresetSet[])new FloorSet[]{FloorSet.dungeon, FloorSet.dryad, FloorSet.granite, FloorSet.wood, FloorSet.deadWood, FloorSet.stoneTiled, FloorSet.deepStoneBrick, FloorSet.snowStoneBrick, FloorSet.deepSnowStone, FloorSet.sandstoneBrick, FloorSet.swampStoneBrick, FloorSet.deepSwampStoneBrick}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new FloorSet[0]);
        this.columns = (ColumnSet[])ColumnSet.getReducedSetForBiome((PresetSet[])new ColumnSet[]{ColumnSet.wood, ColumnSet.deepstone, ColumnSet.snowstone, ColumnSet.deepsnowstone, ColumnSet.sandstone, ColumnSet.deepsandstone, ColumnSet.swampstone, ColumnSet.deepswampstone}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new ColumnSet[0]);
        this.furniture = (FurnitureSet[])FurnitureSet.getReducedSetForBiome((PresetSet[])new FurnitureSet[]{FurnitureSet.oak, FurnitureSet.maple, FurnitureSet.deadwood, FurnitureSet.dungeon}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new FurnitureSet[0]);
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
        return new MinersOfficePreset(this.biome, this.levelIdentifier, random, random.getOneOf(this.rocksAndWalls), random.getOneOf(this.crystal), random.getOneOf(this.floor), random.getOneOf(this.columns), random.getOneOf(this.furniture));
    }
}

