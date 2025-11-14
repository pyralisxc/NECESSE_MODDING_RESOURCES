/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.worldPresets;

import java.awt.Dimension;
import java.awt.Point;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.biomeGenerator.BiomeGeneratorStack;
import necesse.engine.world.worldPresets.LevelPresetsRegion;
import necesse.engine.world.worldPresets.LinesGenerationWorldPreset;
import necesse.engine.world.worldPresets.WorldPreset;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.generationModules.CellAutomaton;
import necesse.level.maps.generationModules.LinesGeneration;

public class SwampCaveSporesWorldPreset
extends WorldPreset {
    @Override
    public boolean shouldAddToRegion(LevelPresetsRegion presetsRegion) {
        return presetsRegion.identifier.equals(LevelIdentifier.CAVE_IDENTIFIER) && presetsRegion.hasAnyOfBiome(BiomeRegistry.SWAMP.getID());
    }

    @Override
    public void addToRegion(GameRandom random, LevelPresetsRegion presetsRegion, final BiomeGeneratorStack generatorStack, PerformanceTimerManager performanceTimer) {
        int total = SwampCaveSporesWorldPreset.getTotalBiomePoints(random, presetsRegion, BiomeRegistry.SWAMP, 0.02f);
        for (int i = 0; i < total; ++i) {
            LinesGenerationWorldPreset lg;
            final Dimension size = new Dimension(24, 24);
            Point tile = SwampCaveSporesWorldPreset.findRandomBiomePresetTile(random, presetsRegion, generatorStack, BiomeRegistry.SWAMP, 20, size, "minibiomes", new WorldPreset.ValidTilePredicate(){

                @Override
                public boolean isValidPosition(int tileX, int tileY) {
                    return SwampCaveSporesWorldPreset.this.runCornerCheck(tileX - size.width / 2, tileY - size.height / 2, size.width, size.height, new WorldPreset.ValidTilePredicate(){

                        @Override
                        public boolean isValidPosition(int tileX, int tileY) {
                            return !generatorStack.isCaveRiverOrLava(tileX, tileY) && generatorStack.getLazyBiomeID(tileX, tileY) == BiomeRegistry.SWAMP.getID();
                        }
                    });
                }
            });
            if (tile == null || !(lg = new LinesGenerationWorldPreset(tile.x + size.width / 2, tile.y + size.height / 2).addRandomArms(random, 4, 8.0f, 12.0f, 8.0f, 12.0f)).isWithinPresetRegionBounds(presetsRegion)) continue;
            presetsRegion.addPreset((WorldPreset)this, lg.getOccupiedTileRectangle(), "minibiomes", new LevelPresetsRegion.WorldPresetPlaceFunction(){

                @Override
                public void place(GameRandom random, Level level, PerformanceTimerManager timer) {
                    GameObject swampSpore = ObjectRegistry.getObject("swampspore");
                    GameObject swampGrass = ObjectRegistry.getObject("swampgrass");
                    GameObject swampCrate = ObjectRegistry.getObject("swampcrate");
                    CellAutomaton ca = lg.doCellularAutomaton(random);
                    ca.forEachTile(level, (level1, tileX, tileY) -> {
                        if (random.getChance(0.03f)) {
                            level.setObject(tileX, tileY, swampCrate.getID());
                        } else if (random.getChance(0.6f)) {
                            level.setObject(tileX, tileY, swampGrass.getID());
                        } else {
                            level.setObject(tileX, tileY, 0);
                        }
                    });
                    for (LinesGeneration line : lg.getLines()) {
                        swampSpore.placeObject(level, line.x2, line.y2, 0, false);
                    }
                }
            });
        }
    }

    public void generateExtraContent(GameRandom random, Level level, PerformanceTimerManager timer, LinesGenerationWorldPreset linesGeneration, CellAutomaton cellAutomaton) {
    }
}

