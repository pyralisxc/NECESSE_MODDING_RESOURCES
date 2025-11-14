/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.worldPresets;

import java.awt.Dimension;
import java.awt.Point;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.biomeGenerator.BiomeGeneratorStack;
import necesse.engine.world.worldPresets.LevelPresetsRegion;
import necesse.engine.world.worldPresets.WorldPreset;
import necesse.level.maps.Level;
import necesse.level.maps.presets.PresetUtils;
import necesse.level.maps.presets.RandomRuinsPreset;

public class SurfaceRuinsWorldPreset
extends WorldPreset {
    protected Dimension size = new Dimension(9, 9);

    @Override
    public boolean shouldAddToRegion(LevelPresetsRegion presetsRegion) {
        return presetsRegion.identifier.equals(LevelIdentifier.SURFACE_IDENTIFIER);
    }

    @Override
    public void addToRegion(GameRandom random, LevelPresetsRegion presetsRegion, final BiomeGeneratorStack generatorStack, PerformanceTimerManager performanceTimer) {
        int total = SurfaceRuinsWorldPreset.getTotalPoints(random, presetsRegion, 0.01f);
        for (int i = 0; i < total; ++i) {
            final Point tile = SurfaceRuinsWorldPreset.findRandomPresetTile(random, presetsRegion, 20, this.size, new String[]{"loot", "villages"}, new WorldPreset.ValidTilePredicate(){

                @Override
                public boolean isValidPosition(int tileX, int tileY) {
                    return SurfaceRuinsWorldPreset.this.runCornerCheck(tileX, tileY, SurfaceRuinsWorldPreset.this.size.width, SurfaceRuinsWorldPreset.this.size.height, new WorldPreset.ValidTilePredicate(){

                        @Override
                        public boolean isValidPosition(int tileX, int tileY) {
                            return !generatorStack.isSurfaceExpensiveWater(tileX, tileY);
                        }
                    });
                }
            });
            if (tile == null) continue;
            presetsRegion.addPreset((WorldPreset)this, tile.x, tile.y, this.size, "loot", new LevelPresetsRegion.WorldPresetPlaceFunction(){

                @Override
                public void place(GameRandom random, Level level, PerformanceTimerManager timer) {
                    WorldPreset.ensureRegionsAreGenerated(level, tile.x, tile.y, SurfaceRuinsWorldPreset.this.size.width, SurfaceRuinsWorldPreset.this.size.height);
                    RandomRuinsPreset ruins = new RandomRuinsPreset(random);
                    PresetUtils.clearMobsInPreset(ruins, level, tile.x, tile.y);
                    ruins.applyToLevel(level, tile.x, tile.y);
                }
            }).setRemoveIfWithinSpawnRegionRange(1);
        }
    }
}

