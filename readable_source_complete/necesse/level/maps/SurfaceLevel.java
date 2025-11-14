/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps;

import java.util.Comparator;
import java.util.TreeSet;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.WorldEntity;
import necesse.level.maps.BiomeGeneratorStackLevel;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.regionSystem.Region;

public class SurfaceLevel
extends BiomeGeneratorStackLevel {
    public SurfaceLevel(LevelIdentifier identifier, int width, int height, WorldEntity worldEntity) {
        super(identifier, width, height, worldEntity);
    }

    public SurfaceLevel(LevelIdentifier identifier, int width, int height, WorldEntity worldEntity, int seed) {
        super(identifier, width, height, worldEntity, seed);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void generateRegion(Region region) {
        super.generateRegion(region);
        this.startDirtyRegionTracking();
        int presetGenerationUniqueID = this.getWorldEntity().startPresetGenerationInRegion(region, this.seed);
        try {
            TreeSet<Biome> foundBiomes = new TreeSet<Biome>(Comparator.comparingInt(Biome::getID));
            for (int regionTileX = 0; regionTileX < region.tileLayer.region.tileWidth; ++regionTileX) {
                for (int regionTileY = 0; regionTileY < region.tileLayer.region.tileHeight; ++regionTileY) {
                    boolean isBeach;
                    int tileX = regionTileX + region.tileXOffset;
                    int tileY = regionTileY + region.tileYOffset;
                    Biome biome = this.generatorStack.getExpensiveBiome(tileX, tileY);
                    Biome spreadBiome = this.generatorStack.getSpreadBiome(tileX, tileY);
                    region.biomeLayer.setBiomeByRegion(regionTileX, regionTileY, biome.getID());
                    foundBiomes.add(spreadBiome);
                    boolean isWater = this.generatorStack.isSurfaceExpensiveWater(tileX, tileY);
                    if (isWater) {
                        region.tileLayer.setTileByRegion(regionTileX, regionTileY, spreadBiome.getGenerationWaterTileID());
                        continue;
                    }
                    boolean bl = isBeach = this.generatorStack.isSurfaceOceanOrRiverBeach(tileX, tileY) && !biome.doesGenerationPreventsBeachTiles();
                    if (isBeach) {
                        region.tileLayer.setTileByRegion(regionTileX, regionTileY, spreadBiome.getGenerationBeachTileID());
                        continue;
                    }
                    region.tileLayer.setTileByRegion(regionTileX, regionTileY, spreadBiome.getGenerationTerrainTileID());
                }
            }
            for (Biome biome : foundBiomes) {
                biome.generateRegionSurfaceTerrain(region, this.generatorStack, this.generatorStack.getNewRegionRandom(region));
            }
        }
        finally {
            this.getWorldEntity().runPresetGenerationInRegion(presetGenerationUniqueID, region, this.seed);
            this.removeDirtyRegion(region.regionX, region.regionY);
        }
    }

    @Override
    public void onRegionGenerated(Region region, boolean skipGenerateForced) {
        super.onRegionGenerated(region, skipGenerateForced);
        region.checkGenerationValid();
    }
}

