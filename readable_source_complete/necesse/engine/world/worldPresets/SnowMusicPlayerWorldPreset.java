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
import necesse.level.maps.presets.MusicPlayerPreset2;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.PresetUtils;

public class SnowMusicPlayerWorldPreset
extends WorldPreset {
    protected Dimension size = new Dimension(17, 28);

    @Override
    public boolean shouldAddToRegion(LevelPresetsRegion presetsRegion) {
        return presetsRegion.identifier.equals(LevelIdentifier.DEEP_CAVE_IDENTIFIER) && presetsRegion.hasAnyOfBiome(BiomeRegistry.SNOW.getID());
    }

    @Override
    public void addToRegion(GameRandom random, LevelPresetsRegion presetsRegion, final BiomeGeneratorStack generatorStack, PerformanceTimerManager performanceTimer) {
        int total = SnowMusicPlayerWorldPreset.getTotalBiomePoints(random, presetsRegion, BiomeRegistry.SNOW, 0.003f);
        for (int i = 0; i < total; ++i) {
            final Point tile = SnowMusicPlayerWorldPreset.findRandomBiomePresetTile(random, presetsRegion, generatorStack, BiomeRegistry.SNOW, 20, this.size, "loot", new WorldPreset.ValidTilePredicate(){

                @Override
                public boolean isValidPosition(int tileX, int tileY) {
                    return SnowMusicPlayerWorldPreset.this.runCornerCheck(tileX, tileY, SnowMusicPlayerWorldPreset.this.size.width, SnowMusicPlayerWorldPreset.this.size.height, new WorldPreset.ValidTilePredicate(){

                        @Override
                        public boolean isValidPosition(int tileX, int tileY) {
                            return !generatorStack.isDeepCaveLava(tileX, tileY);
                        }
                    });
                }
            });
            if (tile == null) continue;
            presetsRegion.addPreset((WorldPreset)this, tile.x, tile.y, this.size, "loot", new LevelPresetsRegion.WorldPresetPlaceFunction(){

                @Override
                public void place(GameRandom random, Level level, PerformanceTimerManager timer) {
                    WorldPreset.ensureRegionsAreGenerated(level, tile.x, tile.y, SnowMusicPlayerWorldPreset.this.size.width, SnowMusicPlayerWorldPreset.this.size.height);
                    Preset preset = new MusicPlayerPreset2(random);
                    preset = PresetUtils.randomizeXMirror(preset, random);
                    preset = PresetUtils.randomizeYMirror(preset, random);
                    preset.applyToLevel(level, tile.x, tile.y);
                }
            });
        }
    }
}

