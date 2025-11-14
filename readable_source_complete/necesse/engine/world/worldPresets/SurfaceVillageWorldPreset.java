/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.worldPresets;

import java.awt.Dimension;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.TicketSystemList;
import necesse.engine.world.biomeGenerator.BiomeGeneratorStack;
import necesse.engine.world.worldPresets.LevelPresetsRegion;
import necesse.engine.world.worldPresets.WorldPreset;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.generationModules.VillageGeneration;
import necesse.level.maps.levelData.OneWorldNPCVillageData;
import necesse.level.maps.presets.set.VillageSet;

public class SurfaceVillageWorldPreset
extends WorldPreset {
    @Override
    public boolean shouldAddToRegion(LevelPresetsRegion presetsRegion) {
        return presetsRegion.identifier.equals(LevelIdentifier.SURFACE_IDENTIFIER);
    }

    @Override
    public void addToRegion(GameRandom random, LevelPresetsRegion presetsRegion, final BiomeGeneratorStack generatorStack, PerformanceTimerManager performanceTimer) {
        int total = SurfaceVillageWorldPreset.getTotalPoints(random, presetsRegion, 0.001f);
        for (int i = 0; i < total; ++i) {
            final int size = random.getIntBetween(60, 120);
            Dimension dimension = new Dimension(size, size);
            final TicketSystemList villageSets = new TicketSystemList();
            final Point tile = SurfaceVillageWorldPreset.findRandomPresetTile(random, presetsRegion, 200, dimension, new String[]{"villagespadding", "villages"}, new WorldPreset.ValidTilePredicate(){

                @Override
                public boolean isValidPosition(int tileX, int tileY) {
                    boolean waterCheck = SurfaceVillageWorldPreset.this.runGridCheck(tileX, tileY, size, size, 16, new WorldPreset.ValidTilePredicate(){

                        @Override
                        public boolean isValidPosition(int tileX, int tileY) {
                            return !generatorStack.isSurfaceOceanOrRiver(tileX, tileY);
                        }
                    });
                    if (!waterCheck) {
                        return false;
                    }
                    final HashMap biomeFrequencies = new HashMap();
                    boolean validBiomes = SurfaceVillageWorldPreset.this.runGridCheck(tileX, tileY, size, size, 16, new WorldPreset.ValidTilePredicate(){

                        @Override
                        public boolean isValidPosition(int tileX, int tileY) {
                            Biome biome = generatorStack.getLazyBiome(tileX, tileY);
                            if (biome.getVillageSets() == null) {
                                return false;
                            }
                            biomeFrequencies.compute(biome.getID(), (key, value) -> value == null ? 1 : value + 1);
                            return true;
                        }
                    });
                    if (validBiomes) {
                        for (Map.Entry entry : biomeFrequencies.entrySet()) {
                            Biome biome = BiomeRegistry.getBiome((Integer)entry.getKey());
                            VillageSet[] biomeVillageSets = biome.getVillageSets();
                            if (biomeVillageSets.length <= 0) continue;
                            int ticketsPerSet = Math.max(1, (Integer)entry.getValue() / biomeVillageSets.length);
                            for (VillageSet biomeVillageSet : biomeVillageSets) {
                                villageSets.addObject(ticketsPerSet, biomeVillageSet);
                            }
                        }
                    }
                    if (villageSets.isEmpty()) {
                        return false;
                    }
                    return validBiomes;
                }
            });
            if (tile == null) continue;
            presetsRegion.addPreset((WorldPreset)this, tile.x, tile.y, dimension, "villages", new LevelPresetsRegion.WorldPresetPlaceFunction(){

                @Override
                public void place(GameRandom random, Level level, PerformanceTimerManager timer) {
                    OneWorldNPCVillageData villageData = OneWorldNPCVillageData.getVillageData(level, true);
                    new VillageGeneration(level, size, villageSets, random).addStandardPresets().generate(villageData, tile.x + size / 2, tile.y + size / 2);
                }
            });
        }
    }
}

