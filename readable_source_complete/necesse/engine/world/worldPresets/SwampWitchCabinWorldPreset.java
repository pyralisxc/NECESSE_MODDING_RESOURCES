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
import necesse.level.maps.presets.PresetUtils;
import necesse.level.maps.presets.worldStructures.WitchCabinMadsPreset;

public class SwampWitchCabinWorldPreset
extends WorldPreset {
    protected Dimension size = new Dimension(14, 15);

    @Override
    public boolean shouldAddToRegion(LevelPresetsRegion presetsRegion) {
        return presetsRegion.identifier.equals(LevelIdentifier.SURFACE_IDENTIFIER) && presetsRegion.hasAnyOfBiome(BiomeRegistry.SWAMP.getID());
    }

    @Override
    public void addToRegion(GameRandom random, LevelPresetsRegion presetsRegion, final BiomeGeneratorStack generatorStack, PerformanceTimerManager performanceTimer) {
        int total = SwampWitchCabinWorldPreset.getTotalBiomePoints(random, presetsRegion, BiomeRegistry.SWAMP, 0.004f);
        for (int i = 0; i < total; ++i) {
            final Point tile = SwampWitchCabinWorldPreset.findRandomBiomePresetTile(random, presetsRegion, generatorStack, BiomeRegistry.SWAMP, 50, this.size, new String[]{"loot", "villages"}, new WorldPreset.ValidTilePredicate(){

                @Override
                public boolean isValidPosition(int tileX, int tileY) {
                    return SwampWitchCabinWorldPreset.this.runCornerCheck(tileX, tileY, SwampWitchCabinWorldPreset.this.size.width, SwampWitchCabinWorldPreset.this.size.height, new WorldPreset.ValidTilePredicate(){

                        @Override
                        public boolean isValidPosition(int tileX, int tileY) {
                            return !generatorStack.isSurfaceExpensiveWater(tileX, tileY) && generatorStack.getLazyBiomeID(tileX, tileY) == BiomeRegistry.SWAMP.getID();
                        }
                    });
                }
            });
            if (tile == null) continue;
            presetsRegion.addPreset((WorldPreset)this, tile.x, tile.y, this.size, new String[]{"loot", "villages"}, new LevelPresetsRegion.WorldPresetPlaceFunction(){

                @Override
                public void place(GameRandom random, Level level, PerformanceTimerManager timer) {
                    WorldPreset.ensureRegionsAreGenerated(level, tile.x, tile.y, SwampWitchCabinWorldPreset.this.size.width, SwampWitchCabinWorldPreset.this.size.height);
                    WitchCabinMadsPreset preset = new WitchCabinMadsPreset(random);
                    PresetUtils.clearMobsInPreset(preset, level, tile.x, tile.y);
                    preset.applyToLevel(level, tile.x, tile.y);
                }
            }).setRemoveIfWithinSpawnRegionRange(1);
        }
    }
}

