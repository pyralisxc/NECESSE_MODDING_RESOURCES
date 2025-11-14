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
import necesse.level.maps.presets.VillagerCampPreset;
import necesse.level.maps.presets.set.BannerSet;
import necesse.level.maps.presets.set.CropSet;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.PresetSet;
import necesse.level.maps.presets.set.RockAndOreSet;
import necesse.level.maps.presets.set.TreeSet;

public class VillagerCampGenerationPreset
extends SimpleGenerationPreset {
    protected Biome biome;
    protected LevelIdentifier levelIdentifier;
    public final TreeSet[] trees;
    public final CropSet[] crops;
    public final FurnitureSet[] furniture;
    public final RockAndOreSet[] rocks;
    public final BannerSet[] banner;

    public VillagerCampGenerationPreset(LevelIdentifier levelIdentifier, Biome biome) {
        super(20, true, true, false, false, biome);
        this.biome = biome;
        this.levelIdentifier = levelIdentifier;
        this.trees = (TreeSet[])TreeSet.getReducedSetForBiome((PresetSet[])new TreeSet[]{TreeSet.pine, TreeSet.birch, TreeSet.maple, TreeSet.palm, TreeSet.oak, TreeSet.spruce}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new TreeSet[0]);
        this.crops = (CropSet[])CropSet.getReducedSetForBiome((PresetSet[])new CropSet[]{CropSet.cabbage, CropSet.corn, CropSet.onion, CropSet.strawberry, CropSet.tomato, CropSet.chilipepper, CropSet.sugarbeet, CropSet.rice, CropSet.potato, CropSet.wheat, CropSet.beet, CropSet.pumpkin, CropSet.eggplant, CropSet.carrot}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new CropSet[0]);
        this.furniture = (FurnitureSet[])FurnitureSet.getReducedSetForBiome((PresetSet[])new FurnitureSet[]{FurnitureSet.oak, FurnitureSet.maple, FurnitureSet.deadwood, FurnitureSet.dryad}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new FurnitureSet[0]);
        this.rocks = (RockAndOreSet[])RockAndOreSet.getReducedSetForBiome((PresetSet[])new RockAndOreSet[]{RockAndOreSet.forest, RockAndOreSet.snow, RockAndOreSet.plains, RockAndOreSet.swamp, RockAndOreSet.desert}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new RockAndOreSet[0]);
        this.banner = (BannerSet[])BannerSet.getReducedSetForBiome((PresetSet[])new BannerSet[]{BannerSet.eggcellent, BannerSet.fishian, BannerSet.frost, BannerSet.dryad}, (Biome)biome, (LevelIdentifier)levelIdentifier, (PresetSet[])new BannerSet[0]);
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
        return new VillagerCampPreset(random, this.biome, random.getOneOf(this.trees), random.getOneOf(this.crops), random.getOneOf(this.furniture), random.getOneOf(this.rocks), random.getOneOf(this.banner));
    }
}

