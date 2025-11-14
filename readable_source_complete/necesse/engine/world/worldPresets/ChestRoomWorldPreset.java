/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.worldPresets;

import java.awt.Dimension;
import java.awt.Point;
import java.util.concurrent.atomic.AtomicInteger;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.biomeGenerator.BiomeGeneratorStack;
import necesse.engine.world.worldPresets.LevelPresetsRegion;
import necesse.engine.world.worldPresets.WorldPreset;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.presets.RandomCaveChestRoom;

public class ChestRoomWorldPreset
extends WorldPreset {
    protected Dimension size = new Dimension(7, 7);

    @Override
    public boolean shouldAddToRegion(LevelPresetsRegion presetsRegion) {
        return presetsRegion.identifier.equals(LevelIdentifier.CAVE_IDENTIFIER) || presetsRegion.identifier.equals(LevelIdentifier.DEEP_CAVE_IDENTIFIER);
    }

    @Override
    public void addToRegion(GameRandom random, LevelPresetsRegion presetsRegion, final BiomeGeneratorStack generatorStack, PerformanceTimerManager performanceTimer) {
        block3: {
            block2: {
                if (!presetsRegion.identifier.equals(LevelIdentifier.CAVE_IDENTIFIER)) break block2;
                int total = ChestRoomWorldPreset.getTotalPoints(random, presetsRegion, 0.05f);
                for (int i = 0; i < total; ++i) {
                    final Point tile = ChestRoomWorldPreset.findRandomPresetTile(random, presetsRegion, 20, this.size, "loot", new WorldPreset.ValidTilePredicate(){

                        @Override
                        public boolean isValidPosition(int tileX, int tileY) {
                            return ChestRoomWorldPreset.this.runCornerCheck(tileX, tileY, ChestRoomWorldPreset.this.size.width, ChestRoomWorldPreset.this.size.height, (cornerTileX, cornerTileY) -> !generatorStack.isCaveRiverOrLava(cornerTileX, cornerTileY));
                        }
                    });
                    if (tile == null) continue;
                    final int finalI = i;
                    presetsRegion.addPreset((WorldPreset)this, tile.x, tile.y, this.size, "loot", new LevelPresetsRegion.WorldPresetPlaceFunction(){

                        @Override
                        public void place(GameRandom random, Level level, PerformanceTimerManager timer) {
                            WorldPreset.ensureRegionsAreGenerated(level, tile.x, tile.y, ChestRoomWorldPreset.this.size.width, ChestRoomWorldPreset.this.size.height);
                            Biome biome = level.getBiome(tile.x + ChestRoomWorldPreset.this.size.width / 2, tile.y + ChestRoomWorldPreset.this.size.height / 2);
                            RandomCaveChestRoom chestRoom = biome.getNewCaveChestRoomPreset(random, new AtomicInteger(finalI));
                            if (chestRoom == null) {
                                return;
                            }
                            chestRoom.applyToLevel(level, tile.x, tile.y);
                        }
                    });
                }
                break block3;
            }
            if (!presetsRegion.identifier.equals(LevelIdentifier.DEEP_CAVE_IDENTIFIER)) break block3;
            int total = ChestRoomWorldPreset.getTotalPoints(random, presetsRegion, 0.05f);
            for (int i = 0; i < total; ++i) {
                final Point tile = ChestRoomWorldPreset.findRandomPresetTile(random, presetsRegion, 20, this.size, "loot", new WorldPreset.ValidTilePredicate(){

                    @Override
                    public boolean isValidPosition(int tileX, int tileY) {
                        return ChestRoomWorldPreset.this.runCornerCheck(tileX, tileY, ChestRoomWorldPreset.this.size.width, ChestRoomWorldPreset.this.size.height, (cornerTileX, cornerTileY) -> !generatorStack.isDeepCaveLava(cornerTileX, cornerTileY));
                    }
                });
                if (tile == null) continue;
                final int finalI = i;
                presetsRegion.addPreset((WorldPreset)this, tile.x, tile.y, this.size, "loot", new LevelPresetsRegion.WorldPresetPlaceFunction(){

                    @Override
                    public void place(GameRandom random, Level level, PerformanceTimerManager timer) {
                        WorldPreset.ensureRegionsAreGenerated(level, tile.x, tile.y, ChestRoomWorldPreset.this.size.width, ChestRoomWorldPreset.this.size.height);
                        Biome biome = level.getBiome(tile.x + ChestRoomWorldPreset.this.size.width / 2, tile.y + ChestRoomWorldPreset.this.size.height / 2);
                        RandomCaveChestRoom chestRoom = biome.getNewDeepCaveChestRoomPreset(random, new AtomicInteger(finalI));
                        if (chestRoom == null) {
                            return;
                        }
                        chestRoom.applyToLevel(level, tile.x, tile.y);
                    }
                });
            }
        }
    }
}

