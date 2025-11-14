/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.regionSystem.managers;

import necesse.engine.registries.BiomeRegistry;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.regionSystem.Region;

public class BiomeLayerManager {
    protected final Level level;

    public BiomeLayerManager(Level level) {
        this.level = level;
    }

    public int getBiomeID(int tileX, int tileY) {
        Region region = this.level.regionManager.getRegionByTile(tileX, tileY, false);
        if (region == null) {
            return BiomeRegistry.UNKNOWN.getID();
        }
        return region.biomeLayer.getBiomeIDByRegion(tileX - region.tileXOffset, tileY - region.tileYOffset);
    }

    public void setBiome(int tileX, int tileY, int biomeID) {
        Region region = this.level.regionManager.getRegionByTile(tileX, tileY, true);
        if (region == null) {
            return;
        }
        region.biomeLayer.setBiomeByRegion(tileX - region.tileXOffset, tileY - region.tileYOffset, biomeID);
    }

    public Biome getBiome(int tileX, int tileY) {
        return BiomeRegistry.getBiome(this.getBiomeID(tileX, tileY));
    }
}

