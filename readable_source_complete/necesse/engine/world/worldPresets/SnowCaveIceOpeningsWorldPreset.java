/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.worldPresets;

import java.awt.Dimension;
import java.awt.Point;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.biomeGenerator.BiomeGeneratorStack;
import necesse.engine.world.worldPresets.LevelPresetsRegion;
import necesse.engine.world.worldPresets.LinesGenerationWorldPreset;
import necesse.engine.world.worldPresets.WorldPreset;
import necesse.level.maps.Level;
import necesse.level.maps.generationModules.CellAutomaton;

public class SnowCaveIceOpeningsWorldPreset
extends WorldPreset {
    @Override
    public boolean shouldAddToRegion(LevelPresetsRegion presetsRegion) {
        return presetsRegion.identifier.equals(LevelIdentifier.CAVE_IDENTIFIER) && presetsRegion.hasAnyOfBiome(BiomeRegistry.SNOW.getID());
    }

    @Override
    public void addToRegion(GameRandom random, LevelPresetsRegion presetsRegion, final BiomeGeneratorStack generatorStack, PerformanceTimerManager performanceTimer) {
        int total = SnowCaveIceOpeningsWorldPreset.getTotalBiomePoints(random, presetsRegion, BiomeRegistry.SNOW, 0.07f);
        for (int i = 0; i < total; ++i) {
            LinesGenerationWorldPreset lg;
            final Dimension size = new Dimension(14, 14);
            Point tile = SnowCaveIceOpeningsWorldPreset.findRandomBiomePresetTile(random, presetsRegion, generatorStack, BiomeRegistry.SNOW, 20, size, new String[]{"minibiomes", "loot"}, new WorldPreset.ValidTilePredicate(){

                @Override
                public boolean isValidPosition(int tileX, int tileY) {
                    return !generatorStack.isCaveRiverOrLava(tileX + size.width / 2, tileY + size.height / 2);
                }
            });
            if (tile == null || !(lg = new LinesGenerationWorldPreset(tile.x + size.width / 2, tile.y + size.height / 2).addRandomArms(random, 2, 4.0f, 10.0f, 3.0f, 6.0f)).isWithinPresetRegionBounds(presetsRegion)) continue;
            presetsRegion.addPreset((WorldPreset)this, lg.getOccupiedTileRectangle(), new String[]{"minibiomes", "loot"}, new LevelPresetsRegion.WorldPresetPlaceFunction(){

                @Override
                public void place(GameRandom random, Level level, PerformanceTimerManager timer) {
                    boolean hasWater = random.getChance(0.5f);
                    CellAutomaton ca = lg.doCellularAutomaton(random);
                    int rockTile = BiomeRegistry.SNOW.getGenerationCaveTileID();
                    if (hasWater) {
                        CellAutomaton waterCells = new CellAutomaton();
                        ca.forEachTile(level, (level1, tileX, tileY) -> {
                            if (level.getTileID(tileX, tileY) == rockTile) {
                                level.setTile(tileX, tileY, TileRegistry.iceID);
                                if (level.getObject((int)tileX, (int)tileY).isRock) {
                                    level.setObject(tileX, tileY, 0);
                                }
                                waterCells.setAlive(tileX, tileY);
                            }
                        });
                        ca.forEachTile(level, (level1, tileX, tileY) -> {
                            if (ca.countDead(tileX, tileY, CellAutomaton.allNeighbours) > 0) {
                                waterCells.setDead(tileX, tileY);
                            }
                        });
                        waterCells.doCellularAutomaton(4, 100, 2);
                        waterCells.forEachTile(level, (level1, tileX, tileY) -> level.setTile(tileX, tileY, TileRegistry.waterID));
                    } else {
                        ca.forEachTile(level, (level1, tileX, tileY) -> {
                            if (level.getTileID(tileX, tileY) == rockTile) {
                                level.setTile(tileX, tileY, TileRegistry.iceID);
                                if (level.getObject((int)tileX, (int)tileY).isRock) {
                                    level.setObject(tileX, tileY, 0);
                                }
                            }
                        });
                    }
                }
            });
        }
    }
}

