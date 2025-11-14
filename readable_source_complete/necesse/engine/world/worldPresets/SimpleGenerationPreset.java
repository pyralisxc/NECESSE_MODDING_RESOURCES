/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.worldPresets;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
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
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.PresetMirrorException;
import necesse.level.maps.presets.PresetRotateException;
import necesse.level.maps.presets.PresetRotation;
import necesse.level.maps.presets.PresetUtils;

public abstract class SimpleGenerationPreset
extends GenerationPreset<GenerationPresetTile> {
    protected WorldPresetTester tester;
    protected int placeAttempts;
    protected boolean randomizeMirrorX;
    protected boolean randomizeMirrorY;
    protected boolean randomizeRotation;
    protected boolean checkCanPlaceForAllOptions;
    protected ArrayList<String> occupiedSpaceChecks = new ArrayList<String>(Arrays.asList("villages", "minibiomes", "loot"));
    protected ArrayList<String> occupiedSpaceSets = this.occupiedSpaceChecks;
    protected boolean clearMobsInPreset = true;

    public SimpleGenerationPreset(int placeAttempts, boolean randomizeMirrorX, boolean randomizeMirrorY, boolean randomizeRotation, boolean checkCanPlaceForAllOptions, Biome ... biomes) {
        super(biomes);
        this.placeAttempts = placeAttempts;
        this.randomizeMirrorX = randomizeMirrorX;
        this.randomizeMirrorY = randomizeMirrorY;
        this.randomizeRotation = randomizeRotation;
        this.checkCanPlaceForAllOptions = checkCanPlaceForAllOptions;
    }

    @Override
    public void init() {
        super.init();
        Preset preset = this.getPreset(GameRandom.globalRandom);
        int width = this.randomizeRotation ? Math.max(preset.width, preset.height) : preset.width;
        int height = this.randomizeRotation ? Math.max(preset.width, preset.height) : preset.height;
        this.tester = new WorldPresetTester(width, height);
        if (this.randomizeMirrorX) {
            try {
                preset.mirrorX();
            }
            catch (PresetMirrorException e) {
                this.randomizeMirrorX = false;
            }
        }
        if (this.randomizeMirrorY) {
            try {
                preset.mirrorY();
            }
            catch (PresetMirrorException e) {
                this.randomizeMirrorY = false;
            }
        }
        if (this.randomizeRotation) {
            try {
                preset.rotate(PresetRotation.CLOCKWISE);
                preset.rotate(PresetRotation.ANTI_CLOCKWISE);
            }
            catch (PresetRotateException e) {
                this.randomizeRotation = false;
            }
        }
        this.tester.addApplyPredicate(new WorldApplyAreaPredicate(0, 0, this.tester.width - 1, this.tester.height - 1, 0, new WorldApplyAreaPredicate.WorldApplyCornerTest(){

            @Override
            public boolean isValidTile(WorldPreset preset, LevelPresetsRegion presetsRegion, BiomeGeneratorStack generatorStack, int tileX, int tileY) {
                return SimpleGenerationPreset.this.isValidBiome(generatorStack.getLazyBiomeID(tileX, tileY));
            }
        }));
        this.setupTester(this.tester);
    }

    public abstract void setupTester(WorldPresetTester var1);

    public abstract Preset getPreset(GameRandom var1);

    public Preset modifyPreset(Preset preset, LevelPresetsRegion presetsRegion, BiomeGeneratorStack generatorStack, int tileX, int tileY) {
        return preset;
    }

    public void modifyPlaceablePreset(LevelPresetsRegion.PlaceableWorldPreset placeablePreset) {
    }

    @Override
    public GenerationPresetTile findRandomTile(GameRandom random, WorldPreset worldPreset, LevelPresetsRegion presetsRegion, BiomeGeneratorStack generatorStack) {
        for (int i = 0; i < this.placeAttempts; ++i) {
            Point region = this.getRandomBiomeRegion(random, presetsRegion);
            if (region == null) {
                return null;
            }
            Point tile = WorldPreset.findRandomPresetTileFromRegion(random, presetsRegion, this.tester.getSize(), region);
            if (tile == null) continue;
            boolean foundOccupied = false;
            for (String board : this.occupiedSpaceChecks) {
                if (!presetsRegion.isRectangleOccupied(board, tile.x, tile.y, this.tester.width, this.tester.height)) continue;
                foundOccupied = true;
                break;
            }
            if (foundOccupied) continue;
            GenerationPresetTile defaultTile = new GenerationPresetTile(tile.x, tile.y, this.tester);
            if (this.checkCanPlaceForAllOptions) {
                ArrayList<GenerationPresetTile> validTiles = new ArrayList<GenerationPresetTile>();
                if (this.randomizeRotation) {
                    GenerationPresetTile mirroredTile;
                    GenerationPresetTile rotatedTile = defaultTile.rotate(random.getOneOf(PresetRotation.CLOCKWISE, PresetRotation.ANTI_CLOCKWISE, PresetRotation.HALF_180, null));
                    if (rotatedTile.canApply(worldPreset, presetsRegion, generatorStack)) {
                        validTiles.add(rotatedTile);
                    }
                    if (this.randomizeMirrorX && (mirroredTile = rotatedTile.mirrorX()).canApply(worldPreset, presetsRegion, generatorStack)) {
                        validTiles.add(mirroredTile);
                    }
                    if (this.randomizeMirrorY && (mirroredTile = rotatedTile.mirrorY()).canApply(worldPreset, presetsRegion, generatorStack)) {
                        validTiles.add(mirroredTile);
                    }
                } else {
                    GenerationPresetTile mirroredTile;
                    if (this.randomizeMirrorX && (mirroredTile = defaultTile.mirrorX()).canApply(worldPreset, presetsRegion, generatorStack)) {
                        validTiles.add(mirroredTile);
                    }
                    if (this.randomizeMirrorY && (mirroredTile = defaultTile.mirrorY()).canApply(worldPreset, presetsRegion, generatorStack)) {
                        validTiles.add(mirroredTile);
                    }
                }
                if (validTiles.isEmpty()) continue;
                return (GenerationPresetTile)random.getOneOf(validTiles);
            }
            if (this.randomizeMirrorX && random.nextBoolean()) {
                defaultTile = defaultTile.mirrorX();
            }
            if (this.randomizeMirrorY && random.nextBoolean()) {
                defaultTile = defaultTile.mirrorY();
            }
            if (this.randomizeRotation) {
                defaultTile = defaultTile.rotate(random.getOneOf(PresetRotation.CLOCKWISE, PresetRotation.ANTI_CLOCKWISE, PresetRotation.HALF_180, null));
            }
            if (!defaultTile.canApply(worldPreset, presetsRegion, generatorStack)) continue;
            return defaultTile;
        }
        return null;
    }

    @Override
    public void addToRegion(GameRandom random, WorldPreset worldPreset, final LevelPresetsRegion presetsRegion, final BiomeGeneratorStack generatorStack, final GenerationPresetTile tile, PerformanceTimerManager performanceTimer) {
        LevelPresetsRegion.PlaceableWorldPreset placeablePreset = presetsRegion.addPreset(worldPreset, tile.x, tile.y, tile.tester.getSize(), this.occupiedSpaceSets.toArray(new String[0]), new LevelPresetsRegion.WorldPresetPlaceFunction(){

            @Override
            public void place(GameRandom random, Level level, PerformanceTimerManager timer) {
                WorldPreset.ensureRegionsAreGenerated(level, tile.x, tile.y, tile.tester.width, tile.tester.height);
                Preset preset = SimpleGenerationPreset.this.getPreset(random);
                try {
                    preset = tile.modifyPreset(preset);
                    preset = SimpleGenerationPreset.this.modifyPreset(preset, presetsRegion, generatorStack, tile.x, tile.y);
                    if (SimpleGenerationPreset.this.clearMobsInPreset) {
                        PresetUtils.clearMobsInPreset(preset, level, tile.x, tile.y);
                    }
                    preset.applyToLevel(level, tile.x, tile.y);
                }
                catch (PresetMirrorException | PresetRotateException exception) {
                    // empty catch block
                }
            }
        });
        placeablePreset.setDebugName(this.getClass().getSimpleName());
        this.modifyPlaceablePreset(placeablePreset);
    }
}

