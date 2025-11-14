/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.worldPresets;

import java.awt.Dimension;
import java.awt.Point;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.biomeGenerator.BiomeGeneratorStack;
import necesse.engine.world.worldPresets.LevelPresetsRegion;
import necesse.engine.world.worldPresets.LinesGenerationWorldPreset;
import necesse.engine.world.worldPresets.WorldPreset;
import necesse.level.gameObject.GameObject;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;
import necesse.level.maps.generationModules.CellAutomaton;

public class SwampMudAreasWorldPreset
extends WorldPreset {
    @Override
    public boolean shouldAddToRegion(LevelPresetsRegion presetsRegion) {
        return presetsRegion.identifier.equals(LevelIdentifier.CAVE_IDENTIFIER) && presetsRegion.hasAnyOfBiome(BiomeRegistry.SWAMP.getID());
    }

    @Override
    public void addToRegion(GameRandom random, LevelPresetsRegion presetsRegion, final BiomeGeneratorStack generatorStack, PerformanceTimerManager performanceTimer) {
        int total = SwampMudAreasWorldPreset.getTotalBiomePoints(random, presetsRegion, BiomeRegistry.SWAMP, 0.04f);
        for (int i = 0; i < total; ++i) {
            LinesGenerationWorldPreset lg;
            final Dimension size = new Dimension(15, 15);
            Point tile = SwampMudAreasWorldPreset.findRandomBiomePresetTile(random, presetsRegion, generatorStack, BiomeRegistry.SWAMP, 20, size, "minibiomes", new WorldPreset.ValidTilePredicate(){

                @Override
                public boolean isValidPosition(int tileX, int tileY) {
                    return SwampMudAreasWorldPreset.this.runCornerCheck(tileX - size.width / 2, tileY - size.height / 2, size.width, size.height, new WorldPreset.ValidTilePredicate(){

                        @Override
                        public boolean isValidPosition(int tileX, int tileY) {
                            return !generatorStack.isCaveRiverOrLava(tileX, tileY) && generatorStack.getLazyBiomeID(tileX, tileY) == BiomeRegistry.SWAMP.getID();
                        }
                    });
                }
            });
            if (tile == null || !(lg = new LinesGenerationWorldPreset(tile.x + size.width / 2, tile.y + size.height / 2).addRandomArms(random, 5, 4.0f, 10.0f, 3.0f, 5.0f)).isWithinPresetRegionBounds(presetsRegion)) continue;
            presetsRegion.addPreset((WorldPreset)this, lg.getOccupiedTileRectangle(), "minibiomes", new LevelPresetsRegion.WorldPresetPlaceFunction(){

                @Override
                public void place(GameRandom random, Level level, PerformanceTimerManager timer) {
                    GameTile mudTile = TileRegistry.getTile(TileRegistry.mudID);
                    GameObject wildMushroom = ObjectRegistry.getObject("wildmushroom");
                    GameObject thorns = ObjectRegistry.getObject("thorns");
                    CellAutomaton ca = lg.doCellularAutomaton(random);
                    boolean addThorns = random.getChance(0.75f);
                    ca.forEachTile(level, (level1, tileX, tileY) -> {
                        level.setTile(tileX, tileY, mudTile.getID());
                        if (level.getObjectID(tileX, tileY) == 0) {
                            if (addThorns) {
                                if (random.getChance(0.85f) && thorns.canPlace(level, tileX, tileY, 0, false) == null) {
                                    thorns.placeObject(level, tileX, tileY, 0, false);
                                }
                            } else if (random.getChance(0.05f) && wildMushroom.canPlace(level, tileX, tileY, 0, false) == null) {
                                wildMushroom.placeObject(level, tileX, tileY, 0, false);
                            }
                        }
                    });
                }
            });
        }
    }

    public void generateExtraContent(GameRandom random, Level level, PerformanceTimerManager timer, LinesGenerationWorldPreset linesGeneration, CellAutomaton cellAutomaton) {
    }
}

