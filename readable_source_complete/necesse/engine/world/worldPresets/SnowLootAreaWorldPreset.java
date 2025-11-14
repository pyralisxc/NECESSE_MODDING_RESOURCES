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
import necesse.level.maps.presets.RandomLootAreaPreset;

public class SnowLootAreaWorldPreset
extends WorldPreset {
    protected int size = 15;

    @Override
    public boolean shouldAddToRegion(LevelPresetsRegion presetsRegion) {
        return presetsRegion.identifier.equals(LevelIdentifier.CAVE_IDENTIFIER) && presetsRegion.hasAnyOfBiome(BiomeRegistry.SNOW.getID());
    }

    @Override
    public void addToRegion(GameRandom random, LevelPresetsRegion presetsRegion, final BiomeGeneratorStack generatorStack, PerformanceTimerManager performanceTimer) {
        int total = SnowLootAreaWorldPreset.getTotalBiomePoints(random, presetsRegion, BiomeRegistry.SNOW, 0.05f);
        for (int i = 0; i < total; ++i) {
            final Point tile = SnowLootAreaWorldPreset.findRandomBiomePresetTile(random, presetsRegion, generatorStack, BiomeRegistry.SNOW, 20, new Dimension(this.size, this.size), "loot", new WorldPreset.ValidTilePredicate(){

                @Override
                public boolean isValidPosition(int tileX, int tileY) {
                    return !generatorStack.isCaveRiverOrLava(tileX + SnowLootAreaWorldPreset.this.size / 2, tileY + SnowLootAreaWorldPreset.this.size / 2);
                }
            });
            if (tile == null) continue;
            presetsRegion.addPreset((WorldPreset)this, tile.x, tile.y, new Dimension(this.size, this.size), "loot", new LevelPresetsRegion.WorldPresetPlaceFunction(){

                @Override
                public void place(GameRandom random, Level level, PerformanceTimerManager timer) {
                    WorldPreset.ensureRegionsAreGenerated(level, tile.x, tile.y, SnowLootAreaWorldPreset.this.size, SnowLootAreaWorldPreset.this.size);
                    RandomLootAreaPreset lootArea = new RandomLootAreaPreset(random, SnowLootAreaWorldPreset.this.size, "snowstonecolumn", "frozendwarf");
                    lootArea.applyToLevel(level, tile.x, tile.y);
                }
            });
        }
    }
}

