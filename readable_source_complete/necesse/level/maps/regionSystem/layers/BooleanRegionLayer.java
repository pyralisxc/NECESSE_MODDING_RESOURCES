/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.regionSystem.layers;

import java.io.IOException;
import java.util.Arrays;
import necesse.engine.GameLog;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.save.SaveSerialize;
import necesse.level.maps.regionSystem.Region;
import necesse.level.maps.regionSystem.layers.RegionLayer;
import necesse.level.maps.regionSystem.layers.interfaces.RegionPacketHandlerRegionLayer;
import necesse.level.maps.regionSystem.layers.interfaces.SaveDataRegionLayer;

public abstract class BooleanRegionLayer
extends RegionLayer
implements RegionPacketHandlerRegionLayer,
SaveDataRegionLayer {
    protected boolean[] data;

    public BooleanRegionLayer(Region region) {
        super(region);
        this.data = new boolean[region.tileWidth * region.tileHeight];
        boolean defaultValue = this.getDefault();
        if (defaultValue) {
            Arrays.fill(this.data, true);
        }
    }

    protected boolean get(int regionTileX, int regionTileY) {
        return this.data[this.getDataIndex(regionTileX, regionTileY)];
    }

    protected void set(int regionTileX, int regionTileY, boolean value) {
        this.data[this.getDataIndex((int)regionTileX, (int)regionTileY)] = value;
    }

    protected int getDataIndex(int regionTileX, int regionTileY) {
        return regionTileX + regionTileY * this.region.tileWidth;
    }

    protected boolean getDefault() {
        return false;
    }

    @Override
    public void writeLayerPacket(PacketWriter writer) {
        for (boolean value : this.data) {
            writer.putNextBoolean(value);
        }
    }

    @Override
    public boolean applyLayerPacket(PacketReader reader) {
        for (int i = 0; i < this.data.length; ++i) {
            this.data[i] = reader.getNextBoolean();
        }
        return true;
    }

    @Override
    public void addSaveData(SaveData save) {
        try {
            save.addCompressedBooleanArray(this.getSaveDataName(), this.data);
        }
        catch (IOException e) {
            save.addSmallBooleanArray(this.getSaveDataName(), this.data);
        }
    }

    @Override
    public void loadSaveData(LoadData save) {
        try {
            boolean[] newData;
            LoadData load = save.getFirstLoadDataByName(this.getSaveDataName());
            if (load == null) {
                this.handleSaveNotFound();
                return;
            }
            try {
                if (SaveSerialize.isSmallBooleanArray(load.getData())) {
                    throw new Exception("Handle small boolean array");
                }
                newData = LoadData.getCompressedBooleanArray(load);
            }
            catch (Exception e) {
                newData = LoadData.getSmallBooleanArray(load);
            }
            this.data = newData.length != this.data.length ? Arrays.copyOf(newData, this.data.length) : newData;
        }
        catch (Exception e) {
            this.handleLoadException(e);
        }
    }

    protected String getSaveDataName() {
        return this.getStringID();
    }

    protected void handleSaveNotFound() {
        GameLog.warn.println("Could not find " + this.level.getIdentifier() + " " + this.getSaveDataName() + " region data for region " + this.region.regionX + "x" + this.region.regionY);
    }

    protected void handleLoadException(Exception e) {
        System.err.println("Failed to load " + this.level.getIdentifier() + " " + this.getSaveDataName() + " region data in region " + this.region.regionX + "x" + this.region.regionY + ", resulting in a wipe");
        this.data = new boolean[this.region.tileWidth * this.region.tileHeight];
        boolean defaultValue = this.getDefault();
        if (defaultValue) {
            Arrays.fill(this.data, true);
        }
    }
}

