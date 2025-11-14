/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.regionSystem.layers;

import java.awt.Point;
import java.util.HashSet;
import necesse.engine.network.server.Server;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.VersionMigration;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.regionSystem.Region;
import necesse.level.maps.regionSystem.layers.UnsignedShortRegionLayer;

public class BiomeRegionLayer
extends UnsignedShortRegionLayer {
    public BiomeRegionLayer(Region region) {
        super(region);
    }

    @Override
    public void init() {
    }

    public void setBiomeByRegion(int regionTileX, int regionTileY, int biome) {
        this.setBiomeByRegion(regionTileX, regionTileY, biome, false);
    }

    public void setBiomeByRegion(int regionTileX, int regionTileY, int biome, boolean forceDontUpdate) {
        Biome newBiome;
        Biome oldBiome = this.getBiomeByRegion(regionTileX, regionTileY);
        this.set(regionTileX, regionTileY, (short)biome);
        int tileX = regionTileX + this.region.tileXOffset;
        int tileY = regionTileY + this.region.tileYOffset;
        if (this.region.isLoadingComplete() && this.level.isLoadingComplete() && !forceDontUpdate && oldBiome != (newBiome = this.getBiomeByRegion(regionTileX, regionTileY))) {
            this.level.biomeBlendingManager.updateBlends(tileX, tileY);
        }
        if (this.level.isClient()) {
            this.level.getClient().levelManager.updateMapTile(tileX, tileY);
        }
    }

    public int getBiomeIDByRegion(int regionTileX, int regionTileY) {
        return this.get(regionTileX, regionTileY);
    }

    public Biome getBiomeByRegion(int regionTileX, int regionTileY) {
        return BiomeRegistry.getBiome(this.getBiomeIDByRegion(regionTileX, regionTileY));
    }

    @Override
    public void addSaveData(SaveData save) {
        HashSet<Integer> usedBiomeIDs = new HashSet<Integer>();
        for (short id : this.data) {
            usedBiomeIDs.add(id & 0xFFFF);
        }
        String[] biomeIDs = VersionMigration.generateStringIDsArray(usedBiomeIDs, BiomeRegistry::getBiomeStringID);
        save.addStringArray("biomeIDs", biomeIDs);
        super.addSaveData(save);
    }

    @Override
    public void loadSaveData(LoadData save) {
        super.loadSaveData(save);
        int[] conversionArray = null;
        if (save.hasLoadDataByName("biomeIDs")) {
            String[] biomeIDs = save.getStringArray("biomeIDs");
            conversionArray = VersionMigration.generateStringIDsArrayConversionArray(biomeIDs, BiomeRegistry.getBiomeStringIDs(), BiomeRegistry.UNKNOWN.getID(), VersionMigration.oldBiomeStringIDs);
        }
        if (conversionArray != null) {
            int i;
            int[] intData = new int[this.data.length];
            for (i = 0; i < intData.length; ++i) {
                intData[i] = this.data[i];
            }
            if (VersionMigration.convertArray(intData, conversionArray)) {
                for (i = 0; i < this.data.length; ++i) {
                    this.data[i] = (short)intData[i];
                }
                Server server = this.level.getServer();
                if (server != null) {
                    server.migratedNames.add("biome data");
                    server.regionsMigrated.add(this.level.getIdentifier(), new Point(this.region.regionX, this.region.regionY));
                } else {
                    System.out.println("Migrated level " + this.level.getIdentifier() + " region " + this.region.regionX + "x" + this.region.regionY + " biome data");
                }
            } else {
                System.out.println("Failed to migrate level " + this.level.getIdentifier() + " region " + this.region.regionX + "x" + this.region.regionY + " biome data");
            }
        }
    }

    @Override
    public void onLayerLoaded() {
    }

    @Override
    public void onLoadingComplete() {
    }

    @Override
    public void onLayerUnloaded() {
    }

    @Override
    protected void handleLoadException(Exception e) {
        throw new RuntimeException("Failed to load level biomes", e);
    }

    @Override
    protected boolean isValidValue(int regionTileX, int regionTileY, int value) {
        return true;
    }
}

