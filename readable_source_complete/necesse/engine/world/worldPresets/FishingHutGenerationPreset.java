/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.worldPresets;

import java.awt.Point;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.util.GameRandom;
import necesse.engine.world.biomeGenerator.BiomeGeneratorStack;
import necesse.engine.world.worldPresets.GenerationPreset;
import necesse.engine.world.worldPresets.GenerationPresetTile;
import necesse.engine.world.worldPresets.LevelPresetsRegion;
import necesse.engine.world.worldPresets.WorldApplyAreaPredicate;
import necesse.engine.world.worldPresets.WorldPreset;
import necesse.engine.world.worldPresets.WorldPresetTester;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.presets.FishingHutPreset;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.PresetMirrorException;
import necesse.level.maps.presets.PresetRotateException;
import necesse.level.maps.presets.PresetRotation;
import necesse.level.maps.presets.PresetUtils;
import necesse.level.maps.presets.set.WallSet;

public class FishingHutGenerationPreset
extends GenerationPreset<GenerationPresetTile> {
    public WallSet[] wallSets;
    public int[] floorTileIDs;
    protected WorldPresetTester tester;

    public FishingHutGenerationPreset(WallSet[] wallSets, int[] floorTileIDs, Biome ... biomes) {
        super(biomes);
        this.wallSets = wallSets;
        this.floorTileIDs = floorTileIDs;
        this.tester = new WorldPresetTester(15, 9);
        this.tester.addApplyPredicate(new WorldApplyAreaPredicate(0, 0, 5, 4, 0, new WorldApplyAreaPredicate.WorldApplyCornerTest(){

            @Override
            public boolean isValidTile(WorldPreset preset, LevelPresetsRegion presetsRegion, BiomeGeneratorStack generatorStack, int tileX, int tileY) {
                return !generatorStack.isSurfaceOcean(tileX, tileY);
            }
        }));
        this.tester.addApplyPredicate(new WorldApplyAreaPredicate(11, -1, 15, 3, 0, new WorldApplyAreaPredicate.WorldApplyCornerTest(){

            @Override
            public boolean isValidTile(WorldPreset preset, LevelPresetsRegion presetsRegion, BiomeGeneratorStack generatorStack, int tileX, int tileY) {
                return generatorStack.isSurfaceOcean(tileX, tileY);
            }
        }));
    }

    @Override
    public GenerationPresetTile findRandomTile(GameRandom random, WorldPreset worldPreset, LevelPresetsRegion presetsRegion, BiomeGeneratorStack generatorStack) {
        for (int i = 0; i < 100; ++i) {
            Point region = this.getRandomBiomeRegion(random, presetsRegion);
            if (region == null) {
                return null;
            }
            Point tile = WorldPreset.findRandomPresetTileFromRegion(random, presetsRegion, this.tester.getSize(), region);
            if (tile == null || !this.isValidBiome(generatorStack, tile.x + 8, tile.y + 3) || !generatorStack.isSurfaceOceanBeach(tile.x + 8, tile.y + 3) || presetsRegion.isRectangleOccupied(new String[]{"villages", "minibiomes", "loot"}, tile.x, tile.y, this.tester.width, this.tester.height)) continue;
            GenerationPresetTile defaultTile = new GenerationPresetTile(tile.x, tile.y, this.tester);
            if (defaultTile.canApply(worldPreset, presetsRegion, generatorStack)) {
                return defaultTile;
            }
            GenerationPresetTile mirroredTile = defaultTile.mirrorX(8);
            if (mirroredTile.canApply(worldPreset, presetsRegion, generatorStack)) {
                return mirroredTile;
            }
            GenerationPresetTile rotatedClockWiseTile = defaultTile.rotate(PresetRotation.CLOCKWISE, 8, 3);
            if (rotatedClockWiseTile.canApply(worldPreset, presetsRegion, generatorStack)) {
                return rotatedClockWiseTile;
            }
            GenerationPresetTile rotatedAntiClockWiseTile = defaultTile.rotate(PresetRotation.CLOCKWISE, 8, 3);
            if (!rotatedAntiClockWiseTile.canApply(worldPreset, presetsRegion, generatorStack)) continue;
            return rotatedAntiClockWiseTile;
        }
        return null;
    }

    @Override
    public void addToRegion(GameRandom random, WorldPreset worldPreset, LevelPresetsRegion presetsRegion, BiomeGeneratorStack generatorStack, final GenerationPresetTile tile, PerformanceTimerManager performanceTimer) {
        presetsRegion.addPreset(worldPreset, tile.x, tile.y, tile.tester.getSize(), new String[]{"villages", "minibiomes", "loot"}, new LevelPresetsRegion.WorldPresetPlaceFunction(){

            @Override
            public void place(GameRandom random, Level level, PerformanceTimerManager timer) {
                WorldPreset.ensureRegionsAreGenerated(level, tile.x, tile.y, tile.tester.width, tile.tester.height);
                WallSet wallSet = random.getOneOf(FishingHutGenerationPreset.this.wallSets);
                int tileID = FishingHutGenerationPreset.this.floorTileIDs[random.nextInt(FishingHutGenerationPreset.this.floorTileIDs.length)];
                Preset preset = new FishingHutPreset(random, wallSet, tileID);
                try {
                    preset = tile.modifyPreset(preset);
                    PresetUtils.clearMobsInPreset(preset, level, tile.x, tile.y);
                    preset.applyToLevel(level, tile.x, tile.y);
                }
                catch (PresetMirrorException | PresetRotateException e) {
                    e.printStackTrace();
                }
            }
        }).setDebugName(this.getClass().getSimpleName());
    }
}

