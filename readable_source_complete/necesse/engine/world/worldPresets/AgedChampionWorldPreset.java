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
import necesse.level.maps.presets.AgedChampionArenaPreset;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.PresetUtils;

public class AgedChampionWorldPreset
extends WorldPreset {
    protected Dimension size = new Dimension(17, 17);

    @Override
    public boolean shouldAddToRegion(LevelPresetsRegion presetsRegion) {
        return presetsRegion.identifier.equals(LevelIdentifier.DEEP_CAVE_IDENTIFIER) && presetsRegion.hasAnyOfBiome(BiomeRegistry.SWAMP.getID());
    }

    @Override
    public void addToRegion(GameRandom random, LevelPresetsRegion presetsRegion, final BiomeGeneratorStack generatorStack, PerformanceTimerManager performanceTimer) {
        int total = AgedChampionWorldPreset.getTotalBiomePoints(random, presetsRegion, BiomeRegistry.SWAMP, 0.006f);
        for (int i = 0; i < total; ++i) {
            final Point tile = AgedChampionWorldPreset.findRandomBiomePresetTile(random, presetsRegion, generatorStack, BiomeRegistry.SWAMP, 100, this.size, "loot", new WorldPreset.ValidTilePredicate(){

                @Override
                public boolean isValidPosition(int tileX, int tileY) {
                    return AgedChampionWorldPreset.this.runCornerCheck(tileX, tileY, AgedChampionWorldPreset.this.size.width, AgedChampionWorldPreset.this.size.height, new WorldPreset.ValidTilePredicate(){

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
                    WorldPreset.ensureRegionsAreGenerated(level, tile.x, tile.y, AgedChampionWorldPreset.this.size.width, AgedChampionWorldPreset.this.size.height);
                    Preset preset = new AgedChampionArenaPreset();
                    preset = PresetUtils.randomizeXMirror(preset, random);
                    preset = PresetUtils.randomizeYMirror(preset, random);
                    preset.applyToLevel(level, tile.x, tile.y);
                }
            });
        }
    }
}

