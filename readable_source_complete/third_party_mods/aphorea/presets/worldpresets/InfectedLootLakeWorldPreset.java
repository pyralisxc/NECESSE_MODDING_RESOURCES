/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.gameLoop.tickManager.PerformanceTimerManager
 *  necesse.engine.util.GameRandom
 *  necesse.engine.util.LevelIdentifier
 *  necesse.engine.world.biomeGenerator.BiomeGeneratorStack
 *  necesse.engine.world.worldPresets.LevelPresetsRegion
 *  necesse.engine.world.worldPresets.WorldPreset
 *  necesse.level.maps.Level
 *  necesse.level.maps.biomes.Biome
 *  necesse.level.maps.presets.Preset
 *  necesse.level.maps.presets.PresetUtils
 */
package aphorea.presets.worldpresets;

import aphorea.presets.InfectedLootLake;
import java.awt.Dimension;
import java.awt.Point;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.biomeGenerator.BiomeGeneratorStack;
import necesse.engine.world.worldPresets.LevelPresetsRegion;
import necesse.engine.world.worldPresets.WorldPreset;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.PresetUtils;

public class InfectedLootLakeWorldPreset
extends WorldPreset {
    protected Dimension size = new Dimension(7, 8);
    public Biome biome;
    public float presetsPerRegion;

    public InfectedLootLakeWorldPreset(float presetsPerRegion, Biome biome) {
        this.biome = biome;
        this.presetsPerRegion = presetsPerRegion;
    }

    public boolean shouldAddToRegion(LevelPresetsRegion presetsRegion) {
        return presetsRegion.identifier.equals(LevelIdentifier.CAVE_IDENTIFIER) && presetsRegion.hasAnyOfBiome(this.biome.getID());
    }

    public void addToRegion(GameRandom random, LevelPresetsRegion presetsRegion, BiomeGeneratorStack generatorStack, PerformanceTimerManager performanceTimer) {
        int total = InfectedLootLakeWorldPreset.getTotalBiomePoints((GameRandom)random, (LevelPresetsRegion)presetsRegion, (Biome)this.biome, (float)this.presetsPerRegion);
        for (int i = 0; i < total; ++i) {
            Point tile = InfectedLootLakeWorldPreset.findRandomPresetTile((GameRandom)random, (LevelPresetsRegion)presetsRegion, (int)20, (Dimension)this.size, (String[])new String[]{"loot", "villages"}, (tileX, tileY) -> this.runCornerCheck(tileX, tileY, this.size.width, this.size.height, (tileX1, tileY1) -> !generatorStack.isSurfaceExpensiveWater(tileX1, tileY1)));
            if (tile == null) continue;
            presetsRegion.addPreset((WorldPreset)this, tile.x, tile.y, this.size, "loot", (random1, level, timer) -> {
                WorldPreset.ensureRegionsAreGenerated((Level)level, (int)tile.x, (int)tile.y, (int)this.size.width, (int)this.size.height);
                InfectedLootLake preset = new InfectedLootLake(random1);
                PresetUtils.clearMobsInPreset((Preset)preset, (Level)level, (int)tile.x, (int)tile.y);
                preset.applyToLevel(level, tile.x, tile.y);
            }).setRemoveIfWithinSpawnRegionRange(1);
        }
    }
}

