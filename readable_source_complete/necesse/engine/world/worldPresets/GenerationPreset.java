/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.worldPresets;

import java.awt.Point;
import java.util.HashSet;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.biomeGenerator.BiomeGeneratorStack;
import necesse.engine.world.worldPresets.LevelPresetsRegion;
import necesse.engine.world.worldPresets.WorldPreset;
import necesse.level.maps.biomes.Biome;

public abstract class GenerationPreset<T extends Point> {
    private final HashSet<Integer> biomeIDs = new HashSet();
    private final GeneratorStackCheck isSurfaceWaterCheck;
    private final GeneratorStackCheck isSurfaceWaterOrBeachCheck;

    public GenerationPreset(Biome ... biomes) {
        boolean hasSwamp = false;
        for (Biome biome : biomes) {
            this.biomeIDs.add(biome.getID());
            hasSwamp |= biome == BiomeRegistry.SWAMP;
        }
        if (hasSwamp) {
            this.isSurfaceWaterCheck = BiomeGeneratorStack::isSurfaceExpensiveWater;
            this.isSurfaceWaterOrBeachCheck = BiomeGeneratorStack::isSurfaceExpensiveWaterOrBeach;
        } else {
            this.isSurfaceWaterCheck = BiomeGeneratorStack::isSurfaceOceanOrRiver;
            this.isSurfaceWaterOrBeachCheck = BiomeGeneratorStack::isSurfaceOceanOrRiverOrBeach;
        }
    }

    public Iterable<Integer> getBiomeIDs() {
        return this.biomeIDs;
    }

    public int getBiomeCount() {
        return this.biomeIDs.size();
    }

    public boolean isValidBiome(int biomeID) {
        return this.biomeIDs.isEmpty() || this.biomeIDs.contains(biomeID);
    }

    public Point getRandomBiomeRegion(GameRandom random, LevelPresetsRegion presetsRegion) {
        if (this.biomeIDs.isEmpty()) {
            return new Point(presetsRegion.worldRegion.startLevelRegionX + random.nextInt(64), presetsRegion.worldRegion.startLevelRegionY + random.nextInt(64));
        }
        return presetsRegion.getRandomBiomeRegion(random, this.biomeIDs);
    }

    public void init() {
    }

    public abstract T findRandomTile(GameRandom var1, WorldPreset var2, LevelPresetsRegion var3, BiomeGeneratorStack var4);

    public abstract void addToRegion(GameRandom var1, WorldPreset var2, LevelPresetsRegion var3, BiomeGeneratorStack var4, T var5, PerformanceTimerManager var6);

    public final boolean run(GameRandom random, WorldPreset worldPreset, LevelPresetsRegion presetsRegion, BiomeGeneratorStack generatorStack, PerformanceTimerManager performanceTimer) {
        T tile = this.findRandomTile(random, worldPreset, presetsRegion, generatorStack);
        if (tile == null) {
            return false;
        }
        this.addToRegion(random, worldPreset, presetsRegion, generatorStack, tile, performanceTimer);
        return true;
    }

    public boolean isValidBiome(BiomeGeneratorStack generatorStack, int tileX, int tileY) {
        return this.isValidBiome(generatorStack.getLazyBiomeID(tileX, tileY));
    }

    public boolean isWaterOrLava(LevelPresetsRegion presetsRegion, BiomeGeneratorStack generatorStack, int tileX, int tileY) {
        if (presetsRegion.identifier.equals(LevelIdentifier.CAVE_IDENTIFIER)) {
            return generatorStack.isCaveRiverOrLava(tileX, tileY);
        }
        if (presetsRegion.identifier.equals(LevelIdentifier.DEEP_CAVE_IDENTIFIER)) {
            return generatorStack.isDeepCaveLava(tileX, tileY);
        }
        return this.isSurfaceWaterCheck.isWater(generatorStack, tileX, tileY);
    }

    public boolean isBeach(LevelPresetsRegion presetsRegion, BiomeGeneratorStack generatorStack, int tileX, int tileY) {
        if (presetsRegion.identifier.equals(LevelIdentifier.SURFACE_IDENTIFIER)) {
            return generatorStack.isSurfaceOceanOrRiverBeach(tileX, tileY);
        }
        return false;
    }

    public boolean isWaterOrLavaOrBeach(LevelPresetsRegion presetsRegion, BiomeGeneratorStack generatorStack, int tileX, int tileY) {
        if (presetsRegion.identifier.equals(LevelIdentifier.CAVE_IDENTIFIER)) {
            return generatorStack.isCaveRiverOrLava(tileX, tileY);
        }
        if (presetsRegion.identifier.equals(LevelIdentifier.DEEP_CAVE_IDENTIFIER)) {
            return generatorStack.isDeepCaveLava(tileX, tileY);
        }
        return this.isSurfaceWaterOrBeachCheck.isWater(generatorStack, tileX, tileY);
    }

    private static interface GeneratorStackCheck {
        public boolean isWater(BiomeGeneratorStack var1, int var2, int var3);
    }
}

