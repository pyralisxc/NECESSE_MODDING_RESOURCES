/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.worldPresets;

import java.awt.Dimension;
import java.awt.Point;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.biomeGenerator.BiomeGeneratorStack;
import necesse.engine.world.worldPresets.LevelPresetsRegion;
import necesse.engine.world.worldPresets.LinesGenerationWorldPreset;
import necesse.engine.world.worldPresets.WorldPreset;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.generationModules.CellAutomaton;

public class SpiderNestsWorldPreset
extends WorldPreset {
    public Biome biome;
    public LevelIdentifier levelIdentifier;
    public float presetsPerRegion;
    public String spiderMobStringID;
    public int arms = 5;
    public int armsMinRange = 8;
    public int armsMaxRange = 15;
    public int armsMinWidth = 3;
    public int armsMaxWidth = 5;

    public SpiderNestsWorldPreset(Biome biome, LevelIdentifier levelIdentifier, float presetsPerRegion, String spiderMobStringID) {
        this.biome = biome;
        this.levelIdentifier = levelIdentifier;
        this.presetsPerRegion = presetsPerRegion;
        this.spiderMobStringID = spiderMobStringID;
    }

    @Override
    public boolean shouldAddToRegion(LevelPresetsRegion presetsRegion) {
        return presetsRegion.identifier.equals(this.levelIdentifier) && presetsRegion.hasAnyOfBiome(this.biome.getID());
    }

    public boolean isValidPosition(int tileX, int tileY, int width, int height, LevelPresetsRegion presetsRegion, BiomeGeneratorStack generatorStack) {
        return !generatorStack.isCaveRiverOrLava(tileX + width / 2, tileY + height / 2);
    }

    @Override
    public void addToRegion(GameRandom random, final LevelPresetsRegion presetsRegion, final BiomeGeneratorStack generatorStack, PerformanceTimerManager performanceTimer) {
        int total = SpiderNestsWorldPreset.getTotalBiomePoints(random, presetsRegion, this.biome, this.presetsPerRegion);
        for (int i = 0; i < total; ++i) {
            LinesGenerationWorldPreset lg;
            final int size = this.armsMaxRange + this.armsMaxWidth + this.armsMinRange;
            Dimension dimension = new Dimension(size, size);
            Point tile = SpiderNestsWorldPreset.findRandomBiomePresetTile(random, presetsRegion, generatorStack, this.biome, 20, dimension, "minibiomes", new WorldPreset.ValidTilePredicate(){

                @Override
                public boolean isValidPosition(int tileX, int tileY) {
                    return SpiderNestsWorldPreset.this.isValidPosition(tileX, tileY, size, size, presetsRegion, generatorStack);
                }
            });
            if (tile == null || !(lg = new LinesGenerationWorldPreset(tile.x + size / 2, tile.y + size / 2).addRandomArms(random, this.arms, this.armsMinRange, this.armsMaxRange, this.armsMinWidth, this.armsMaxWidth)).isWithinPresetRegionBounds(presetsRegion)) continue;
            presetsRegion.addPreset((WorldPreset)this, lg.getOccupiedTileRectangle(), "minibiomes", new LevelPresetsRegion.WorldPresetPlaceFunction(){

                @Override
                public void place(GameRandom random, Level level, PerformanceTimerManager timer) {
                    CellAutomaton ca = lg.doCellularAutomaton(random);
                    ca.forEachTile(level, (level2, tileX, tileY) -> {
                        level.setTile(tileX, tileY, TileRegistry.spiderNestID);
                        if (random.getChance(0.95f)) {
                            level.setObject(tileX, tileY, ObjectRegistry.cobWebID);
                        } else {
                            level.setObject(tileX, tileY, 0);
                        }
                    });
                    SpiderNestsWorldPreset.this.generateExtraContent(random, level, timer, lg, ca);
                    ca.spawnMobs(level, random, SpiderNestsWorldPreset.this.spiderMobStringID, 4, 8, 1, 4);
                }
            }).setRemoveIfWithinSpawnRegionRange(2);
        }
    }

    public void generateExtraContent(GameRandom random, Level level, PerformanceTimerManager timer, LinesGenerationWorldPreset linesGeneration, CellAutomaton cellAutomaton) {
    }
}

