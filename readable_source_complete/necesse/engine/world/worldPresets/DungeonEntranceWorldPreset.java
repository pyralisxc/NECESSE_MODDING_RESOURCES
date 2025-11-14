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
import necesse.level.maps.presets.DungeonEntrancePreset;
import necesse.level.maps.presets.PresetUtils;

public class DungeonEntranceWorldPreset
extends WorldPreset {
    protected Dimension size = new Dimension(20, 20);

    @Override
    public boolean shouldAddToRegion(LevelPresetsRegion presetsRegion) {
        return presetsRegion.identifier.equals(LevelIdentifier.SURFACE_IDENTIFIER);
    }

    @Override
    public void addToRegion(GameRandom random, LevelPresetsRegion presetsRegion, final BiomeGeneratorStack generatorStack, PerformanceTimerManager performanceTimer) {
        int total = DungeonEntranceWorldPreset.getTotalBiomePoints(random, presetsRegion, BiomeRegistry.FOREST, 0.002f);
        for (int i = 0; i < total; ++i) {
            final Point tile = DungeonEntranceWorldPreset.findRandomBiomePresetTile(random, presetsRegion, generatorStack, BiomeRegistry.FOREST, 20, this.size, new String[]{"loot", "villages"}, new WorldPreset.ValidTilePredicate(){

                @Override
                public boolean isValidPosition(int tileX, int tileY) {
                    return DungeonEntranceWorldPreset.this.runCornerCheck(tileX, tileY, DungeonEntranceWorldPreset.this.size.width, DungeonEntranceWorldPreset.this.size.height, new WorldPreset.ValidTilePredicate(){

                        @Override
                        public boolean isValidPosition(int tileX, int tileY) {
                            return generatorStack.getLazyBiomeID(tileX, tileY) == BiomeRegistry.FOREST.getID() && !generatorStack.isSurfaceOceanOrRiver(tileX, tileY);
                        }
                    });
                }
            });
            if (tile == null) continue;
            presetsRegion.addPreset((WorldPreset)this, tile.x, tile.y, this.size, new String[]{"loot", "villages"}, new LevelPresetsRegion.WorldPresetPlaceFunction(){

                @Override
                public void place(GameRandom random, Level level, PerformanceTimerManager timer) {
                    WorldPreset.ensureRegionsAreGenerated(level, tile.x, tile.y, DungeonEntranceWorldPreset.this.size.width, DungeonEntranceWorldPreset.this.size.height);
                    DungeonEntrancePreset preset = new DungeonEntrancePreset(random);
                    PresetUtils.clearMobsInPreset(preset, level, tile.x, tile.y);
                    preset.applyToLevel(level, tile.x, tile.y);
                }
            }).setRemoveIfWithinSpawnRegionRange(-1);
        }
    }
}

