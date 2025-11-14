/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.worldPresets;

import java.awt.Dimension;
import java.awt.Point;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.biomeGenerator.BiomeGeneratorStack;
import necesse.engine.world.worldPresets.LevelPresetsRegion;
import necesse.engine.world.worldPresets.WorldPreset;
import necesse.level.maps.Level;
import necesse.level.maps.presets.CavelingOasisPreset;

public class DesertCavelingOasisWorldPreset
extends WorldPreset {
    protected Dimension size = new Dimension(20, 17);

    @Override
    public boolean shouldAddToRegion(LevelPresetsRegion presetsRegion) {
        return presetsRegion.identifier.equals(LevelIdentifier.CAVE_IDENTIFIER) && presetsRegion.hasAnyOfBiome(BiomeRegistry.DESERT.getID());
    }

    @Override
    public void addToRegion(GameRandom random, LevelPresetsRegion presetsRegion, final BiomeGeneratorStack generatorStack, PerformanceTimerManager performanceTimer) {
        int total = DesertCavelingOasisWorldPreset.getTotalBiomePoints(random, presetsRegion, BiomeRegistry.DESERT, 0.004f);
        for (int i = 0; i < total; ++i) {
            final Point tile = DesertCavelingOasisWorldPreset.findRandomBiomePresetTile(random, presetsRegion, generatorStack, BiomeRegistry.DESERT, 50, this.size, "loot", new WorldPreset.ValidTilePredicate(){

                @Override
                public boolean isValidPosition(int tileX, int tileY) {
                    return DesertCavelingOasisWorldPreset.this.runCornerCheck(tileX, tileY, DesertCavelingOasisWorldPreset.this.size.width, DesertCavelingOasisWorldPreset.this.size.height, new WorldPreset.ValidTilePredicate(){

                        @Override
                        public boolean isValidPosition(int tileX, int tileY) {
                            return !generatorStack.isCaveRiverOrLava(tileX, tileY) && generatorStack.getLazyBiomeID(tileX, tileY) == BiomeRegistry.DESERT.getID();
                        }
                    });
                }
            });
            if (tile == null) continue;
            presetsRegion.addPreset((WorldPreset)this, tile.x, tile.y, this.size, "loot", new LevelPresetsRegion.WorldPresetPlaceFunction(){

                @Override
                public void place(GameRandom random, Level level, PerformanceTimerManager timer) {
                    WorldPreset.ensureRegionsAreGenerated(level, tile.x, tile.y, DesertCavelingOasisWorldPreset.this.size.width, DesertCavelingOasisWorldPreset.this.size.height);
                    new CavelingOasisPreset(random).applyToLevel(level, tile.x, tile.y);
                }
            });
        }
    }
}

